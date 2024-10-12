package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JWTokenAccessRepository extends JpaRepository<AccessToken, Long>{
    AccessToken findByToken(String token);

    @Query(value = "SELECT * FROM access_token WHERE subject =:subject", nativeQuery = true)
    AccessToken findBySubject(@Param("subject") String subjects);

    @Query(value = "DELETE FROM access_token WHERE subject =:subject", nativeQuery = true)
    void deleteAllBySubject(@Param("subject") String subject);

    @Query(value = "SELECT EXISTS(SELECT * FROM access_token WHERE subject =:subject)", nativeQuery = true)
    Boolean existsByToken(@Param("subject") String subject);
}