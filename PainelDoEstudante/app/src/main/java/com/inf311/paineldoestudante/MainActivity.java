package com.inf311.paineldoestudante;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import com.google.gson.Gson;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button continueButton;
    private ProgressBar loadingProgressBar;

    public static final String APP_PREFERENCES = "AppPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            Log.d("SESSAO", "Usuário já está logado. Pulando para a Homepage.");
            irParaHomepage();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailInput);
        continueButton = findViewById(R.id.continueButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        continueButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, insira o email", Toast.LENGTH_SHORT).show();
                return;
            }
            fazLogin(email);
        });
    }

    private void fazLogin(String email) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        continueButton.setEnabled(false);

        int origem = 9; // aqui é o campo Código do Canal do painel do perfil -> integracoes -> canais/api
        String token = "f70e467007e33f442d2b01c37e6e0397"; // aqui é o campo token da mesma pagina

        LoginRequest loginRequest = new LoginRequest(email, null, origem, token);

        RubeusClient.getInstance().loginGestor(loginRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                loadingProgressBar.setVisibility(View.GONE);
                continueButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Tradução manual -> html -> aplication
                        String jsonString = response.body().string();// ega a resposta como texto
                        Gson gson = new Gson();
                        UserResponse userResponse = gson.fromJson(jsonString, UserResponse.class);//faz de fato a traducao

                        if (userResponse.isSuccess() && userResponse.getDados() != null && !userResponse.getDados().isEmpty()) {
                            //aqui é quando deu certo
                            UserData gestor = userResponse.getDados().get(0);
                            Toast.makeText(MainActivity.this, "Bem-vindo, " + gestor.getNome(), Toast.LENGTH_LONG).show();

                            salvarSessao(gestor.getId(), gestor.getNome());
                            irParaHomepage();

                        } else {
                            Toast.makeText(MainActivity.this, "Email não encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Erro ao processar a resposta.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erro no servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                continueButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Falha na conexão.", Toast.LENGTH_LONG).show();
                Log.e("LOGIN_FALHA", "Erro: " + t.getMessage());
            }
        });
    }

    private void irParaHomepage() {
        Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
        startActivity(intent);
        finish();
    }

    private void salvarSessao(String userId, String userName) {
        Log.d("SESSAO", "Salvando sessão para o usuário: " + userName);
        SharedPreferences prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);

        editor.apply();
    }
}