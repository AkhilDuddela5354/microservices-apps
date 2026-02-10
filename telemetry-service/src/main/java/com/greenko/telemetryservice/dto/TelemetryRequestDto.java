package com.greenko.telemetryservice.dto;

public record TelemetryRequestDto(
        String assetId,
        double power,
        double temperature
) {
}
