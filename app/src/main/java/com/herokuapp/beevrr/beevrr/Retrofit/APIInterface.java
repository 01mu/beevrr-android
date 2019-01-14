/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {
    @FormUrlEncoded
    @POST("login")
    Call<String> login(@Field("user_name") String userName,
                       @Field("password") String password);

    @GET("dashboard")
    Call<String> dashboard();

    @POST("logout")
    Call<String> logout();

    @FormUrlEncoded
    @POST("change_password")
    Call<String> changePassword(@Field("oldpw") String oldPW,
                                @Field("conoldpw") String conOldPW,
                                @Field("newpw") String newPW,
                                @Field("connewpw") String conNewPW);

    @FormUrlEncoded
    @POST("change_bio")
    Call<String> changeBio(@Field("bio") String bio);

    @FormUrlEncoded
    @POST("register")
    Call<String> register(@Field("user_name") String user_name,
                                @Field("password") String password,
                                @Field("passwordc") String passwordc);

    @GET("user_info/{id}/{option}/p/{p}")
    Call<String> userInfo(@Path(value = "id", encoded = true) int userID,
                          @Path(value = "option", encoded = true) String type,
                          @Path(value = "p", encoded = true) int page);

    @GET("home/p/{p}")
    Call<String> discussions(@Path(value = "p", encoded = true) int page);
}
