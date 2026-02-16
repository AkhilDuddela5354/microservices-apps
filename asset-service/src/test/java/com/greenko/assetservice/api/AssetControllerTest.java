package com.greenko.assetservice.api;

import com.greenko.assetservice.model.Asset;
import com.greenko.assetservice.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetController.class)
@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AssetRepository assetRepo;

    @Test
    void registerAsset() {

    }

    @Test
    void getAllAsset() {
    }

    @Test
    void getAssetByIdWhenAssetNotPresent() throws Exception {

        // AAA - Arrange, Act, Assert
        // Arrange - Set up the test data and mock behavior
        String assetId = "123";

        Mockito.when(assetRepo.findById(assetId))
                .thenReturn(Optional.empty());

        // Act - Call the method being tested

        mockMvc.perform(get("/api/assets/"+assetId))
                .andExpect(status().isNotFound());


        // Assert - Verify the results
        Mockito.verify(assetRepo).findById(assetId);

    }

    @Test
    void getAssetById() throws Exception {

        // AAA - Arrange, Act, Assert
        // Arrange - Set up the test data and mock behavior
        Asset asset = new Asset();
        asset.setAssetId("123");
        asset.setAssetName("Test Asset");
        asset.setType("Test Type");
        asset.setLocation("Test Location");
        asset.setStatus("Active");
        asset.setInstalledDate(LocalDate.parse("2024-01-01"));

        Mockito.when(assetRepo.findById("123"))
                .thenReturn(Optional.of(asset));

        Mockito.when(assetRepo.findByType("Test Type"))
                .thenReturn(List.of());

        // Act - Call the method being tested

        mockMvc.perform(get("/api/assets/"+asset.getAssetId()))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                    "assetId": "123",
                                    "assetName": "Test Asset",
                                    "type": "Test Type",
                                    "location": "Test Location",
                                    "status": "Active",
                                    "installedDate": "2024-01-01"
                                }
                                """
                ));


        // Assert - Verify the results
        Mockito.verify(assetRepo).findById(asset.getAssetId());

    }
}