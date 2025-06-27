// Arquivo: RegisterData.java

package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class RegisterData {

    @SerializedName("id")
    private String id;

    @SerializedName("etapaNome")
    private String etapaNome;

    @SerializedName("camposPersonalizados")
    private HistoryData camposPersonalizados;

    public String getId() { return id; }
    public String getEtapaNome() { return etapaNome; }
    public HistoryData getCamposPersonalizados() { return camposPersonalizados; }
}