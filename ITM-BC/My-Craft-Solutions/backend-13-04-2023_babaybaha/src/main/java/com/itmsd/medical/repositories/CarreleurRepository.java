package com.itmsd.medical.repositories;

import com.itmsd.medical.entities.Carreleur;
import com.itmsd.medical.entities.Plombier;
import com.itmsd.medical.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarreleurRepository extends JpaRepository<Carreleur, Long> {
	Carreleur findByEmail (String email);
	List<Carreleur> findAllByVille(String ville);
	List<Carreleur> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Carreleur> findAllByPostalCode(Integer postalCode);
	Optional<Carreleur> findByUsername(String username);
	@Query(value = "Select * from Carreleur Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Carreleur> findTopPraticien();
}
