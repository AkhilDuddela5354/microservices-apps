package com.greenko.telemetryservice.clent;

import com.greenko.telemetryservice.dto.AssetResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "assetClient",
        url = "${ASSET_SERVICE_URL:http://localhost:8080}")
public interface AssetServiceClient {

    @GetMapping("/api/assets/{assetId}")
    AssetResponseDto getAssetById(@PathVariable String assetId);


}
