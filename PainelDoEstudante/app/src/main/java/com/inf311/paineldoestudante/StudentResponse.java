package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class StudentResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("dados")
    private StudentData dados;

    public boolean isSuccess() {
        return success;
    }

    public StudentData getDados() {
        return dados;
    }
}
