package com.SIEBS.ITCompany.controller;

import com.SIEBS.ITCompany.dto.RegistrationRequestResponse;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.*;

import com.SIEBS.ITCompany.repository.PermissionRepository;
import com.SIEBS.ITCompany.repository.SkillRepository;
import com.SIEBS.ITCompany.service.AuthenticationService;
import com.SIEBS.ITCompany.service.EmailService;
import com.SIEBS.ITCompany.service.HmacService;
import com.SIEBS.ITCompany.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthenticationService authService;
    @Autowired
    private final HmacService hmacService;
    @Autowired
    private final NotificationController notificationController;


    @PostMapping("/blockUser")
    public ResponseEntity<MessageResponse> blockUser(@RequestBody ForgotPasswordDTO emailDTO){
        return ResponseEntity.ok(userService.BlockUser(emailDTO.getEmail()));
    }

    @PostMapping("/unblockUser")
    public ResponseEntity<MessageResponse> unblockUser(@RequestBody ForgotPasswordDTO emailDTO){
        return ResponseEntity.ok(userService.UnblockUser(emailDTO.getEmail()));
    }

    @GetMapping("/checkIsForgotPasswordLinkValid")
    public ResponseEntity verifyForgotPasswordLink(
            @RequestParam("token") String token,
            HttpServletResponse response
    ){
        URI changePasswordUri = URI.create("http://localhost:3000/changeForgotPassword/" + token);
        URI tokenExpiredUri = URI.create("http://localhost:3000/token-expired");

        if(!userService.isTokenFromForgotPasswordLinkValid(token)){
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(tokenExpiredUri)
                    .body(null);
        }else{
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(changePasswordUri)
                    .body(null);
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO){
        return ResponseEntity.ok(userService.SendMailForForgotPassword(forgotPasswordDTO.getEmail()));
    }

    @PostMapping("/changeForgottenPassword")
    public ResponseEntity<MessageResponse> changeForgottenPassword(@RequestBody ChangeForgottenPasswordDTO changeForgottenPasswordDTO){
        return ResponseEntity.ok(userService.ChangeForgottenPassword(changeForgottenPasswordDTO));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        return ResponseEntity.ok(userService.ChangePassword(changePasswordDTO));
    }

    @PostMapping("/send")
    public String sendMail(@RequestParam(value = "file", required = false)MultipartFile[] file, String to, String subject, String body){
        return emailService.sendMail(to, subject, body);
    }


    @GetMapping("/registration/requests")
    public List<RegistrationRequestResponse> getRegistrationRequests() throws Exception {
        List<User> users = userService.getRegistrationRequests();

        List<RegistrationRequestResponse> usersResponse = users.stream()
                .map(user -> RegistrationRequestResponse.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .isApproved(user.isApproved())
                        .title(user.getTitle())
                        .role(new RoleDTO(user.getRole().getId(), user.getRole().getName()))
                        .build())
                .collect(Collectors.toList());
        return usersResponse;
    }
    @PreAuthorize("@permissionService.hasPermission('GET_ALL_USERS')")
    @GetMapping("/getAll")
    public ResponseEntity<List<UsersResponse>> getAllUsers() throws Exception {
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
                        .role(new RoleDTO(user.getRole().getId(), user.getRole().getName()))
                        .isBlocked(user.isBlocked())
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
            log.info("Project created successfully. Name: " + projectDTO.getName());
            MessageResponse response = new MessageResponse("Project created successfully.");
            return ResponseEntity.ok(response);
        } else {
            log.error("Error creating project. Name: " + projectDTO.getName());
            MessageResponse response = new MessageResponse("Error creating project.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/registration/decline")
    public ResponseEntity<MessageResponse> registrationDecline(@RequestBody RegistrationDecline request){
        userService.updateRegistrationDate(request.getEmail(), new Date());
        String response = emailService.sendMail(request.getEmail(), "Registration request - DECLINED", request.getDescription());
        if (response == "Success!"){
            log.info("Registration request - DECLINED" + authService.removeDangerousCharacters(request.getDescription()));
            MessageResponse message = new MessageResponse("Registration request successfully declined.");
            return ResponseEntity.ok(message);
        }else{
            log.error("Error declining registration request." + authService.removeDangerousCharacters(request.getEmail()));
            MessageResponse message = new MessageResponse("Error declining registration request.");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/registration/hmac")
    public ResponseEntity<MessageResponse> checkIntegrity(@RequestBody HmacRequest request){
        String code = hmacService.generateHmac(request.getPhoneNumber(), request.getLink());
        if (code.equals(request.getCode())){
            log.info("HMAC success - SAFE. Phone: " + authService.removeDangerousCharacters(request.getPhoneNumber()));
            MessageResponse message = new MessageResponse("SAFE.");
            return ResponseEntity.ok(message);
        }else{
            log.error("HMAC failed - UNSAFE. Phone: " + authService.removeDangerousCharacters(request.getPhoneNumber()));
            MessageResponse message = new MessageResponse("UNSAFE!");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/registration/accept")
    public ResponseEntity<MessageResponse> registrationAccept(@RequestBody RegistrationAccept request) throws Exception {
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
            log.info("Registration request successfully accepted. Email:" + authService.removeDangerousCharacters(request.getEmail()));
            MessageResponse message = new MessageResponse("Registration request successfully accepted.");
            return ResponseEntity.ok(message);
        }else{
            log.error("Error accepting registration request. Email: " + authService.removeDangerousCharacters(request.getEmail()));
            MessageResponse message = new MessageResponse("Error accepting registration request.");
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PreAuthorize("@permissionService.hasPermission('GET_USER')")
    @GetMapping("/get")
    public ResponseEntity<UsersResponse> get() {
        User user = authService.getLoggedUser();

        UsersResponse usersResponse = UsersResponse.builder()
                .userId(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .title(user.getTitle())
                .address(user.getAddress())
                .role(new RoleDTO(user.getRole().getId(), user.getRole().getName()))
                .build();

        return ResponseEntity.ok(usersResponse);
    }
    @PreAuthorize("@permissionService.hasPermission('UPDATE_USER')")
    @PutMapping("/update")
    public ResponseEntity<MessageResponse> updateUser(@RequestBody UsersResponse usersResponse) {
        boolean isUpdated = userService.updateUser(usersResponse);

        if (isUpdated) {
            log.info("User updated successfully." + authService.removeDangerousCharacters(usersResponse.getEmail()));
            MessageResponse response = new MessageResponse("User updated successfully.");
            return ResponseEntity.ok(response);
        } else {
            log.error("User updated unsuccessfully." + authService.removeDangerousCharacters(usersResponse.getEmail()));
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
            log.info("Permissions saved successfully. Admin:" + authService.removeDangerousCharacters(authService.getLoggedUser().getEmail()));
            MessageResponse response = new MessageResponse("Permissions saved successfully.");
            notificationController.createNotification("Permissions saved successfully. Admin:" + authService.removeDangerousCharacters(authService.getLoggedUser().getEmail()));
            return ResponseEntity.ok(response);
        } else {
            log.info("Error saving permissions.Admin:" + authService.removeDangerousCharacters(authService.getLoggedUser().getEmail()));
            MessageResponse response = new MessageResponse("Error saving permissions.Admin:" + authService.removeDangerousCharacters(authService.getLoggedUser().getEmail()));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("@permissionService.hasPermission('ADD_SKILL')")
    @PostMapping("/addSkill")
    public ResponseEntity<MessageResponse> saveSkill(@RequestBody SkillDTO skill) {
        boolean isSaved = userService.createSkill(skill);

        if (isSaved) {
            log.info("Skill saved successfully:" + authService.removeDangerousCharacters(skill.getName()));
            MessageResponse response = new MessageResponse("Skill saved successfully.");
            return ResponseEntity.ok(response);
        } else {
            log.warn("Error saving skill: " + authService.removeDangerousCharacters(skill.getName()));
            MessageResponse response = new MessageResponse("Error saving skill.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("@permissionService.hasPermission('GET_ALL_SKILL')")
    @GetMapping("/getAllSkill")
    @ResponseBody
    public ResponseEntity<List<AllSkillDTO>> getSkillForUser() throws Exception {
        User user = authService.getLoggedUser();
        return ResponseEntity.ok(userService.getSkills(user.getEmail()));
    }

    @PreAuthorize("@permissionService.hasPermission('EDIT_SKILL')")
    @PutMapping("/editSkill")
    @ResponseBody
    public ResponseEntity<MessageResponse> editSkill(@RequestBody AllSkillDTO allSkillDTO) {
        boolean isUpdated = userService.editSkill(allSkillDTO);
        if (isUpdated) {
            log.info("Skill updated successfully." + authService.removeDangerousCharacters(allSkillDTO.getName()));
            MessageResponse response = new MessageResponse("Skill updated successfully.");
            return ResponseEntity.ok(response);
        } else {
            log.warn("Error updating skill." + authService.removeDangerousCharacters(allSkillDTO.getName()));
            MessageResponse response = new MessageResponse("Error updating skill.");
            return ResponseEntity.badRequest().body(response);
        }

    }
    @PreAuthorize("@permissionService.hasPermission('ADD_CV')")
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            // Čitanje sadržaja fajla
            byte[] fileData = multipartFile.getBytes();
            User user = authService.getLoggedUser();


            // Čuvanje fajla u bazi podataka
            File fileEntity = new File();
            fileEntity.setUser(user);
            fileEntity.setFileData(fileData);
            List<File> allFiles = userService.getFile();
            for(File f: allFiles){
                if(f.getUser().getId() == user.getId()){
                    userService.deleteFile(f.getId());
                }
            }

            userService.saveFile(fileEntity);
            log.info("CV upload successfully. Email: " + authService.removeDangerousCharacters(user.getEmail()));
            MessageResponse response = new MessageResponse("File upload successfully.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.info("CV upload unsuccessfully. Email: " + authService.removeDangerousCharacters(authService.getLoggedUser().getEmail()));
            MessageResponse response = new MessageResponse("Error uploading.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("@permissionService.hasPermission('DOWNLOAD_CV')")
    @GetMapping("/download")
    public ResponseEntity<byte[]>  downloadFile() throws IOException {
        User user = authService.getLoggedUser();
        byte[] fileBytes = userService.getFileBytesById(user);

        if (fileBytes == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file.pdf"); // Set the desired file name

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
    @PreAuthorize("@permissionService.hasPermission('GET_MANAGER_PROJECTS')")
    @GetMapping("/getManagerProjects")
    public ResponseEntity<List<ProjectDTO>> getEmployeeProjects() {
        User user = authService.getLoggedUser();
        List<Project> projects = userService.getEmployeeProjects(user);
        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ProjectDTO> projectsDTO = new ArrayList<>();

        for(Project p: projects){
            List<EmployeeProject>eProjects = p.getEmployeeProjects();
            for(EmployeeProject e: eProjects){
                if(e.getUser().getId() == user.getId()){
                             projectsDTO = projects.stream()
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
            }
        }

        return ResponseEntity.ok(projectsDTO);
    }

    @PreAuthorize("@permissionService.hasPermission('EDIT_JOB_DESCRIPTION')")
    @PutMapping("/editJobDescription")
    @ResponseBody
    public void editJobDescription(@RequestBody JobDescriptionDTO jobDescriptionDTO) {
        userService.updateJobDescription(jobDescriptionDTO);
    }

    @PreAuthorize("@permissionService.hasPermission('GET_EMPLOYEE_PROJECTS')")
    @GetMapping("/getEmployeProjects")
    public ResponseEntity<List<ProjectDTO>> getManagerProjects() {
        User user = authService.getLoggedUser();
        List<Project> projects = userService.getEmployeeProjects(user);
        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ProjectDTO> projectsDTO = new ArrayList<>();

        for(Project p: projects){
            List<EmployeeProject>eProjects = p.getEmployeeProjects();
            for(EmployeeProject e: eProjects){
                    projectsDTO = projects.stream()
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

                List<ProjectDTO>retProject = new ArrayList<>();
                for(ProjectDTO dto : projectsDTO) {
                    List<EmployeeProjectDTO> employeDto =  new ArrayList<>();
                    for (EmployeeProjectDTO emplyee : dto.getEmployeeProjects()) {
//                        System.out.println("========" + emplyee.getUser().getFirstname() + emplyee.getUser().getUserId());
//                        System.out.println("====USER====" + user.getFirstname() + user.getId());


                        if (emplyee.getUser().getUserId().equals(user.getId())) {
                            employeDto.add(emplyee);
//                            System.out.println("========" + e.getUser().getFirstname());
                            dto.setEmployeeProjects(employeDto);
                            retProject.add(dto);
                        }
                    }
                }
                    return ResponseEntity.ok(retProject);
                }
        }
            return ResponseEntity.ok(projectsDTO);
    }

    @PreAuthorize("@permissionService.hasPermission('EDIT_EMPLOYEE_ON_PROJECT')")
    @PutMapping("/editEmployessOnProject")
    @ResponseBody
    public void editEmployessOnProject(@RequestBody EditEmployeeDTO editEmployeeDTO) {
        userService.editEmployees(editEmployeeDTO);
    }

    @PostMapping("/search")
    public ResponseEntity<List<UsersResponse>> search(@RequestBody SearchDTO request) throws Exception {
        List<User> users =userService.search(request);
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
                        .role(new RoleDTO(user.getRole().getId(), user.getRole().getName()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersResponse);
    }

    @GetMapping("/findByEmail")
    public RegistrationRequestResponse findByEmail() throws Exception {
        Optional<User> user = userService.findByEmail("pavle@gmail.com");

        RegistrationRequestResponse usersResponse = RegistrationRequestResponse.builder()
                        .firstname(user.get().getFirstname())
                        .lastname(user.get().getLastname())
                        .email(user.get().getEmail())
                        .phoneNumber(user.get().getPhoneNumber())
                        .isApproved(user.get().isApproved())
                        .title(user.get().getTitle())
                        .role(new RoleDTO(user.get().getRole().getId(), user.get().getRole().getName()))
                        .build();
        return usersResponse;
    }

}
