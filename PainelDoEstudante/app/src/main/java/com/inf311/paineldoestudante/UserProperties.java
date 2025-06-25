package com.inf311.paineldoestudante;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class UserProperties {

    @SerializedName("nome")
    private String nome;

    @SerializedName("valor")
    private JsonElement valor;

    // Getters
    public String getNome() {
        return nome;
    }

    public JsonElement getValor() {
        return valor;
    }

    public String getValorAsString() {
        if (valor != null && valor.isJsonPrimitive()) {
            return valor.getAsString();
        }
        return null;
    }
}