package com.SIEBS.ITCompany.controller;

import com.SIEBS.ITCompany.dto.RegistrationRequestResponse;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.EmployeeProject;
import com.SIEBS.ITCompany.model.Project;
import com.SIEBS.ITCompany.model.User;

import com.SIEBS.ITCompany.service.EmailService;
import com.SIEBS.ITCompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final UserService userService;



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
                                employeeProjectDTO.setUser(createUsersResponse(employeeProject.getUser()));  // Konverzija User objekta u UserDTO
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

    private UsersResponse createUsersResponse(User user) {
        UsersResponse userDTO = new UsersResponse();
        userDTO.setUserId(user.getId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        return userDTO;
    }

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

    @PostMapping("/registration/accept")
    public ResponseEntity<MessageResponse> registrationAccept(@RequestBody RegistrationAccept request){
        userService.updateRegistrationDate(request.getEmail(), new Date());
        userService.approveUser(request.getEmail());
        String response = emailService.sendMail(request.getEmail(), "Registration request - ACCEPTED", "LINK DODAJ");
        if (response == "Success!"){
            MessageResponse message = new MessageResponse("Registration request successfully accepted.");
            return ResponseEntity.ok(message);
        }else{
            MessageResponse message = new MessageResponse("Error accepting registration request.");
            return ResponseEntity.badRequest().body(message);
        }
    }


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
}
