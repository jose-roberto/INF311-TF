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

public class FinancialFragment extends Fragment {
    private ProfileViewModel viewModel;
    private LinearLayout paymentsLayout;
    private LinearLayout overviewLayout;
    private TextView profileUsername;
    private ImageView profilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_financial, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::updateStudentHeader);
    }

    private void initializeViews(View view) {
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePicture = view.findViewById(R.id.profilePicture);
        paymentsLayout = view.findViewById(R.id.paymentsContainer);
        overviewLayout = view.findViewById(R.id.overviewContainer);
    }

    private void updateStudentHeader(StudentData student) {
        if (student != null) {
            profileUsername.setText(student.getNome());
        }

        if (student.getImagem() != null && !student.getImagem().isEmpty()) {
            Glide.with(this)
                    .load(student.getImagem())
                    .circleCrop()
                    .error(R.drawable.default_profile)
                    .into(profilePicture);
        }
    }
}