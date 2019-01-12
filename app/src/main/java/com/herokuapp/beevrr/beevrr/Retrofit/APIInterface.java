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

public interface APIInterface {
    @FormUrlEncoded
    @POST("mobile/login")
    Call<String> login(@Field("user_name") String userName,
                       @Field("password") String password);

    @GET("mobile/dashboard")
    Call<String> dashboard();

    @POST("mobile/logout")
    Call<String> logout();

    @FormUrlEncoded
    @POST("mobile/change_password")
    Call<String> changePassword(@Field("oldpw") String oldPW,
                                @Field("conoldpw") String conOldPW,
                                @Field("newpw") String newPW,
                                @Field("connewpw") String conNewPW);

    @FormUrlEncoded
    @POST("mobile/change_bio")
    Call<String> changeBio(@Field("bio") String bio);

    @FormUrlEncoded
    @POST("mobile/register")
    Call<String> register(@Field("user_name") String user_name,
                                @Field("password") String password,
                                @Field("passwordc") String passwordc);
}
