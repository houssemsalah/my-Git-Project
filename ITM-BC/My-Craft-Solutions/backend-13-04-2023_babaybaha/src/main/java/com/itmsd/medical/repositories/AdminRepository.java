package com.itmsd.medical.repositories;

import com.itmsd.medical.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Admin;

import java.util.Optional;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Admin findByEmail(String email);
	Optional<Admin> findByUsername(String username);
}
