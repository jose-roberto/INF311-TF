package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

public class HistoryData {
    @SerializedName("campopersonalizado_5_compl_proc")
    private String situacao;

    @SerializedName("campopersonalizado_6_compl_proc")
    private String curso;

    @SerializedName("campopersonalizado_1_compl_proc")
    private String notageral;

    @SerializedName("campopersonalizado_2_compl_proc")
    private String frequencia;

    @SerializedName("campopersonalizado_3_compl_proc")
    private String satisfacao;

    @SerializedName("campopersonalizado_4_compl_proc")
    private String lancamentosvencidos;

    public String getSituacao() { return situacao; }
    public String getCurso() { return curso; }
    public String getNota() { return notageral; }
    public String getFrequencia() { return frequencia; }
    public String getSatisfacao() { return satisfacao; }
    public String getLancamentos() { return lancamentosvencidos; }
}
