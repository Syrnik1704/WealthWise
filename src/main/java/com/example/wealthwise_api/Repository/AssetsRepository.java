package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Assets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {
    @Query(value = "SELECT * FROM assets WHERE id_user =:userId", nativeQuery = true)
    List<Assets> findAllAssetsByUserId(@Param("userId")Long userId);
    @Query(value = "SELECT * FROM assets WHERE value =:value AND currency =:currency AND name =:name AND id_user =:userId", nativeQuery = true)
    Assets findAssetByAllData(@Param("value") double value, @Param("currency")String currency,@Param("name") String name, @Param("userId")Long userId);
}
