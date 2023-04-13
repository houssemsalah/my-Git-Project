package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Chauffagiste;

@Repository
public interface ChauffagisteRepository extends JpaRepository<Chauffagiste, Long> {
    Chauffagiste findByEmail (String email);
    Optional<Chauffagiste> findByUsername(String username);
    Chauffagiste findChauffagisteById (Long id);
    List<Chauffagiste> findAllBySpeciality(String speciality);
    List<Chauffagiste> findAllBySpecialityAndPostalCode(String speciality, Integer postalCode);
    List<Chauffagiste> findAllBySpecialityAndVille(String speciality, String ville);
    List<Chauffagiste> findAllBySpecialityAndVilleAndPostalCode(String speciality, String ville, Integer postalCode);
    List<Chauffagiste> findAllByVille(String ville);
    List<Chauffagiste> findAllByVilleAndPostalCode(String ville, Integer postalCode);
    List<Chauffagiste> findAllByPostalCode(Integer postalCode);
    @Query(value = "Select * from Chauffagiste Order by nb_rdv desc LIMIT 1",
            nativeQuery = true)
    List<Chauffagiste> findTopChauffagiste();

}

