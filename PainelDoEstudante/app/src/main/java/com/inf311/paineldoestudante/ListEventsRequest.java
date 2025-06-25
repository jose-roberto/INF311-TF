package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;

public class ListEventsRequest {
    @SerializedName("pessoa")
    private PersonId pessoa;

    @SerializedName("origem")
    private int origem;
    @SerializedName("tipo")
    private Integer tipo;
    @SerializedName("token")
    private String token;

    public ListEventsRequest(int personId, Integer eventType, int origem, String token) {
        this.pessoa = new PersonId(personId);
        this.tipo = eventType;
        this.origem = origem;
        this.token = token;
    }

    // Classe aninhada para o objeto "pessoa"
    public static class PersonId {
        @SerializedName("id")
        private int id;
        public PersonId(int id) { this.id = id; }
    }
}