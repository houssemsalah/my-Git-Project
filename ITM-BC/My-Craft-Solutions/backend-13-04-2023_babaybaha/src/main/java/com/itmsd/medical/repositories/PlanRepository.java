package com.itmsd.medical.repositories;


import com.itmsd.medical.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository  extends JpaRepository<Plan, Long> {

    Plan findByTitle(String title);
}
