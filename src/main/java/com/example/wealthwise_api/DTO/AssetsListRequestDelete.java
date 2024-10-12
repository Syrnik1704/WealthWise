package com.example.wealthwise_api.DTO;

import java.util.List;

public class AssetsListRequestDelete {

    private String token;

    private List<AssetsRequestDelete> assetsRequestDeleteList;

    public AssetsListRequestDelete(String token, List<AssetsRequestDelete> assetsRequestDeleteList) {
        this.token = token;
        this.assetsRequestDeleteList = assetsRequestDeleteList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<AssetsRequestDelete> getAssetsRequestDeleteList() {
        return assetsRequestDeleteList;
    }

    public void setAssetsRequestDeleteList(List<AssetsRequestDelete> assetsRequestDeleteList) {
        this.assetsRequestDeleteList = assetsRequestDeleteList;
    }
}
