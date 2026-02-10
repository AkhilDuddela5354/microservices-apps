package com.greenko.telemetryservice.service;

import com.greenko.telemetryservice.clent.AssetServiceClient;
import com.greenko.telemetryservice.dto.AssetResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class TelemetryService {

    @Autowired
    private AssetServiceClient assetServiceClient;

    public AssetResponseDto fetchAssetDetails(String assetId) {

        var assetDetails = assetServiceClient.getAssetById(assetId);
        return assetDetails;
    }






}
