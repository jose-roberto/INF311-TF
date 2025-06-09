package com.inf311.paineldoestudante;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.logging.HttpLoggingInterceptor;


public class RubeusClient {
    private static final String BASE_URL = "https://crmufvgrupo5.apprubeus.com.br/";
    private static RubeusApi instance;

    public static RubeusApi getInstance() {
        if (instance == null) {
            // 1) Cria o interceptor de logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                    android.util.Log.d("RubeusHTTP", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2) Monta o client com timeout e interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)        // <— aqui
                    .build();

            // 3) Cria o Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(RubeusApi.class);
        }
        return instance;
    }
}