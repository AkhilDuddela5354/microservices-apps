package com.greenko.assetservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Asset {

    @Id
    private String assetId;
    private String assetName;
    private String type;
    private LocalDate installedDate;
    private String location;
    private String status;

}
