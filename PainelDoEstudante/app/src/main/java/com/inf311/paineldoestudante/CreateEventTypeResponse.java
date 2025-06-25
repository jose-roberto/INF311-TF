package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

// Este é o "molde" da resposta que esperamos receber do servidor
public class CreateEventTypeResponse {
    @SerializedName("success")
    private boolean success;

    // A API deve nos devolver o ID do novo tipo de evento criado
    @SerializedName("id")
    private int id;

    public boolean isSuccess() {
        return success;
    }

    public int getId() {
        return id;
    }
}