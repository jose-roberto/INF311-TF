package com.inf311.paineldoestudante;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentData {
    @SerializedName("id")
    private String id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("datanascimento")
    private String datanascimento;

    @SerializedName("emails")
    private EmailsWrapper emails;

    @SerializedName("camposPersonalizados")
    private List<UserProperties> userProperties;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDataNascimento() {
        return datanascimento;
    }

    public String getEmailPrincipal() {
        return emails != null && emails.principal != null
                ? emails.principal.email
                : null;
    }

    public static class EmailsWrapper {
        @SerializedName("principal")
        public EmailItem principal;
    }

    public static class EmailItem {
        @SerializedName("email")
        public String email;
    }

    public String getCurso() {
        if (userProperties != null && !userProperties.isEmpty()) {
            UserProperties course = userProperties.get(0);

            if (course != null) {
                return course.getValor();
            }
        }

        return "--";
    }
}
