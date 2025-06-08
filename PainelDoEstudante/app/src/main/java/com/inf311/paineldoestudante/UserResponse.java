package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("qtdTotal")
    private int qtdTotal;
    @SerializedName("dados")
    private List<UserData> dados;

    public boolean isSuccess() { return success; }
    public int getQtdTotal() { return qtdTotal; }
    public List<UserData> getDados() { return dados; }
}