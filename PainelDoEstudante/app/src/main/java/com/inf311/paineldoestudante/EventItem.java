package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class EventItem {
    @SerializedName("descricao")
    private String descricao;

    // No futuro, podemos pegar a data aqui também
    @SerializedName("momento")
    private String momento;

    public String getDescricao() { return descricao; }
    public String getMomento() { return momento; }
}