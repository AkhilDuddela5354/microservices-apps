package com.greenko.assetservice.repository;

import com.greenko.assetservice.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, String> {

    Optional<Asset> findByAssetId(String assetId);

    List<Asset> findByType(String type);

}
