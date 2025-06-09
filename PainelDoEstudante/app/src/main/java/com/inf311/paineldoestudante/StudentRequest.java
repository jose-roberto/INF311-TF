package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentRequest {
    @SerializedName("origem")
    private int origem;

    @SerializedName("token")
    private String token;

    @SerializedName("id")
    private int id;

    @SerializedName("camposRetorno")
    private List<String> camposRetorno;

    public StudentRequest(int origem, String token, int id, List<String> camposRetorno) {
        this.origem = origem;
        this.token = token;
        this.id = id;
        this.camposRetorno = camposRetorno;
    }
}
