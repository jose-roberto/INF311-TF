package com.inf311.paineldoestudante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    Button generalTab, historyTab, financeTab, documentsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        generalTab = findViewById(R.id.generalTab);
        historyTab = findViewById(R.id.historyTab);
        financeTab = findViewById(R.id.financeTab);
        documentsTab = findViewById(R.id.documentsTab);

        replaceFragment(new GeneralFragment());

        generalTab.setOnClickListener(v -> replaceFragment(new GeneralFragment()));
        historyTab.setOnClickListener(v -> replaceFragment(new HistoryFragment()));
        financeTab.setOnClickListener(v -> replaceFragment(new FinancialFragment()));
        documentsTab.setOnClickListener(v -> replaceFragment(new DocumentFragment()));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new GeneralFragment())
                    .commit();
        }

        // 2) Dispara a chamada
        String userId = getIntent().getStringExtra("USER_ID");

        Log.d("USER_ID", "Email do usuário: " + userId);
        getStudent(userId);
    }

    private void getStudent(String idStr) {
        int id = Integer.parseInt(idStr); // trate NumberFormatException se precisar

        // Peça exatamente os campos que quer ver
        List<String> campos = Arrays.asList(
                "id",
                "nome",
                "emailPrincipal",   // conforme doc
                "dataNascimento"    // conforme doc
        );

        StudentRequest request = new StudentRequest(
                9,
                "f70e467007e33f442d2b01c37e6e0397",
                id,
                campos
        );

        RubeusClient.getInstance()
                .getStudent(request)
                .enqueue(new Callback<StudentResponse>() {
                    @Override
                    public void onResponse(Call<StudentResponse> call,
                                           Response<StudentResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            StudentData aluno = response.body().getDados();
                            updateStudentView(aluno);
                            Log.d("API_SUCCESS", "Aluno: " + aluno.getNome() +
                                    " / Email: " + aluno.getEmail() +
                                    " / Nascimento: " + aluno.getBirthday());
                        } else {
                            Log.e("API_ERROR", "Falha no fetch ou success=false: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<StudentResponse> call, Throwable t) {
                        Log.e("NETWORK_ERROR", "Erro na requisição", t);
                    }
                });
    }

    private void updateStudentView(StudentData data) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (frag instanceof GeneralFragment) {
            ((GeneralFragment) frag).updateStudentView(data);
        } else {
            Log.e("UPDATE_VIEW", "GeneralFragment não encontrado");
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
