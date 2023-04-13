package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Session;
import java.util.List;
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByScheduleId(long ScheduleId);
}
