/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @FormUrlEncoded
    @POST("mobile/login")
    Call<String> login(@Field("user_name") String user_name,
                            @Field("password") String password);

    @GET("mobile/details")
    Call<String> details();
}
