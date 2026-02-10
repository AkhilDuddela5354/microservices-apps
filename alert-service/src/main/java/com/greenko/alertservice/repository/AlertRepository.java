package com.greenko.alertservice.repository;

import com.greenko.alertservice.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatus(String status);
    List<Alert> findByTargetService(String targetService);
    List<Alert> findBySeverity(String severity);
}
