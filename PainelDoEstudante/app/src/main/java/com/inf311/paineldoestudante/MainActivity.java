// Local: app/src/main/java/com/inf311/paineldoestudante/MainActivity.java

package com.inf311.paineldoestudante;

// Imports de ferramentas e componentes do Android
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

// Import do GSON para fazer a tradução manual
import com.google.gson.Gson;

// Imports de bibliotecas de suporte do Google (AndroidX)
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Imports das ferramentas do Retrofit e OkHttp
import okhttp3.ResponseBody; // Importamos o "corpo bruto"
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button continueButton;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
            realizarLogin(email);
        });
    }

    private void realizarLogin(String email) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        continueButton.setEnabled(false);


        int origem = 9; // aqui é o campo Código do Canal do painel do perfil -> integracoes -> canais/api
        String token = "f70e467007e33f442d2b01c37e6e0397"; // aqui é o campo token da mesma pagina

        LoginRequest loginRequest = new LoginRequest(email, null, origem, token);


        RetrofitClient.getInstance().loginGestor(loginRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                loadingProgressBar.setVisibility(View.GONE);
                continueButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Tradução manual -> html -> aplication
                        String jsonString = response.body().string(); // ega a resposta como texto
                        Gson gson = new Gson(); 
                        UserResponse userResponse = gson.fromJson(jsonString, UserResponse.class); //faz de fato a traducao


                        if (userResponse.isSuccess() && userResponse.getDados() != null && !userResponse.getDados().isEmpty()) {
                            //aqui é quando deu certo
                            UserData gestor = userResponse.getDados().get(0);
                            Toast.makeText(MainActivity.this, "SUCESSO! Bem-vindo, " + gestor.getNome(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Email não encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Erro ao processar a resposta do servidor.", Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN_PARSE_ERRO", "Erro de tradução do JSON", e);
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
}