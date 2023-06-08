package com.SIEBS.ITCompany.repository;

import com.SIEBS.ITCompany.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  List<User> findAll();
  List<User> findByIsApprovedFalse();
  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.registrationDate = :newDate WHERE u.email = :email")
  void updateRegistrationDate(@Param("email") String email, @Param("newDate") Date newValue);

  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.isApproved = :isApproved WHERE u.email = :email")
  void updateIsApproved(@Param("email") String email, @Param("isApproved") boolean isApproved);

  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.firstname = :#{#user.firstname},u.lastname = :#{#user.lastname},u.password = :#{#user.password},u.phoneNumber = :#{#user.phoneNumber},u.registrationDate = :#{#user.registrationDate},u.address = :#{#user.address},u.role = :#{#user.role},u.title = :#{#user.title}  WHERE u.email = :#{#user.email}")
  void update(@Param("user") User user);


}
