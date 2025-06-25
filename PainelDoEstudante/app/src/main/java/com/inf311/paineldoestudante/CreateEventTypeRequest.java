package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

// Este é o "formulário" que vamos preencher e enviar para a API
public class CreateEventTypeRequest {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("formaCriacaoOportunidade")
    private int formaCriacaoOportunidade;

    @SerializedName("origem")
    private int origem;

    @SerializedName("token")
    private String token;

    public CreateEventTypeRequest(String titulo, int formaCriacaoOportunidade, int origem, String token) {
        this.titulo = titulo;
        this.formaCriacaoOportunidade = formaCriacaoOportunidade;
        this.origem = origem;
        this.token = token;
    }
}