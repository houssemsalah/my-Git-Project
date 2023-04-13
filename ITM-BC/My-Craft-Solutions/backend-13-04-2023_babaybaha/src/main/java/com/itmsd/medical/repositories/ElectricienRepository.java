package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import com.itmsd.medical.entities.Electricien;
import com.itmsd.medical.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricienRepository extends JpaRepository<Electricien, Long>{
	Electricien findByEmail (String email);
	List<Electricien> findAllByVille(String ville);
	List<Electricien> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Electricien> findAllByPostalCode(Integer postalCode);
	Optional<Electricien> findByUsername(String username);
	@Query(value = "Select * from electricien Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Electricien> findTopPraticien();
}
