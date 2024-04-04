package com.example.capturecloud;

import com.google.gson.JsonElement;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudService {
    @Multipart
    @POST("upload")
    Call<JsonElement> uploadImage(@Part MultipartBody.Part file, @Part("category") RequestBody category);
}