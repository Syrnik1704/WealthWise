package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Categories;

public interface CategoriesDAO {

    boolean exists(String category);

    Categories findByName(String category);
}
