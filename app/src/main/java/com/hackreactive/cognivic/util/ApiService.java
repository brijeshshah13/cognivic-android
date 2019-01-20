package com.hackreactive.cognivic.util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("uploadSource")
    Call<ResponseBody> postSourceImage(@Part MultipartBody.Part image, @Part("upload") RequestBody name);

    @Multipart
    @POST("uploadTarget")
    Call<ResponseBody> postTargetImage(@Part MultipartBody.Part image, @Part("upload") RequestBody name);


    @POST("checkMatch")
    Call<ResponseBody> postCheckMatch();
}