package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AssetsListRequestDelete;
import com.example.wealthwise_api.DTO.AssetsRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping(value="/addAsset",produces = "application/json")
    public ResponseEntity<?> addAsset(@RequestBody AssetsRequest assetsRequest){
        return assetService.addAsset(assetsRequest);
    }

    @PostMapping(value="/getAsset",produces = "application/json")
    public ResponseEntity<?> getAsset(@RequestBody TokenRequest tokenRequest){
        return assetService.getAllAssetsList(tokenRequest);
    }


    @PostMapping(value="/deleteAsset",produces = "application/json")
    public ResponseEntity<?> deleteAsset(@RequestBody AssetsListRequestDelete assetsListRequestDelete){
        return assetService.deleteAsset(assetsListRequestDelete);
    }

}
