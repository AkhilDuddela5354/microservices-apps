package com.greenko.assetservice.api;

import com.greenko.assetservice.exception.AssetNotFoundException;
import com.greenko.assetservice.model.Asset;
import com.greenko.assetservice.repository.AssetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetController {


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
        return assetRepository.findAll();
    }

    @GetMapping("/{assetId}")
    public Asset getAssetById(@PathVariable String assetId) {
        return assetRepository.findById(assetId).orElseThrow(() -> new AssetNotFoundException("Asset not found with id: " + assetId));
    }

}
