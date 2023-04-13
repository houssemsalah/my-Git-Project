package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Menuisier;

@Repository
public interface MenuisierRepository extends JpaRepository<Menuisier, Long> {
	Menuisier findByEmail (String email);
	Optional<Menuisier> findByUsername(String username);
	Menuisier findMenuisierById (Long id);
	List<Menuisier> findAllBySpeciality(String speciality);
	List<Menuisier> findAllBySpecialityAndPostalCode(String speciality, Integer postalCode);
	List<Menuisier> findAllBySpecialityAndVille(String speciality, String ville);
	List<Menuisier> findAllBySpecialityAndVilleAndPostalCode(String speciality, String ville, Integer postalCode);
	List<Menuisier> findAllByVille(String ville);
	List<Menuisier> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Menuisier> findAllByPostalCode(Integer postalCode);
	@Query(value = "Select * from Menuisier Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Menuisier> findTopMenuisier();
	
}

