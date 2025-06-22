package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class UserProperties {

    @SerializedName("nome")
    private String nome;

    @SerializedName("valor")
    private String valor;

    // Getters
    public String getNome() {
        return nome;
    }

    public String getValor() {
        return valor;
    }
}