package com.SIEBS.ITCompany.model;

import com.SIEBS.ITCompany.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
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
  private String title;
  @OneToOne
  private Address address;

  @ManyToOne
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @OneToMany(mappedBy = "user")
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
}
