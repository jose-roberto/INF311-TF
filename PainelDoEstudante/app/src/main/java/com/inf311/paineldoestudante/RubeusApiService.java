package com.inf311.paineldoestudante;


import okhttp3.ResponseBody; // Importamos o "corpo bruto" da resposta
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Esta interface define todos os "comandos" que nosso app pode enviar para a API da Rubeus.
 */
public interface RubeusApiService {

    /**
     * Define o comando para buscar os dados de um contato (nosso "login").
     * @param requestBody O objeto com email, token e origem.
     * @return Uma chamada que espera receber um ResponseBody (a resposta bruta do servidor).
     */
    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> loginGestor(@Body LoginRequest requestBody);

    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> searchContatos(@Body SearchRequest requestBody);

}