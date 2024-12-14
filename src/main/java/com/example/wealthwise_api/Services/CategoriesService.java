package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.CategoriesDAO;
import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {
    private final CategoriesDAO categoriesDAO;

    public CategoriesService(CategoriesDAO categoriesDAO) {
        this.categoriesDAO = categoriesDAO;
    }

    public ResponseEntity<List<Categories>> getCategories(){
        try{

            List<Categories> categoriesList = categoriesDAO.findAll();

            if(categoriesList.isEmpty()){
                return new ResponseEntity<>(List.of(),HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(categoriesList,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(List.of(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
