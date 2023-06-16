package com.SIEBS.ITCompany.model;

import com.SIEBS.ITCompany.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String phoneNumber;
  private boolean isApproved;
  private boolean isBlocked;
  @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
  private Date registrationDate;
  private String title;
  @OneToOne
  private Address address;
  private boolean isUsing2FA;
  private String secret;

  @ManyToOne
  private Role role;


  @OneToMany(mappedBy = "user")
  @Fetch(FetchMode.JOIN)
  private List<UserRole> roles;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;


  @OneToMany(mappedBy = "user")
  @Fetch(FetchMode.JOIN)
  private List<EmployeeProject> employeeProjects;
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.getName()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public User(Integer id, String firstname, String lastname, String email, String password, String phoneNumber, boolean isApproved, Date registrationDate, String title, Address address, boolean isUsing2FA, String secret, Role role, List<UserRole> roles, List<Token> tokens, List<EmployeeProject> employeeProjects) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.isApproved = isApproved;
    this.registrationDate = registrationDate;
    this.title = title;
    this.address = address;
    this.isUsing2FA = isUsing2FA;
    this.secret = Base32.random();
    this.role = role;
    this.roles = roles;
    this.tokens = tokens;
    this.employeeProjects = employeeProjects;
  }
}
