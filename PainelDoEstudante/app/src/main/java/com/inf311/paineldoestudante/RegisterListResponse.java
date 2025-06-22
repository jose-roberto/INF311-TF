package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RegisterListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("dados")
    private List<RegisterData> dados;

    public boolean isSuccess() { return success; }
    public List<RegisterData> getDados() { return dados; }
}
