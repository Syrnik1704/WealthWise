package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AssetsListRequestDelete;
import com.example.wealthwise_api.DTO.AssetsRequest;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Services.AssetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

    @GetMapping(value="/getAsset",produces = "application/json")
    public ResponseEntity<?> getAsset(HttpServletRequest request){
        return assetService.getAllAssetsList(request);
    }


    @PostMapping(value="/deleteAsset",produces = "application/json")
    public ResponseEntity<?> deleteAsset(@RequestBody AssetsListRequestDelete assetsListRequestDelete){
        return assetService.deleteAsset(assetsListRequestDelete);
    }

}
