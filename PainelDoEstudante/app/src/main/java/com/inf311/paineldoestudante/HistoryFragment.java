package com.inf311.paineldoestudante;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ProfileViewModel viewModel;
    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView historyFrequencia;
    private TextView historyNotaGeral;
    private TextView historySatisfacao;
    private TextView historySituacao;

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
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::updateStudentHeader);
        viewModel.getHistory().observe(getViewLifecycleOwner(), this::handleHistoryResponse);
    }

    private void initializeViews(View view) {
        profilePicture = view.findViewById(R.id.profilePicture);
        profileUsername = view.findViewById(R.id.profileUsername);

        historyFrequencia = view.findViewById(R.id.userFrequency);
        historyNotaGeral = view.findViewById(R.id.userGrade);
        historySatisfacao = view.findViewById(R.id.userSatisfaction);
        historySituacao = view.findViewById(R.id.userSituation);
    }

    private void updateStudentHeader(StudentData student) {
        if (student != null) {
            profileUsername.setText(student.getNome());
            // Glide.with(this).load(student.getFotoUrl()).into(profilePicture);
        }
    }

    private void handleHistoryResponse(List<RegisterData> registers) {
        if (registers != null && !registers.isEmpty()) {
            RegisterData opportunity = registers.get(0);
            updateDetailsView(opportunity);
        } else {
            if (getView() != null) {
                getView().findViewById(R.id.historyInfoContainer).setVisibility(View.GONE);
            }
        }
    }

    private void updateDetailsView(RegisterData data) {
        HistoryData historyDetails = data.getCamposPersonalizados();
        if (historyDetails == null) return;

        historyFrequencia.setText("Frequência: " + historyDetails.getFrequencia());
        historyNotaGeral.setText("Nota geral: " + historyDetails.getNota());
        historySatisfacao.setText("Satisfação: " + historyDetails.getSatisfacao());
        historySituacao.setText("Situação: " + historyDetails.getSituacao());
    }
}