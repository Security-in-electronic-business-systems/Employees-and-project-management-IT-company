package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.EmployeeProjectDTO;
import com.SIEBS.ITCompany.dto.ProjectDTO;
import com.SIEBS.ITCompany.dto.UsersResponse;
import com.SIEBS.ITCompany.model.*;
import com.SIEBS.ITCompany.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private final EmployeeProjectRepository employeeProjectsRepository;

    public List<User> getAllUsers() {
        List<User> users = repository.findAll();
        return users;
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




}
