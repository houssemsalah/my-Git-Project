package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.RendezVous;

import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long>{
    List<RendezVous> findByPersonnelId(long personnelId);
    List<RendezVous> findByClientId(long clientId);


}
