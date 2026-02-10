package com.greenko.telemetryservice.repository;

import com.greenko.telemetryservice.model.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryRepo extends JpaRepository<Telemetry, String> {



}
