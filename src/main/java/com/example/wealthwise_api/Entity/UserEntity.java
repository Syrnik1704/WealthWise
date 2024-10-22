package com.example.wealthwise_api.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;


@Entity
@Table(name = "users")
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
    @Column(nullable = false)
    private Boolean isBlocked;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<SavingsGoals> savingsGoalsSet;
    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Assets> assetsSet;

    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Incomes> incomesSet;

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

    public UserEntity(@NotNull String email, @NotNull String password, @NotNull String name, @NotNull String surname, @NotNull String birthDay, @NotNull Boolean isActive,  @NotNull Boolean isBlocked, @NotNull Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.isActive = isActive;
        this.isBlocked = isBlocked;
        this.role = role;
    }

    public UserEntity() {
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setUsername(String username) {
        this.name = username;
    }

    public String getName() {
        return name;
    }



    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return idUser == that.idUser && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(birthDay, that.birthDay) && Objects.equals(isActive, that.isActive) && Objects.equals(isBlocked, that.isBlocked) && role == that.role && Objects.equals(savingsGoalsSet, that.savingsGoalsSet) && Objects.equals(assetsSet, that.assetsSet) && Objects.equals(incomesSet, that.incomesSet) && Objects.equals(expensesSet, that.expensesSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, email, password, name, surname, birthDay, isActive, isBlocked, role, savingsGoalsSet, assetsSet, incomesSet, expensesSet);
    }
}