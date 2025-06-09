package com.inf311.paineldoestudante;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RubeusApi {
    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> loginGestor(@Body LoginRequest requestBody);

    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> searchContatos(@Body UserRequest requestBody);

    @POST("api/Contato/dadosPessoa")
    Call<StudentResponse> getStudent(@Body StudentRequest requestBody);
}