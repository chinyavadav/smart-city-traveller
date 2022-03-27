package com.smartcitytraveller.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponseDto {
    @SerializedName("user")
    @Expose
    UserDto user;

    @SerializedName("jwt")
    @Expose
    JWT jwt;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public JWT getJwt() {
        return jwt;
    }

    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }
}

