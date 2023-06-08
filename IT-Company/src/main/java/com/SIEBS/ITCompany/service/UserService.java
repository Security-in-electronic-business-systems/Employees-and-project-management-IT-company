package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.*;
import com.SIEBS.ITCompany.repository.*;
import lombok.RequiredArgsConstructor;
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

    private final PermissionRepository permissionRepository;

    private final EmployeeProjectRepository employeeProjectsRepository;
    private final SkillRepository skillRepository;

    private final FileRepository fileRepository;

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

    public Optional<User> findByEmail(String email){
        return repository.findByEmail(email);
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

    public UsersResponse createUsersResponse(User user) {
        UsersResponse userDTO = new UsersResponse();
        userDTO.setUserId(user.getId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        return userDTO;
    }
    public List<PermissionDTO> getAllPermission(){
        List<PermissionDTO> permissionDTOS = new ArrayList<>();
        List <Role> roles = roleRepository.findAll();
        List <Permission> permissions = permissionRepository.findAll();
        for (Role role: roles) {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setRole(role.getName());
            permissionDTO.setMethods(new ArrayList<>());
            for (Permission per: permissions) {
                if (per.getRole()==role){
                    permissionDTO.setId(per.getId());
                    permissionDTO.addMethod(per.getPermision());
                }
            }
            permissionDTOS.add(permissionDTO);
        }
        return permissionDTOS;
    }

    public boolean savePermissions(List<PermissionDTO> permissionDTOs) {
        try {
            permissionRepository.deleteAllPer();
            List<Permission> permissions = new ArrayList<>();
            for (PermissionDTO permissionDTO : permissionDTOs) {
                Role role = roleRepository.findByName(permissionDTO.getRole());
                if (role != null) {
                    for (Methods method : permissionDTO.getMethods()) {
                        Permission permission = new Permission();
                        permission.setRole(role);
                        permission.setPermision(method);
                        permissions.add(permission);
                    }
                }
            }
            permissionRepository.saveAll(permissions);
            return true; // Operacija uspešna
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Greška prilikom čuvanja dozvola
        }
    }

    public boolean createSkill(SkillDTO skillDTO) {
        Skill skill = new Skill();
        try {

            Optional<User> userOptional = findByEmail(skillDTO.getEmail());
            User user = userOptional.orElse(null);
            skill.setName(skillDTO.getName());
            skill.setUser(user);
            skill.setGrade(skillDTO.getGrade());

            // Sačuvaj skill
            Skill savedSkill =  skillRepository.save(skill);

            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false; // Greška prilikom čuvanja dozvola
        }

    }

    public List<AllSkillDTO> getSkills(String email) {
        System.out.println("****************************"+email);
        List<Skill> allSkills = skillRepository.findAll();
        List<AllSkillDTO> skills = new ArrayList<>();

        Optional<User> userOptional = findByEmail(email);
        User user = userOptional.orElse(null);

        for (Skill s : allSkills){
            AllSkillDTO dto = new AllSkillDTO();
            if(s.getUser().getId() == user.getId()){
                dto.setId(s.getId());
                dto.setName(s.getName());
                dto.setGrade(s.getGrade());
                skills.add(dto);
            }
        }

        System.out.println("****************************");
        return skills;
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
    public boolean editSkill(AllSkillDTO skillResponse) {
        Optional<Skill> skilOptionall =  skillRepository.findById( Long.valueOf(skillResponse.getId()).intValue());
        Skill skill = skilOptionall.orElse(null);
        if (skill == null) {
            return false;
        }
        skill.setGrade(skillResponse.getGrade());
        skillRepository.save(skill);
        return true;
    }

    public List<File> getFile() {

        List<File> allFiles = fileRepository.findAll();
        return allFiles;
    }

    public void deleteFile(Long id){
        fileRepository.deleteById(Long.valueOf(id).intValue());
    }
    public boolean saveFile(File file) {
        try {

            // Sačuvaj skill
            File save =  fileRepository.save(file);

            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false; // Greška prilikom čuvanja dozvola
        }
    }

    public byte[] getFileBytesById(User user) {
        List<File>files = fileRepository.findAll();
        for (File f: files) {
            if(f.getUser().getId()==user.getId()){
                return f.getFileData();
            }
        }
        return null;
    }

    public List<Project> getEmployeeProjects(User user) {
        List<Project> projects = projectRepository.findAll();
        List<Project> employeeProjects = new ArrayList<>();

        for(Project p: projects){
            List<EmployeeProject> employee = p.getEmployeeProjects();
            if(p.getEmployeeProjects().isEmpty()==false){
                for(EmployeeProject e : employee){
                    if(e.getUser().getId() == user.getId()){
                        employeeProjects.add(p);
                    }
                }
            }
        }

        return employeeProjects;
    }


    public void updateJobDescription(JobDescriptionDTO jobDescriptionDTO) {
        try {
            Optional<EmployeeProject> employeeProjectOptional =  employeeProjectsRepository.findById(jobDescriptionDTO.getId());
            EmployeeProject employeeProject = employeeProjectOptional.orElse(null);
            employeeProject.setJobDescription(jobDescriptionDTO.getJobDescription());
            employeeProjectsRepository.save(employeeProject);

            System.out.println("Opis posla je uspešno ažuriranju .");
        } catch (Exception e) {
            System.out.println("Došlo je do greške pri ažuriranju opisa posla: " + e.getMessage());
        }
    }

    public void editEmployees(EditEmployeeDTO editEmployeeProjectDTO) {
        try {
            Optional<Project> projectOptional =  projectRepository.findById(editEmployeeProjectDTO.getId());
            Project project = projectOptional.orElse(null);
            List<EmployeeProject>oldEmployeeProjects = project.getEmployeeProjects();
            EmployeeProject employeeProject=  new EmployeeProject();
            System.out.println("****************OLD************** "+oldEmployeeProjects.size());

            Optional<User> userOptional = repository.findById(editEmployeeProjectDTO.getUserId());
            User user= userOptional.orElse(null);
            employeeProject.setUser(user);
            employeeProject.setProject(project);
            employeeProject.setJobDescription(editEmployeeProjectDTO.getJobDescription());
            employeeProject.setStartDate(editEmployeeProjectDTO.getStartDate());
            employeeProject.setEndDate(editEmployeeProjectDTO.getEndDate());
            oldEmployeeProjects.add(employeeProject);
            System.out.println("****************NEW************** "+oldEmployeeProjects.size());
            project.setEmployeeProjects(oldEmployeeProjects);
            employeeProjectsRepository.save(employeeProject);
            projectRepository.save(project);

            System.out.println("Opis posla je uspešno ažuriranju .");
        } catch (Exception e) {
            System.out.println("Došlo je do greške pri ažuriranju opisa posla: " + e.getMessage());
        }
    }
}
