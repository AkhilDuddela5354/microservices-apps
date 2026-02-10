package com.greenko.alertservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponseDto {
    private Long id;
    private String title;
    private String message;
    private String severity;
    private String targetService;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
