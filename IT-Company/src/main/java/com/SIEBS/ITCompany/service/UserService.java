package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.*;
import com.SIEBS.ITCompany.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ProjectRepository projectRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmployeeProjectRepository employeeProjectsRepository;

    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        return users;
    }

    public List<User> getRegistrationRequests() {
        List<User> users = repository.findByIsApprovedFalse();
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users){
            if (user.getRegistrationDate()==null){
                filteredUsers.add(user);
            }else{
                Date checkDate = new Date(user.getRegistrationDate().getTime() + 10 * 60 * 1000);
                Date now = new Date();
                if (checkDate.before(now)){
                    filteredUsers.add(user);
                }
            }
        }
        return filteredUsers;
    }
    public void updateRegistrationDate(String email, Date newDate) {
        try {
            repository.updateRegistrationDate(email, newDate);
            System.out.println("Korisnik je uspešno ažuriran.");
        } catch (Exception e) {
            System.out.println("Došlo je do greške pri ažuriranju korisnika: " + e.getMessage());
        }
    }

    public void approveUser(String email){
        repository.updateIsApproved(email, true);
    }


    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    public User findById(Integer id) {
        Optional<User> optionalUser = repository.findById(id);
        return optionalUser.orElse(null);
    }

    public Project createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setDescription(projectDTO.getDescription());

        // Sačuvaj projekat
        Project savedProject = projectRepository.save(project);

        List<EmployeeProjectDTO> employeeProjectsDTO = projectDTO.getEmployeeProjects();
        List<EmployeeProject> employeeProjects = employeeProjectsDTO.stream()
                .map(employeeProjectDTO -> {
                    EmployeeProject employeeProject = new EmployeeProject();
                    employeeProject.setProject(savedProject); // Koristi sačuvani projekat
                    User user = findById(employeeProjectDTO.getUser().getUserId());
                    employeeProject.setUser(user);
                    employeeProject.setJobDescription(employeeProjectDTO.getJobDescription());
                    employeeProject.setStartDate(employeeProjectDTO.getStartDate());
                    employeeProject.setEndDate(employeeProjectDTO.getEndDate());
                    return employeeProjectsRepository.save(employeeProject); // Čuvanje EmployeeProject entiteta
                })
                .collect(Collectors.toList());

        savedProject.setEmployeeProjects(employeeProjects);

        return savedProject;
    }

    public boolean updateUser(UsersResponse usersResponse) {
        Optional<User> userOptional = repository.findByEmail(usersResponse.getEmail());
        User user = userOptional.orElse(null);
        if (user == null) {
            return false;
        }
        Role role = roleRepository.findByName(usersResponse.getRole().getName());
        user.setFirstname(usersResponse.getFirstname());
        user.setEmail(usersResponse.getEmail());
        user.setLastname(usersResponse.getLastname());
        user.setPhoneNumber(usersResponse.getPhoneNumber());
        user.setTitle(usersResponse.getTitle());
        user.setRole(role);

        // Azuriranje podataka o adresi
        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setCountry(usersResponse.getAddress().getCountry());
        address.setCity(usersResponse.getAddress().getCity());
        address.setStreet(usersResponse.getAddress().getStreet());
        address.setNumber(usersResponse.getAddress().getNumber());
        user.setAddress(address);
        addressRepository.save(address);
        User updatedUser = repository.save(user);
        return updatedUser != null;
    }


    public MessageResponse ChangePassword(ChangePasswordDTO changePasswordDTO) {
        User user = repository.findByEmail(changePasswordDTO.getEmail()).orElse(null);
        if(user == null){
            return MessageResponse.builder().message("User not found!").build();
        }

        if(passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            repository.update(user);
            return MessageResponse.builder().message("Successfully!").build();
        }else{
            return MessageResponse.builder().message("Old password is not correct!").build();
        }
    }
}
