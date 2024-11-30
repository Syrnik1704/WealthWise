package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.DTO.UserData;
import com.example.wealthwise_api.DTO.UserInfo;
import com.example.wealthwise_api.Entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>{

    @Query(value = "SELECT * FROM users  WHERE email =:email", nativeQuery = true)
    UserEntity findByEmail(@Param("email") String email);

    @Query(value = "SELECT EXISTS(SELECT * FROM users  WHERE email =:email)", nativeQuery = true)
    boolean findEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET password =:password WHERE email =:email", nativeQuery = true)
    void changePassword(@Param("email") String email, @Param("password") String password);

    @Query(value="SELECT new com.example.wealthwise_api.DTO.UserInfo(ue.name,ue.role) FROM UserEntity ue where ue" +
            ".email=:email")
    UserInfo getUserData(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET is_active = true WHERE email =:email", nativeQuery = true)
    void changeStatusOfUser(@Param("email") String email);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.email IN :emails")
    List<UserEntity> findAllByEmailIn(@Param("emails") List<String> emails);
}