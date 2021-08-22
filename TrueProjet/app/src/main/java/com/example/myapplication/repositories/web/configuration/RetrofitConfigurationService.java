package com.example.myapplication.repositories.web.configuration;

import android.content.Context;

import com.example.myapplication.repositories.web.webservice.WebService;
import com.example.myapplication.utils.ConnectivityCheckInspector;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitConfigurationService {

    private static final String BASE_URL = "http://10.0.2.2:3001/";

    // Retrofit client creation
    private Retrofit retrofitClient;

    private static WebService webService = null;

    private RetrofitConfigurationService(Context context) {
        initializeRetrofit(context);
    }

    private void initializeRetrofit(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityCheckInspector(context))
                .build();

        Moshi moshiConverter = new Moshi.Builder()
                .add(new KotlinJsonAdapterFactory())
                .build();

        this.retrofitClient = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshiConverter))
                .baseUrl(BASE_URL)
                .build();
    }

    public static RetrofitConfigurationService getInstance(Context context) {
        return new RetrofitConfigurationService(context);
    }

    public WebService webService() {
        if (webService == null) {
            webService = retrofitClient.create(WebService.class);
        }
        return webService;
    }
}