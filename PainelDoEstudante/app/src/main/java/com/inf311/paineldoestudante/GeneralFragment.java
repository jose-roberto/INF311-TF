package com.inf311.paineldoestudante;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeneralFragment extends Fragment {

    // --- Componentes da View ---
    private TextView profileUsername, userName, userEmail, userBirth, userCourse;
    private EditText observationEditText;
    private Button saveObservationButton;
    private LinearLayout observationsListLayout;
    private ImageView profilePicture;

    // --- Ferramentas de Lógica ---
    private ProfileViewModel viewModel;
    private String studentId;

    public GeneralFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Conecta todas as variáveis aos componentes do layout
        setupViews(view);

        // Configura o ViewModel para receber os dados do estudante da ProfileActivity
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::updateStudentView);

        // Configura o clique do botão de salvar
        saveObservationButton.setOnClickListener(v -> {
            String observationText = observationEditText.getText().toString().trim();
            if (!observationText.isEmpty()) {
                saveObservationToApi(observationText);
            } else {
                Toast.makeText(getContext(), "A observação não pode ser vazia.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método ajudante para manter o onViewCreated limpo
    private void setupViews(View view) {
        profileUsername = view.findViewById(R.id.profileUsername);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userBirth = view.findViewById(R.id.userBirth);
        userCourse = view.findViewById(R.id.userCourse);
        observationEditText = view.findViewById(R.id.editText_observation);
        saveObservationButton = view.findViewById(R.id.button_save_observation);
        observationsListLayout = view.findViewById(R.id.layout_observations_list);
        profilePicture = view.findViewById(R.id.profilePicture);
    }

    // Método chamado quando os dados do estudante chegam do ViewModel
    private void updateStudentView(StudentData data) {
        if (data == null) return;

        this.studentId = data.getId();
        profileUsername.setText(data.getNome());
        userName.setText(data.getNome());
        userEmail.setText(data.getEmailPrincipal());
        userCourse.setText(data.getCurso());

        // Formata a data de nascimento
        String rawBirthday = data.getDataNascimento();
        if (rawBirthday != null) {
            userBirth.setText(formatApiDateOnly(rawBirthday));
        } else {
            userBirth.setText("Não informada");
        }

        // Gatilho inicial para buscar o histórico de observações
        if (studentId != null && !studentId.isEmpty()) {
            fetchObservations(studentId);
        }

        if (data.getImagem() != null && !data.getImagem().isEmpty()) {
            Glide.with(this)
                    .load(data.getImagem())
                    .circleCrop()
                    .error(R.drawable.default_profile)
                    .into(profilePicture);
        }
    }

    /**
     *Envia a nova observação para a API e, em caso de sucesso,
     * chama fetchObservations para recarregar a lista de obs
     */
    private void saveObservationToApi(String observationText) {
        String token = "f70e467007e33f442d2b01c37e6e0397";
        int origem = 9;
        int eventTypeId = 113;

        if (token == null || origem == 0 || studentId == null) {
            Toast.makeText(getContext(), "Erro: Dados da sessão ou do estudante não encontrados.", Toast.LENGTH_SHORT).show();
            return;
        }

        EventRequest request = new EventRequest(Integer.parseInt(studentId), observationText, eventTypeId, origem, token);

        RubeusClient.getInstance().addEvent(request).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Observação salva!", Toast.LENGTH_SHORT).show();
                    observationEditText.setText(""); // Limpa o campo

                    // LÓGICA CORRETA: Recarrega a lista do servidor para garantir consistência
                    fetchObservations(studentId);

                } else {
                    Toast.makeText(getContext(), "Erro ao salvar observação.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Falha de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Busca as observações do estudante (filtrando pelo tipo 113 que é a observação)
     * e, para cada uma, chama o addObservationToView
     */
    private void fetchObservations(String studentId) {
        String token = "f70e467007e33f442d2b01c37e6e0397";
        int origem = 9;
        if (token == null) return;
        ListEventsRequest request = new ListEventsRequest(Integer.parseInt(studentId), 113, origem, token);
        RubeusClient.getInstance().listEvents(request).enqueue(new Callback<ListEventsResponse>() {
            @Override
            public void onResponse(Call<ListEventsResponse> call, Response<ListEventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    observationsListLayout.removeAllViews();
                    List<EventItem> todasObservacoes = response.body().getDados();
                    todasObservacoes.sort(Comparator.comparing(EventItem::getMomento).reversed());
                    if (todasObservacoes != null && !todasObservacoes.isEmpty()) {
                        List<EventItem> observacoesFiltradas = new ArrayList<>();
                        for (EventItem observacao : todasObservacoes) {
                            //To fazendo client-side filtering aqui
                            //tentei fazer server-side, mas nao deu certo
                            //se alguem quiser tentar
                            if (studentId.equals(observacao.getPessoa())) {
                                observacoesFiltradas.add(observacao);
                            }
                        }

                        if (!observacoesFiltradas.isEmpty()) {
                            observacoesFiltradas.sort(Comparator.comparing(EventItem::getMomento).reversed());
                            for (EventItem observacao : observacoesFiltradas) {
                                addObservationToView(observacao);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ListEventsResponse> call, Throwable t) {
                Log.e("FETCH_OBS_FAIL", "Falha de conexão ao buscar observações", t);
            }
        });
    }

    /**
     * Método que constroi a observacoa.
     */
    private void addObservationToView(EventItem observation) {
        if (getContext() == null) return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View observationView = inflater.inflate(R.layout.item_observation, observationsListLayout, false);

        TextView observationTextView = observationView.findViewById(R.id.textView_observation_text);
        TextView timestampTextView = observationView.findViewById(R.id.textView_observation_timestamp);
        ImageView deleteButton = observationView.findViewById(R.id.button_delete_observation);

        observationTextView.setText(observation.getDescricao());
        timestampTextView.setText(formatApiTimestamp(observation.getMomento()));

        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog(observation);
        });

        // Adiciona a view (sem o ", 0" para que as mais novas fiquem em cima)
        observationsListLayout.addView(observationView);
    }

    private void showDeleteConfirmationDialog(EventItem observation) {
        new AlertDialog.Builder(getContext())
                .setTitle("Apagar Observação")
                .setMessage("Tem certeza que deseja apagar esta observação?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    deleteSingleObservation(observation.getId());
                })
                .setNegativeButton("Não", null)
                .show();
    }
    private void deleteSingleObservation(String eventId) {
        String token = "f70e467007e33f442d2b01c37e6e0397";
        int origem = 9;

        DeleteEventRequest request = new DeleteEventRequest(Integer.parseInt(eventId), origem, token);

        RubeusClient.getInstance().deleteEvent(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Observação apagada.", Toast.LENGTH_SHORT).show();
                    fetchObservations(studentId); // Recarrega a lista para mostrar a mudança
                } else {
                    Toast.makeText(getContext(), "Erro ao apagar.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Falha de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método ajudante para formatar a data e hora que vem da API.
     */
    private String formatApiTimestamp(String apiTimestamp) {
        if (apiTimestamp == null) return "Data indisponível";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(apiTimestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            return apiTimestamp;
        }
    }

    /**
     * Método ajudante para formatar apenas a data (para o campo de nascimento).
     */
    private String formatApiDateOnly(String apiDate) {
        if (apiDate == null) return "Não informada";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(apiDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            return apiDate;
        }
    }
}