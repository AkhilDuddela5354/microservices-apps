package com.greenko.assetservice.repository;

import com.greenko.assetservice.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, String> {

}
