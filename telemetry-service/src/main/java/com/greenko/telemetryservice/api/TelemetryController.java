package com.greenko.telemetryservice.api;

import com.greenko.telemetryservice.dto.AssetResponseDto;
import com.greenko.telemetryservice.dto.TelemetryRequestDto;
import com.greenko.telemetryservice.model.Telemetry;
import com.greenko.telemetryservice.repository.TelemetryRepo;
import com.greenko.telemetryservice.service.TelemetryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {


    private final TelemetryRepo telemetryRepo;
    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryRepo telemetryRepo, TelemetryService telemetryService) {
        this.telemetryRepo = telemetryRepo;
        this.telemetryService = telemetryService;
    }

    @GetMapping
    public List<Telemetry> getAllTelemetry(){
        return telemetryRepo.findAll();
    }

    @PostMapping
    public Telemetry recordData(@RequestBody TelemetryRequestDto telemetryRequestDto) {
        try {
            AssetResponseDto asset = telemetryService.fetchAssetDetails(telemetryRequestDto.assetId());
            Telemetry telemetry = new Telemetry();
            telemetry.setAssetId(telemetryRequestDto.assetId());
            telemetry.setRecordedAt(LocalDateTime.now());
            telemetry.setId(UUID.randomUUID().toString());
            telemetry.setTemperature(telemetryRequestDto.temperature());
            telemetry.setPower(telemetryRequestDto.power());
            return telemetryRepo.save(telemetry);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }


}
