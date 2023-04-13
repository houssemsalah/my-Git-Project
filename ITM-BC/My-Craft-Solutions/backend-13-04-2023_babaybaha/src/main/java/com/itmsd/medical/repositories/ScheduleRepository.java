package com.itmsd.medical.repositories;

import com.itmsd.medical.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    //List<Schedule> findByUser(User u);

    List<Schedule> findById(long schedule_id) ;

}