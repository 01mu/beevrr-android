/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Retrofit;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.herokuapp.beevrr.beevrr.Preferences;

import org.riversun.okhttp3.OkHttp3CookieHelper;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {
    private static final String BASE_URL = "https://beevrr.herokuapp.com/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Activity activity) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(getCookieStore(activity).cookieJar())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    private static OkHttp3CookieHelper getCookieStore(Activity activity) {
        Preferences preferences = new Preferences(activity);

        final String xsrfToken = preferences.getXSRFToken();
        final String laravelSession = preferences.getLaravelSession();

        OkHttp3CookieHelper cookieHelper = new OkHttp3CookieHelper();

        cookieHelper.setCookie(BASE_URL, "laravel_session", laravelSession);
        cookieHelper.setCookie(BASE_URL, "XSRF-TOKEN", xsrfToken);

        return cookieHelper;
    }
}
