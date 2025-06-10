package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class UserRequest {

    @SerializedName("nome")
    private String nome;

    @SerializedName("origem")
    private int origem;

    @SerializedName("token")
    private String token;

    public UserRequest(String nome, int origem, String token) {
        this.nome = nome;
        this.origem = origem;
        this.token = token;
    }
}