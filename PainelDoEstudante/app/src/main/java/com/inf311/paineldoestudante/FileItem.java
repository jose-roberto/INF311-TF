package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class FileItem {
    @SerializedName("nome")
    private String nome;

    @SerializedName("url")
    private String url;

    public FileItem(String nome, String url) {
        this.nome = nome;
        this.url = url;
    }

    public String getNome() { return nome; }
    public String getUrl() { return url; }
}
