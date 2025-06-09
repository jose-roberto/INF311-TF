package com.inf311.paineldoestudante;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RubeusApi {

    /**
     * Define o comando para buscar os dados de um contato (nosso "login").
     *
     * @param requestBody O objeto com email, token e origem.
     * @return Uma chamada que espera receber um ResponseBody (a resposta bruta do servidor).
     */

    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> loginGestor(@Body LoginRequest requestBody);

    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> searchContatos(@Body UserRequest requestBody);

    @POST("api/Contato/dadosPessoa")
    Call<StudentResponse> getStudent(@Body StudentRequest requestBody);

}