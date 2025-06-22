// RegisterResponse.java
package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("dados")
    private RegisterData dados;

    public boolean isSuccess() {
        return success;
    }

    public RegisterData getDados() {
        return dados;
    }
}
