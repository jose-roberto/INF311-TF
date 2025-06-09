package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class StudentData {
    @SerializedName("id")
    private int id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("emailPrincipal")
    private String email;
    @SerializedName("dataNascimento")
    private String birthday;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }
}