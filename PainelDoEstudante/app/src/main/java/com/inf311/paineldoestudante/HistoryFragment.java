package com.inf311.paineldoestudante;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ProfileViewModel viewModel;
    private View historyInfoContainer;
    private TextView profileUsername;
    private TextView historyFrequencia, historyNotaGeral, historySatisfacao, historySituacao, historyLancamentos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Observa os dados do ALUNO para atualizar o cabeçalho.
        viewModel.getStudent().observe(getViewLifecycleOwner(), studentData -> {
            if (studentData != null) {
                profileUsername.setText(studentData.getNome());
            }
        });

        // Observa os dados do HISTÓRICO para preencher os detalhes.
        viewModel.getHistory().observe(getViewLifecycleOwner(), historyList -> {
            if (historyList != null && !historyList.isEmpty()) {
                historyInfoContainer.setVisibility(View.VISIBLE);
                RegisterData opportunity = historyList.get(0);
                // A chamada agora é válida, pois o método espera o tipo correto.
                updateDetailsView(opportunity.getCamposPersonalizados());
            } else {
                Log.d("HistoryFragment", "Lista de histórico vazia ou nula. Container permanece invisível.");
                historyInfoContainer.setVisibility(View.GONE);
            }
        });
    }

    private void initializeViews(View view) {
        historyInfoContainer = view.findViewById(R.id.historyInfoContainer);
        profileUsername = view.findViewById(R.id.profileUsername);
        historyFrequencia = view.findViewById(R.id.value_frequencia);
        historyNotaGeral = view.findViewById(R.id.value_nota_geral);
        historySatisfacao = view.findViewById(R.id.value_satisfacao);
        historySituacao = view.findViewById(R.id.value_situacao);
        historyLancamentos = view.findViewById(R.id.value_lancamentos);
    }

    private void updateDetailsView(OpportunityFields details) {
        if (details == null) {
            Log.e("HistoryFragment", "O objeto 'camposPersonalizados' do histórico chegou nulo.");
            return;
        }

        historyFrequencia.setText(details.getFrequencia() != null ? details.getFrequencia() + "%" : "--");
        historyNotaGeral.setText(details.getNota() != null ? details.getNota() : "--");
        historySatisfacao.setText(details.getSatisfacao() != null ? details.getSatisfacao() : "--");
        historySituacao.setText(details.getSituacao() != null ? details.getSituacao() : "--");
        historyLancamentos.setText(details.getLancamentos() != null ? details.getLancamentos() : "--");
    }
}