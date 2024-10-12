package com.example.wealthwise_api.DAO;


import com.example.wealthwise_api.Entity.UserDataRequest;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("jpa")
public class UserJPADataAccessService  implements UserDAO{

    private final UserEntityRepository userEntityRepository;

    public UserJPADataAccessService (UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }
    @Override
    public UserEntity findUserByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public boolean existsUserWithEmail(String email) {
        return userEntityRepository.findEmail(email);
    }

    @Override
    public void save(UserEntity userEntity) {
        userEntityRepository.save(userEntity);
    }

    @Override
    public void changePassword(String email, String password) {
        userEntityRepository.changePassword(email, password);
    }

    @Override
    public UserDataRequest getData(String email) {
        return userEntityRepository.getUserData(email);
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        userEntityRepository.delete(userEntity);
    }

}