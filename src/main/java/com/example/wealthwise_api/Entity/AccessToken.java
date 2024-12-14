package com.example.wealthwise_api.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "access_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private Date expirationTime;

    public AccessToken(String token, String subject, Date expirationTime) {
        this.token = token;
        this.subject = subject;
        this.expirationTime = expirationTime;
    }
}
