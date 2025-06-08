package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("id")
    private String id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("email")
    private String email;

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}