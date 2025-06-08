package com.inf311.paineldoestudante;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // ATENÇÃO: PRECISAMOS MUDAR AQUI PRA URL DA RUBEUS DE FATO
    private static final String BASE_URL = "https://crmufvgrupo5.apprubeus.com.br/";

    private static RubeusApiService apiService = null;

    public static RubeusApiService getInstance() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(RubeusApiService.class);
        }
        return apiService;
    }
}