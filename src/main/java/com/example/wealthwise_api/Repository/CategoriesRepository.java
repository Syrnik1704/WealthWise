package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    @Query(value = "SELECT * FROM categories WHERE name = :category", nativeQuery = true)
    Categories findByCategory( @Param("category")String category);
    @Query(value = "SELECT EXISTS(SELECT * FROM categories WHERE name = :category)", nativeQuery = true)
    boolean existsByCategory(String category);
}
