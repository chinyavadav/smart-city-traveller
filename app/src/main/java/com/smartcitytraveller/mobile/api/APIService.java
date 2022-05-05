package com.smartcitytraveller.mobile.api;

import com.smartcitytraveller.mobile.api.dto.CreateProductDto;
import com.smartcitytraveller.mobile.api.dto.Location;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.api.dto.AuthResponseDto;
import com.smartcitytraveller.mobile.api.dto.CheckResponseDto;
import com.smartcitytraveller.mobile.api.dto.SignInRequest;
import com.smartcitytraveller.mobile.api.dto.SignUpRequest;
import com.smartcitytraveller.mobile.api.dto.ProductDto;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

  @Headers({"Accept: application/json"})
  @GET("/api/v1/user/check/{msisdn}")
  Call<CheckResponseDto> check(@Path("msisdn") String msisdn);

  @Headers({"Accept: application/json"})
  @POST("/api/v1/user/sign-in")
  Call<AuthResponseDto> signIn(@Body SignInRequest signInRequest);

  @Headers({"Accept: application/json"})
  @POST("/api/v1/user/sign-up")
  Call<AuthResponseDto> signUp(@Body SignUpRequest signUpRequest);

  @Headers({"Accept: application/json"})
  @GET("/api/v1/user/{userId}")
  Call<UserDto> getUser(
      @Header("Authorization") String authentication, @Path("userId") UUID userId);

  @Headers({"Accept: application/json"})
  @PUT("/api/v1/user")
  Call<UserDto> updateUser(
      @Header("Authorization") String authentication, @Body UserDto updateProfileRequest);

  @Headers({"Accept: application/json"})
  @POST("/api/v1/user/panic-button/{userId}")
  Call<UserDto> panicButton(
      @Header("Authorization") String authentication,
      @Path("userId") UUID userId,
      @Body Location location);

  @Headers({"Accept: application/json"})
  @GET("/api/v1/user/reset-password/{msisdn}")
  Call<String> resetPassword(@Path("msisdn") String msisdn);
}
