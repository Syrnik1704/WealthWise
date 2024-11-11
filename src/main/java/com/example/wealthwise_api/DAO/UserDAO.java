package com.example.wealthwise_api.DAO;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Entity.UserDataRequest;

import java.util.List;


public interface UserDAO {
    UserEntity findUserByEmail(String email);

    List<UserEntity> findAll();

    boolean existsUserWithEmail(String email);

    void save(UserEntity userEntity);

    void changePassword(String email, String password);

    UserDataRequest getData(String email);

    void deleteUser(UserEntity userEntity);

}
