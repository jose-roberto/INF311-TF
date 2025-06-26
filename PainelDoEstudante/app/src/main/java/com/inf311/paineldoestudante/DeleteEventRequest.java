package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class DeleteEventRequest {
    @SerializedName("id")
    private int id;

    @SerializedName("origem")
    private int origem;

    @SerializedName("token")
    private String token;

    public DeleteEventRequest(int eventId, int origem, String token) {
        this.id = eventId;
        this.origem = origem;
        this.token = token;
    }
}