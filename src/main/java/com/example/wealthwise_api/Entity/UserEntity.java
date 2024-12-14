package com.example.wealthwise_api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    @Column(nullable = false,
            unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String birthDay;
    @Column(nullable = false)
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SavingTarget> savingTargetSet;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Assets> assetsSet;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Incomes> incomesSet;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Expenses> expensesSet;

    public UserEntity( @NotNull long idUser,@NotNull  String email, @NotNull String password,
                       @NotNull String username,@NotNull String surname,@NotNull String birthDay,@NotNull Role role) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.name = username;
        this.surname = surname;
        this.birthDay = birthDay;
        this.role = role;
    }

    public UserEntity(@NotNull String email, @NotNull String password, @NotNull String name, @NotNull String surname, @NotNull String birthDay, @NotNull Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.role = role;
    }

    public UserEntity(@NotNull String email, @NotNull String password, @NotNull String name, @NotNull String surname, @NotNull String birthDay, @NotNull Boolean isActive, @NotNull Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.isActive = isActive;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return idUser == that.idUser && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(birthDay, that.birthDay) && Objects.equals(isActive, that.isActive) && role == that.role && Objects.equals(savingTargetSet, that.savingTargetSet) && Objects.equals(assetsSet, that.assetsSet) && Objects.equals(incomesSet, that.incomesSet) && Objects.equals(expensesSet, that.expensesSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, email, password, name, surname, birthDay, isActive, role, savingTargetSet, assetsSet, incomesSet, expensesSet);
    }
}