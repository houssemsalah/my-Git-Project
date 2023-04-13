package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import com.itmsd.medical.entities.Menuisier;
import com.itmsd.medical.entities.Paysagiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaysagisteRepository extends JpaRepository<Paysagiste, Long> {
	Paysagiste findByEmail (String email);
	Optional<Paysagiste> findByUsername(String username);
	List<Paysagiste> findAllByVille(String ville);
	List<Paysagiste> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Paysagiste> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from paysagiste Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Paysagiste> findTopPraticien();
}
