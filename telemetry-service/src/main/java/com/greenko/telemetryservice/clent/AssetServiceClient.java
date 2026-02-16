package com.greenko.telemetryservice.clent;

import com.greenko.telemetryservice.dto.AssetResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "assetClient",
        url = "${ASSET_SERVICE_URL:http://localhost:8080}")
public interface AssetServiceClient {

    @GetMapping("/api/assets/{assetId}")
    @CircuitBreaker(name = "assetService", fallbackMethod = "getAssetByIdFallback")
    @Retry(name = "assetServiceRetry")
    @RateLimiter(name = "assetServiceRateLimiter")
    AssetResponseDto getAssetById(@PathVariable String assetId);

    default AssetResponseDto getAssetByIdFallback(String assetId, Throwable throwable) {
       System.err.println("Failed to fetch asset details for assetId: " + assetId + ". Error: " + throwable.getMessage());
        return null;
    }

}
