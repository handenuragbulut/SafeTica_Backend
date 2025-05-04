package com.safetica.safetica_backend.dto;

import java.time.LocalDate;

public class UserUpdateRequest {
    private String phoneNumber;
    private String country;
    private LocalDate birthDate;
 
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
}
