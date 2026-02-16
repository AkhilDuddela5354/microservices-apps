package com.greenko.assetservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    private String assetId;
    private String assetName;
    private String type;
    private LocalDate installedDate;
    private String location;
    private String status;

}
