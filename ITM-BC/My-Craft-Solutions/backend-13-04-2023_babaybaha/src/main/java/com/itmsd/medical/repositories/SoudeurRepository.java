package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import com.itmsd.medical.entities.Menuisier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Soudeur;

@Repository
public interface SoudeurRepository extends JpaRepository<Soudeur, Long> {
	Soudeur findByEmail (String email);
	Optional<Soudeur> findByUsername(String username);
	List<Soudeur> findAllByVille(String ville);
	List<Soudeur> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Soudeur> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from soudeur Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Soudeur> findTopPraticien();
}
