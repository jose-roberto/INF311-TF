package com.inf311.paineldoestudante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel vm;
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

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish();
        });

        generalTab = findViewById(R.id.generalTab);
        historyTab = findViewById(R.id.historyTab);
        financeTab = findViewById(R.id.financeTab);
        documentsTab = findViewById(R.id.documentsTab);

        List<Button> tabButtons = Arrays.asList(generalTab, historyTab, financeTab, documentsTab);

        vm = new ViewModelProvider(this).get(ProfileViewModel.class);

        generalTab.setOnClickListener(v -> {
            replaceFragment(new GeneralFragment());
            setActiveTab(generalTab);
        });

        historyTab.setOnClickListener(v -> {
            replaceFragment(new HistoryFragment());
            setActiveTab(historyTab);
        });

        financeTab.setOnClickListener(v -> {
            replaceFragment(new FinancialFragment());
            setActiveTab(financeTab);
        });

        documentsTab.setOnClickListener(v -> {
            replaceFragment(new DocumentFragment());
            setActiveTab(documentsTab);
        });

        if (savedInstanceState == null) {
            replaceFragment(new GeneralFragment());
            setActiveTab(generalTab);
        }

        String currentUserId = getIntent().getStringExtra("USER_ID");
        Log.d("USER_ID", "Email do usuário: " + currentUserId);
        getStudent(currentUserId);
    }

    private void getStudent(String idStr) {
        int id = Integer.parseInt(idStr);

        List<String> campos = Arrays.asList(
                "id",
                "nome",
                "emails",
                "datanascimento",
                "imagem",
                "camposPersonalizados"
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
                            StudentData currentStudent = response.body().getDados();
                            vm.setStudent(currentStudent);

                            RecentProfilesManager.saveProfile(
                                    ProfileActivity.this,
                                    currentStudent.getId(),
                                    currentStudent.getNome(),
                                    currentStudent.getImagem()
                            );

                            Log.d("API_SUCCESS", "Aluno: " + currentStudent.getNome() +
                                    " / Email: " + currentStudent.getEmailPrincipal() +
                                    " / Nascimento: " + currentStudent.getDataNascimento());
                            getHistory(Integer.parseInt(currentStudent.getId()));
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

    private void getHistory(int contactId) {
        List<String> camposRetorno = Arrays.asList(
                "id",
                "etapaNome",
                "camposPersonalizados"
        );

        RegisterRequest request = new RegisterRequest(
                9,
                "f70e467007e33f442d2b01c37e6e0397",
                contactId,
                camposRetorno
        );

        RubeusClient.getInstance()
                .listarRegistros(request)
                .enqueue(new Callback<RegisterListResponse>() {
                    @Override
                    public void onResponse(Call<RegisterListResponse> call, Response<RegisterListResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            vm.setHistory(response.body().getDados());
                            Log.d("API_SUCCESS", "Histórico com campos personalizados carregado!");
                        } else {
                            Log.e("API_ERROR", "Falha no fetch ou success=false: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterListResponse> call, Throwable t) {
                        Log.e("NETWORK_ERROR", "Erro na requisição", t);
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setActiveTab(Button activeTab) {
        List<Button> allTabs = Arrays.asList(generalTab, historyTab, financeTab, documentsTab);

        for (Button tab : allTabs) {
            if (tab == activeTab) {
                tab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
                tab.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                tab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
                tab.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }
    }
}
