package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Assets;
import com.example.wealthwise_api.Repository.AssetsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("assetJPA")
public class AssetsJPADataAccessService implements AssetsDAO{

    private final AssetsRepository assetsRepository;

    public AssetsJPADataAccessService(AssetsRepository assetsRepository) {
        this.assetsRepository = assetsRepository;
    }


    @Override
    public void save(Assets assets) {
        assetsRepository.save(assets);
    }

    @Override
    public void delete(Assets assets) {
        assetsRepository.delete(assets);
    }

    @Override
    public Assets findAssetByAllData(double value, String currency, String name, Long userId) {
        return assetsRepository.findAssetByAllData(value,currency,name,userId);
    }
    @Override
    public List<Assets> getAssetsByUserId(Long userId) {
        return assetsRepository.findAllAssetsByUserId(userId);
    }
}
