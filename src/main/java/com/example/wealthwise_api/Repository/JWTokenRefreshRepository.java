package com.example.wealthwise_api.Repository;

import com.example.wealthwise_api.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JWTokenRefreshRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);
    @Query(value = "SELECT * FROM refresh_token WHERE subject =:subject", nativeQuery = true)
    RefreshToken findBySubject(@Param("subject") String subjects);
    @Query(value = "DELETE FROM refresh_token WHERE subject =:subject", nativeQuery = true)
    void deleteAllBySubject(@Param("subject") String subject);
    @Query(value = "SELECT EXISTS(SELECT * FROM refresh_token WHERE subject =:subject)", nativeQuery = true)
    Boolean existsByToken(@Param("subject") String subject);
}
