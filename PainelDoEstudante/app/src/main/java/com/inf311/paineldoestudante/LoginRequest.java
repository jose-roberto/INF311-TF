package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("cpf")
    private String cpf;
    @SerializedName("origem")
    private int origem;
    @SerializedName("token")
    private String token;

    public LoginRequest(String email, String cpf, int origem, String token) {
        this.email = email;
        this.cpf = cpf;
        this.origem = origem;
        this.token = token;
    }
}