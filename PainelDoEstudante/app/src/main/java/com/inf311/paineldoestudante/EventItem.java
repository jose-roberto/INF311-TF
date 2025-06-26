package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class EventItem {

    @SerializedName("id")
    private String id;
    @SerializedName("descricao")
    private String descricao;

    // No futuro, podemos pegar a data aqui também
    @SerializedName("momento")
    private String momento;

    @SerializedName("pessoa")
    private String pessoa;

    public String getId() { return id; }

    public String getPessoa() { return pessoa; }

    public String getDescricao() { return descricao; }
    public String getMomento() { return momento; }
}