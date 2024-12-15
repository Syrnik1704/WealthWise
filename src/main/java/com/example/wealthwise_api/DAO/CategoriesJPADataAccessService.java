package com.example.wealthwise_api.DAO;

import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Repository.CategoriesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("categoriesJPA")
public class CategoriesJPADataAccessService implements CategoriesDAO{

    private final CategoriesRepository categoriesRepository;

    public CategoriesJPADataAccessService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public boolean exists(String category) {
        return categoriesRepository.existsByCategory(category);
    }

    @Override
    public Categories findByName(String category) {
        return categoriesRepository.findByCategory(category);
    }

    @Override
    public void save(Categories categories) {
        categoriesRepository.save(categories);
    }

    @Override
    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }

    @Override
    public void delete(Categories categories) {
        categoriesRepository.delete(categories);
    }
}
