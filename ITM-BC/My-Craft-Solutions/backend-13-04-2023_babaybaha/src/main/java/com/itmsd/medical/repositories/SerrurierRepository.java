package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Serrurier;

@Repository
public interface SerrurierRepository extends JpaRepository<Serrurier, Long> {
    Serrurier findByEmail (String email);
    Optional<Serrurier> findByUsername(String username);
    Serrurier findSerrurierById (Long id);
    List<Serrurier> findAllBySpeciality(String speciality);
    List<Serrurier> findAllBySpecialityAndPostalCode(String speciality, Integer postalCode);
    List<Serrurier> findAllBySpecialityAndVille(String speciality, String ville);
    List<Serrurier> findAllBySpecialityAndVilleAndPostalCode(String speciality, String ville, Integer postalCode);
    List<Serrurier> findAllByVille(String ville);
    List<Serrurier> findAllByVilleAndPostalCode(String ville, Integer postalCode);
    List<Serrurier> findAllByPostalCode(Integer postalCode);
    @Query(value = "Select * from Serrurier Order by nb_rdv desc LIMIT 1",
            nativeQuery = true)
    List<Serrurier> findTopSerrurier();

}

