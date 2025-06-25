package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class EventRequest {
    @SerializedName("pessoa")
    private PersonId pessoa;
    @SerializedName("descricao")
    private String descricao;
    @SerializedName("tipo")
    private int tipo;
    @SerializedName("origem")
    private int origem;
    @SerializedName("token")
    private String token;

    public EventRequest(int personId, String descricao, int tipo, int origem, String token) {
        this.pessoa = new PersonId(personId);
        this.descricao = descricao;
        this.tipo = tipo;
        this.origem = origem;
        this.token = token;
    }

    public static class PersonId {
        @SerializedName("id")
        private int id;
        public PersonId(int id) { this.id = id; }
    }
}