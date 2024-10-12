package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Assets;

import java.util.List;

public interface AssetsDAO {

    void save(Assets assets);

    void delete(Assets assets);

    Assets findAssetByAllData(double value, String currency, String name, Long userId);

    List<Assets> getAssetsByUserId(Long userId);
}
