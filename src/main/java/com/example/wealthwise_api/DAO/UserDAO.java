package com.example.wealthwise_api.DAO;
import com.example.wealthwise_api.DTO.UserInfo;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.DTO.UserData;

import java.util.List;
import java.util.Optional;


public interface UserDAO {
    UserEntity findUserByEmail(String email);

    List<UserEntity> findAll();

    boolean existsUserWithEmail(String email);

    void save(UserEntity userEntity);

    void changePassword(String email, String password);

    UserInfo getData(String email);

    void deleteUser(UserEntity userEntity);

    void deleteAll(List<UserEntity> userEntities);

    List<UserEntity> findAllByEmailIn(List<String> emails);

    Optional<UserEntity> findUserById(long id);
}
