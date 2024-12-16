package com.example.wealthwise_api.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity
@Table(name = "refresh_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private Date expirationTime;

    public RefreshToken(@NotNull String token, @NotNull String subject, @NotNull Date expirationTime) {
        this.token = token;
        this.subject = subject;
        this.expirationTime = expirationTime;
    }
}
