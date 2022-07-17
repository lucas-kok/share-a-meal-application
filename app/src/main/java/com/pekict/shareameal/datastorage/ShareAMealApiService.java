package com.pekict.shareameal.datastorage;

import android.telecom.Call;

import com.pekict.shareameal.domain.LoginData;
import com.pekict.shareameal.domain.LoginResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ShareAMealApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body() LoginData loginData);

    @GET("api/user/profile")
    Call<LoginResponse> getUserProfile(@Header("Authorization") String token);

}
