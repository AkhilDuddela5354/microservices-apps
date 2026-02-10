package com.greenko.telemetryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Telemetry {

    @Id
    private String id;
    private String assetId;
    private double power;
    private LocalDateTime recordedAt;
    private double temperature;

}
