package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class EventResponse {
    @SerializedName("success")
    private boolean success;
    public boolean isSuccess() { return success; }
}