package com.greenko.alertservice.service;

import com.greenko.alertservice.dto.AlertRequestDto;
import com.greenko.alertservice.dto.AlertResponseDto;
import com.greenko.alertservice.model.Alert;
import com.greenko.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public AlertResponseDto createAlert(AlertRequestDto request) {
        Alert alert = new Alert();
        alert.setTitle(request.getTitle());
        alert.setMessage(request.getMessage());
        alert.setSeverity(request.getSeverity());
        alert.setTargetService(request.getTargetService());
        alert.setStatus("PENDING");

        Alert savedAlert = alertRepository.save(alert);
        log.info("Created alert: {} for service: {}", savedAlert.getId(), savedAlert.getTargetService());

        // Simulate sending alert (in real scenario, you would call the target service)
        try {
            sendAlertToService(savedAlert);
            savedAlert.setStatus("SENT");
            savedAlert.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send alert: {}", e.getMessage());
            savedAlert.setStatus("FAILED");
            savedAlert.setErrorMessage(e.getMessage());
        }

        alertRepository.save(savedAlert);
        return toResponseDto(savedAlert);
    }

    public List<AlertResponseDto> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<AlertResponseDto> getAlertsByStatus(String status) {
        return alertRepository.findByStatus(status).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<AlertResponseDto> getAlertsByService(String service) {
        return alertRepository.findByTargetService(service).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<AlertResponseDto> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverity(severity).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private void sendAlertToService(Alert alert) {
        // Simulate sending alert to target service
        log.info("Sending alert to service: {} - Title: {}, Severity: {}", 
                alert.getTargetService(), alert.getTitle(), alert.getSeverity());
        
        // In a real implementation, you would use RestTemplate or Feign Client
        // to send the alert to the target service's webhook/notification endpoint
    }

    private AlertResponseDto toResponseDto(Alert alert) {
        AlertResponseDto dto = new AlertResponseDto();
        dto.setId(alert.getId());
        dto.setTitle(alert.getTitle());
        dto.setMessage(alert.getMessage());
        dto.setSeverity(alert.getSeverity());
        dto.setTargetService(alert.getTargetService());
        dto.setStatus(alert.getStatus());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setSentAt(alert.getSentAt());
        return dto;
    }
}
