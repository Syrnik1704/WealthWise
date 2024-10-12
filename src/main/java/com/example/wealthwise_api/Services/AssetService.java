package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.AssetsDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.AssetsListRequestDelete;
import com.example.wealthwise_api.DTO.AssetsRequest;
import com.example.wealthwise_api.DTO.AssetsRequestDelete;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.Assets;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final AssetsDAO assetDAO;

    public AssetService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, @Qualifier("assetJPA") AssetsDAO assetDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.assetDAO = assetDAO;
    }

    public ResponseEntity<?> addAsset(AssetsRequest assetsRequest){
        try {

            if(assetsRequest.getCurrency() == null || assetsRequest.getName() == null || assetsRequest.getValue() <=0 || assetsRequest.getToken() == null){
                return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
            }

            if(assetsRequest.getName().equals("") || assetsRequest.getCurrency().equals("") || assetsRequest.getToken().equals("")){
                return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(assetsRequest.getToken());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Assets assets = new Assets(assetsRequest.getValue(),assetsRequest.getCurrency(),assetsRequest.getName(),principal);

           assetDAO.save(assets);

            return new ResponseEntity<String>("Assets has been saved successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteAsset(AssetsListRequestDelete assetsListRequestDelete){
        try {
            if(assetsListRequestDelete.getAssetsRequestDeleteList() == null || assetsListRequestDelete.getToken() == null
                    || assetsListRequestDelete.getAssetsRequestDeleteList().isEmpty() || assetsListRequestDelete.getToken().equals("")){
                return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(assetsListRequestDelete.getToken());
            UserEntity principal = userDAO.findUserByEmail(email);

            if (principal == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            List<AssetsRequestDelete> assetsRequestDeleteList = assetsListRequestDelete.getAssetsRequestDeleteList();
            for(AssetsRequestDelete assetsRequestDelete : assetsRequestDeleteList){
                Assets assetsDelete = assetDAO.findAssetByAllData(assetsRequestDelete.getValue(),assetsRequestDelete.getCurrency(),assetsRequestDelete.getName(),principal.getIdUser());
                assetDAO.delete(assetsDelete);
            }

            return new ResponseEntity<>("Assets has been deleted successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getAllAssetsList(TokenRequest tokenRequest){
     try{
            if(tokenRequest.token() == null || tokenRequest.token().equals("")){
                return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            List<Assets> assetsList = assetDAO.getAssetsByUserId(principal.getIdUser());
            List<AssetsRequestDelete> assetsRequestDeleteList = new ArrayList<>();
            for(Assets assets : assetsList){
                AssetsRequestDelete assetsRequestDelete = new AssetsRequestDelete(assets.getCurrency(),assets.getName(),assets.getValue());
                assetsRequestDeleteList.add(assetsRequestDelete);
            }

            return new ResponseEntity<>(assetsRequestDeleteList,HttpStatus.OK);
    }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
