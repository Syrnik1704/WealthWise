package com.example.wealthwise_api.DTO;

public class UserDataResponse{
    private String name;
    private String surname;
    private String email;
    private String  birthDay;

    public UserDataResponse(String name, String surname, String email, String birthDay) {
        this.name = name;
        this.surname = surname;
        this.email = email;
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
}
