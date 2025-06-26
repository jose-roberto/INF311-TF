package com.inf311.paineldoestudante;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RubeusApi {
    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> loginGestor(@Body LoginRequest requestBody);

    @POST("api/Contato/dadosPessoas")
    Call<ResponseBody> searchContatos(@Body UserRequest requestBody);

    @POST("api/Contato/dadosPessoa")
    Call<StudentResponse> getStudent(@Body StudentRequest requestBody);

    @POST("api/Contato/listarOportunidades")
    Call<RegisterListResponse> listarRegistros(@Body RegisterRequest request);

    @POST("api/Registro/dados")
    Call<RegisterResponse> buscarRegistro(@Body RegisterRequest request);

    @POST("api/Evento/cadastroTipoEvento")
    Call<CreateEventTypeResponse> createEventType(@Body CreateEventTypeRequest requestBody);

    @POST("api/Evento/cadastro")
    Call<EventResponse> addEvent(@Body EventRequest requestBody);

    @POST("api/Evento/listarEventos")
    Call<ListEventsResponse> listEvents(@Body ListEventsRequest requestBody);

    @POST("api/Evento/excluir")
    Call<ResponseBody> deleteEvent(@Body DeleteEventRequest requestBody);

}
