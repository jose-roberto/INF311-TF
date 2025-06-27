package com.inf311.paineldoestudante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportFragment extends Fragment {
    private ProfileViewModel viewModel;
    private LinearLayout reportConfigLayout;
    private LinearLayout reportLayout;
    private TextView profileUsername;
    private ImageView profilePicture;
    private TextInputEditText materialInit, materialEnd;
    private boolean isSelectingStartDate = true; // Flag para controlar qual campo está sendo preenchido

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupDatePickers();
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::updateStudentHeader);
    }

    private void initializeViews(View view) {
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePicture = view.findViewById(R.id.profilePicture);
        reportConfigLayout = view.findViewById(R.id.reportConfigContainer);
        reportLayout = view.findViewById(R.id.reportContainer);
        materialInit = view.findViewById(R.id.materialInitDate);
        materialEnd = view.findViewById(R.id.materialEndDate);
    }

    private void setupDatePickers() {
        materialInit.setOnClickListener(v -> {
            isSelectingStartDate = true;
            showMaterialDatePicker();
        });

        materialEnd.setOnClickListener(v -> {
            isSelectingStartDate = false;
            showMaterialDatePicker();
        });
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isSelectingStartDate ? "Selecione a data inicial" : "Selecione a data final")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));

            if (isSelectingStartDate) {
                materialInit.setText(formattedDate);
                // Validação opcional: garantir que data inicial <= data final
                if (materialEnd.getText().length() > 0) {
                    validateDateRange(selection, getDateFromString(materialEnd.getText().toString()));
                }
            } else {
                materialEnd.setText(formattedDate);
                // Validação opcional: garantir que data final >= data inicial
                if (materialInit.getText().length() > 0) {
                    validateDateRange(getDateFromString(materialInit.getText().toString()), selection);
                }
            }
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    // Método auxiliar para validação de intervalo
    private void validateDateRange(long startDate, long endDate) {
        if (startDate > endDate) {
            materialEnd.setError("Data final deve ser após a data inicial");
        } else {
            materialEnd.setError(null);
            // Aqui você pode chamar generateReport() se ambas as datas estiverem válidas
        }
    }

    // Método auxiliar para converter string em timestamp
    private long getDateFromString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateStudentHeader(StudentData student) {
        if (student != null) {
            profileUsername.setText(student.getNome());
        }

        if (student != null && student.getImagem() != null && !student.getImagem().isEmpty()) {
            Glide.with(this)
                    .load(student.getImagem())
                    .circleCrop()
                    .error(R.drawable.default_profile)
                    .into(profilePicture);
        }
    }
}