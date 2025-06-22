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

public class GeneralFragment extends Fragment {

    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView userName;
    private TextView userEmail;
    private TextView userBirth;
    private TextView userCourse;
    private TextView obs1;

    private ProfileViewModel viewModel;

    public GeneralFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePicture = view.findViewById(R.id.profilePicture);
        profileUsername = view.findViewById(R.id.profileUsername);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userBirth = view.findViewById(R.id.userBirth);
        userCourse = view.findViewById(R.id.userCourse);
        obs1 = view.findViewById(R.id.obs1);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        viewModel.getStudent().observe(getViewLifecycleOwner(), data -> {
            updateStudentView(data);
        });
    }

    private void updateStudentView(StudentData data) {
        if (data == null) return;

        profileUsername.setText(data.getNome());
        userName.setText(data.getNome());

        userEmail.setText(data.getEmailPrincipal());

        String raw = data.getDataNascimento();
        String formatted;
        try {
            String[] parts = raw.split("-");
            formatted = parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            formatted = raw;
        }
        userBirth.setText(formatted);
        userCourse.setText(data.getCurso());

        obs1.setText("--");
    }
}
