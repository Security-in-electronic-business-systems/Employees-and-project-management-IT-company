package com.SIEBS.ITCompany.controller;

import com.SIEBS.ITCompany.dto.RegistrationRequestResponse;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.EmployeeProject;
import com.SIEBS.ITCompany.model.Project;
import com.SIEBS.ITCompany.model.User;

import com.SIEBS.ITCompany.repository.PermissionRepository;
import com.SIEBS.ITCompany.service.AuthenticationService;
import com.SIEBS.ITCompany.service.EmailService;
import com.SIEBS.ITCompany.service.HmacService;
import com.SIEBS.ITCompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthenticationService authService;
    @Autowired
    private final HmacService hmacService;


    @PostMapping("/send")
    public String sendMail(@RequestParam(value = "file", required = false)MultipartFile[] file, String to, String subject, String body){
        return emailService.sendMail(to, subject, body);
    }


    @GetMapping("/registration/requests")
    public List<RegistrationRequestResponse> getRegistrationRequests(){
        List<User> users = userService.getRegistrationRequests();

        List<RegistrationRequestResponse> usersResponse = users.stream()
                .map(user -> RegistrationRequestResponse.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .isApproved(user.isApproved())
                        .title(user.getTitle())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
        return usersResponse;
    }
    @PreAuthorize("@permissionService.hasPermission('GET_ALL_USERS')")
    @GetMapping("/getAll")
    public ResponseEntity<List<UsersResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<UsersResponse> usersResponse = users.stream()
                .map(user -> UsersResponse.builder()
                        .userId(user.getId())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .title(user.getTitle())
                        .address(user.getAddress())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersResponse);
    }
    @PreAuthorize("@permissionService.hasPermission('GET_ALL_PROJECTS')")
    @GetMapping("/getAllProjects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = userService.getAllProjects();

        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProjectDTO> projectsDTO = projects.stream()
                .map(project -> {
                    ProjectDTO projectDTO = new ProjectDTO();
                    projectDTO.setId(project.getId());
                    projectDTO.setName(project.getName());
                    projectDTO.setStartDate(project.getStartDate());
                    projectDTO.setEndDate(project.getEndDate());
                    projectDTO.setDescription(project.getDescription());

                    List<EmployeeProject> employeeProjects = project.getEmployeeProjects();
                    List<EmployeeProjectDTO> employeeProjectsDTO = employeeProjects.stream()
                            .map(employeeProject -> {
                                EmployeeProjectDTO employeeProjectDTO = new EmployeeProjectDTO();
                                employeeProjectDTO.setID(employeeProject.getId());
                                employeeProjectDTO.setUser(userService.createUsersResponse(employeeProject.getUser()));
                                employeeProjectDTO.setJobDescription(employeeProject.getJobDescription());
                                employeeProjectDTO.setStartDate(employeeProject.getStartDate());
                                employeeProjectDTO.setEndDate(employeeProject.getEndDate());
                                return employeeProjectDTO;
                            })
                            .collect(Collectors.toList());

                    projectDTO.setEmployeeProjects(employeeProjectsDTO);
                    return projectDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(projectsDTO);
    }

    @PreAuthorize("@permissionService.hasPermission('CREATE_PROJECT')")
    @PostMapping("/createProject")
    public ResponseEntity<MessageResponse> createProject(@RequestBody ProjectDTO projectDTO) {
        Project savedProject = userService.createProject(projectDTO);

        if (savedProject != null) {
            MessageResponse response = new MessageResponse("Project created successfully.");
            return ResponseEntity.ok(response);
        } else {
            MessageResponse response = new MessageResponse("Error creating project.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/registration/decline")
    public ResponseEntity<MessageResponse> registrationDecline(@RequestBody RegistrationDecline request){
        userService.updateRegistrationDate(request.getEmail(), new Date());
        String response = emailService.sendMail(request.getEmail(), "Registration request - DECLINED", request.getDescription());
        if (response == "Success!"){
            MessageResponse message = new MessageResponse("Registration request successfully declined.");
            return ResponseEntity.ok(message);
        }else{
            MessageResponse message = new MessageResponse("Error declining registration request.");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/registration/hmac")
    public ResponseEntity<MessageResponse> checkIntegrity(@RequestBody HmacRequest request){
        String code = hmacService.generateHmac(request.getPhoneNumber(), request.getLink());
        if (code.equals(request.getCode())){
            MessageResponse message = new MessageResponse("SAFE.");
            return ResponseEntity.ok(message);
        }else{
            MessageResponse message = new MessageResponse("UNSAFE!");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/registration/accept")
    public ResponseEntity<MessageResponse> registrationAccept(@RequestBody RegistrationAccept request){
        String link = authService.generateTokenForRegistration(request.getEmail());
        Optional<User> user = userService.findByEmail(request.getEmail());
        String secretKey;
        if (user.isPresent()){
            secretKey = user.get().getPhoneNumber();
        }else{
            MessageResponse message = new MessageResponse("Error user does not exist!");
            return ResponseEntity.badRequest().body(message);
        }
        String code = hmacService.generateHmac(secretKey, link);

        StringBuilder htmlBody = new StringBuilder();
        htmlBody.append("<p>Poštovani,</p>");
        htmlBody.append("<p>Vaš zahtjev za registraciju je uspjesno odbren od strane admine.</p>");
        htmlBody.append("<p>Pratite navedene korake da bi provjerili integritet pristiglih podataka <br>i uspjesno izvrsili verifikaciju vaseg profila.</p>");
        htmlBody.append("<p><b>1.</b> Kliknite na link: <link>https://localhost:3000/hmac</link></p>");
        htmlBody.append("<p><b>2.</b> Kopirajte <b>HMAC code</b> : <i>").append(code).append("</i> i unesite u formu.</p>");
        htmlBody.append("<p><b>3.</b> Kopirajte <b>verifikacioni link</b> : <i>").append(link).append("</i> i unesite u formu.</p>");
        htmlBody.append("<p><b>4.</b> Unesite svoju lozinku u formu.</p>");
        htmlBody.append("<p><b>5.</b> Kliknite SUBMIT.</p>");
        htmlBody.append("<p><b>5.</b> Ako je verifikacija uspjesna pristupite verifikacionom linku.</p>");
        htmlBody.append("<p><b>5.</b> Ako je verifikacija nije uspjesna obratite se korisnickoj podrscida Vam posalju novi link.</p>");
        htmlBody.append("<hr>");
        htmlBody.append("<p></p>");

        String response = emailService.sendMail(request.getEmail(), "Registration request - ACCEPTED", htmlBody.toString());
        if (response == "Success!"){
            MessageResponse message = new MessageResponse("Registration request successfully accepted.");
            return ResponseEntity.ok(message);
        }else{
            MessageResponse message = new MessageResponse("Error accepting registration request.");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PreAuthorize("@permissionService.hasPermission('GET_USER')")
    @GetMapping("/get")
    public ResponseEntity<UsersResponse> get() {
        User user = userService.findById(152);

        UsersResponse usersResponse = UsersResponse.builder()
                .userId(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .title(user.getTitle())
                .address(user.getAddress())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(usersResponse);
    }
    @PreAuthorize("@permissionService.hasPermission('UPDATE_USER')")
    @PutMapping("/update")
    public ResponseEntity<MessageResponse> updateUser(@RequestBody UsersResponse usersResponse) {
        boolean isUpdated = userService.updateUser(usersResponse);

        if (isUpdated) {
            MessageResponse response = new MessageResponse("User updated successfully.");
            return ResponseEntity.ok(response);
        } else {
            MessageResponse response = new MessageResponse("Error updating user.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDTO>> getPermissions() {
        return ResponseEntity.ok(userService.getAllPermission());
    }

    @PostMapping("/permissions")
    public ResponseEntity<MessageResponse> savePermissions(@RequestBody List<PermissionDTO> permissionDTOList) {
        boolean isSaved = userService.savePermissions(permissionDTOList);

        if (isSaved) {
            MessageResponse response = new MessageResponse("Permissions saved successfully.");
            return ResponseEntity.ok(response);
        } else {
            MessageResponse response = new MessageResponse("Error saving permissions.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
