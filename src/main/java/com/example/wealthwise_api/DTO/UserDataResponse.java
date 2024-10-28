package com.example.wealthwise_api.DTO;

import com.example.wealthwise_api.Entity.Role;

public class UserDataResponse{
    private long id;
    private String name;
    private String surname;
    private String email;
    private Boolean isActive;
    private Role role;
    private String  birthDay;

    public UserDataResponse(long idUser, String name, String surname, String email, Boolean isActive, Role role, String birthDay) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.isActive = isActive;
        this.role = role;
        this.birthDay = birthDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getId() {
        return id;
    }
}
