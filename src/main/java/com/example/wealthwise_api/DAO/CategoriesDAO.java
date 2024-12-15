package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Categories;

import java.util.List;

public interface CategoriesDAO {

    boolean exists(String category);

    Categories findByName(String category);

    void save(Categories categories);

    List<Categories> findAll();

    void delete(Categories categories);
}
