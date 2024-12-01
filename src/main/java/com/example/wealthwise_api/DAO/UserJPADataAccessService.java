package com.example.wealthwise_api.DAO;


import com.example.wealthwise_api.DTO.UserData;
import com.example.wealthwise_api.DTO.UserInfo;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class UserJPADataAccessService implements UserDAO{

    private final UserEntityRepository userEntityRepository;

    public UserJPADataAccessService (UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }
    @Override
    public UserEntity findUserByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public List<UserEntity> findAll() {
        return userEntityRepository.findAll();
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
    public UserInfo getData(String email) {
        return userEntityRepository.getUserData(email);
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        userEntityRepository.delete(userEntity);
    }

    @Override
    public void deleteAll(List<UserEntity> userEntities) {
        userEntityRepository.deleteAll(userEntities);
    }

    @Override
    public List<UserEntity> findAllByEmailIn(List<String> emails) {
        return userEntityRepository.findAllByEmailIn(emails);
    }

    @Override
    public Optional<UserEntity> findUserById(long id) {
        return userEntityRepository.findById(id);
    }

}