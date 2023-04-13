package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import com.itmsd.medical.entities.Menuisier;
import com.itmsd.medical.entities.Peintre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeintreRepository extends JpaRepository<Peintre, Long>{
	Peintre findByEmail (String email);
	Optional<Peintre> findByUsername(String username);
	List<Peintre> findAllByVille(String ville);
	List<Peintre> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Peintre> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from peintre Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Peintre> findTopPraticien();
}
