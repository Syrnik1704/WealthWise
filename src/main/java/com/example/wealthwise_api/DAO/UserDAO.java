package com.example.wealthwise_api.DAO;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Entity.UserDataRequest;


public interface UserDAO {
    UserEntity findUserByEmail(String email);

    boolean existsUserWithEmail(String email);

    void save(UserEntity userEntity);

    void changePassword(String email, String password);

    UserDataRequest getData(String email);

    void deleteUser(UserEntity userEntity);

}
