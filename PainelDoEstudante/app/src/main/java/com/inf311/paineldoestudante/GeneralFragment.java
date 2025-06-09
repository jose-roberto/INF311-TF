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

import java.util.Collections;
import java.util.List;

public class GeneralFragment extends Fragment {

    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView userName;
    private TextView userEmail;
    private TextView userBirth;
    private TextView userCourse;
    private TextView obs1;

    public GeneralFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Liga as views
        profilePicture = view.findViewById(R.id.profilePicture);
        profileUsername = view.findViewById(R.id.profileUsername);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userBirth = view.findViewById(R.id.userBirth);
        userCourse = view.findViewById(R.id.userCourse);
        obs1 = view.findViewById(R.id.obs1);
    }

    /**
     * Atualiza os dados do aluno na tela.
     * @param data objeto contendo nome, email e birthday do aluno
     */
    public void updateStudentView(StudentData data) {
        if (data == null) return;

        // Nome de usuário no topo e no campo
        profileUsername.setText(data.getNome());
        userName.setText(data.getNome());

        // Email
        userEmail.setText(data.getEmail());

        // Data de nascimento: formata de yyyy-MM-dd para dd/MM/yyyy
        String raw = data.getBirthday();
        String formatted;
        try {
            String[] parts = raw.split("-");
            // parts[0] = yyyy, [1] = MM, [2] = dd
            formatted = parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            formatted = raw;
        }
        userBirth.setText(formatted);

        // Curso e observações: se houver, usar outro campo do data ou input estático
        // Exemplo placeholder:
        userCourse.setText("--");
        obs1.setText("--");
    }
}
