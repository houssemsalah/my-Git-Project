package com.itmsd.medical.repositories;

import com.itmsd.medical.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{
	Client findByEmail (String email);
	Optional<Client> findByUsername(String username);
}
