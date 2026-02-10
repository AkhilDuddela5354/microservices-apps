package com.greenko.alertservice.api;

import com.greenko.alertservice.dto.AlertRequestDto;
import com.greenko.alertservice.dto.AlertResponseDto;
import com.greenko.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertResponseDto> createAlert(@RequestBody AlertRequestDto request) {
        AlertResponseDto response = alertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDto>> getAllAlerts() {
        List<AlertResponseDto> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByStatus(@PathVariable String status) {
        List<AlertResponseDto> alerts = alertService.getAlertsByStatus(status);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<AlertResponseDto>> getAlertsByService(@PathVariable String service) {
        List<AlertResponseDto> alerts = alertService.getAlertsByService(service);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AlertResponseDto>> getAlertsBySeverity(@PathVariable String severity) {
        List<AlertResponseDto> alerts = alertService.getAlertsBySeverity(severity);
        return ResponseEntity.ok(alerts);
    }
}
