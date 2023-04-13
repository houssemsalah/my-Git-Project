package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import com.itmsd.medical.entities.Menuisier;
import com.itmsd.medical.entities.Plombier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlombierRepository extends JpaRepository<Plombier, Long> {
	Plombier findByEmail (String email);
	List<Plombier> findAllByVille(String ville);
	List<Plombier> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Plombier> findAllByPostalCode(Integer postalCode);
	Optional<Plombier> findByUsername(String username);
	@Query(value = "Select * from plombier Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Plombier> findTopPlombier();
}
