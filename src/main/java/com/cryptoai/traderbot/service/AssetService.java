package com.cryptoai.traderbot.service;

import com.cryptoai.traderbot.model.Asset;
import com.cryptoai.traderbot.model.Coin;
import com.cryptoai.traderbot.model.User;

import java.util.List;

public interface AssetService {

    Asset createAsset(User user, Coin coin, Double quantity);

    Asset getAssetById(Long id) throws Exception;

    Asset getAssetByUserIdAndId(Long userId, Long assetId);

    List<Asset> getUserAssets(Long userId);

    Asset updateAsset(Long assetId, Double quantity) throws Exception;

    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);

    void deleteAsset(Long assetId);
}
