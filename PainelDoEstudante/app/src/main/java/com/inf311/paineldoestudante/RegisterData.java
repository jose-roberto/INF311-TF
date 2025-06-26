package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class RegisterData {

    @SerializedName("id")
    private String id;

    @SerializedName("etapaNome")
    private String etapaNome;

    @SerializedName("camposPersonalizados")
    private OpportunityFields camposPersonalizados;

    public String getId() { return id; }
    public String getEtapaNome() { return etapaNome; }

    // O getter também foi atualizado para retornar o tipo correto.
    public OpportunityFields getCamposPersonalizados() { return camposPersonalizados; }
}
