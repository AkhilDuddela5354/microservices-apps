package com.greenko.assetservice.api;

import com.greenko.assetservice.exception.AssetNotFoundException;
import com.greenko.assetservice.model.Asset;
import com.greenko.assetservice.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@Slf4j
public class AssetController {

    @Autowired
    Environment env;


    @GetMapping("/message")
    public String getMessage() {
        return env.getProperty("app.message.welcome");
    }


    private final AssetRepository assetRepository;

    public AssetController(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @PostMapping
    public Asset registerAsset(@RequestBody Asset asset){
        asset.setAssetId(UUID.randomUUID().toString());
        return assetRepository.save(asset);
    }

    @GetMapping
    public List<Asset> getAllAsset(){
        log.info("Fetching all assets from the database");
        var assets = assetRepository.findAll();
        log.info("Number of assets retrieved: {}", assets.size());
        return assets;
    }

    @GetMapping("/{assetId}")
    public Asset getAssetById(@PathVariable String assetId) {
        return assetRepository.findById(assetId).orElseThrow(() -> {
            log.error("Asset with id {} not found", assetId);
           return new AssetNotFoundException("Asset not found with id: " + assetId);
        });
    }

}
