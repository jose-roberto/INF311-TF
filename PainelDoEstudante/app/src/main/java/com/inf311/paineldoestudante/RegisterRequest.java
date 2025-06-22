package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RegisterRequest {

    @SerializedName("origem")
    private int origem;

    @SerializedName("token")
    private String token;

    @SerializedName("id")
    private int id;

    // VOLTOU A SER UMA LISTA SIMPLES DE STRINGS
    @SerializedName("camposRetorno")
    private List<String> camposRetorno;

    public RegisterRequest(int origem, String token, int id, List<String> camposRetorno) {
        this.origem = origem;
        this.token = token;
        this.id = id;
        this.camposRetorno = camposRetorno;
    }
}