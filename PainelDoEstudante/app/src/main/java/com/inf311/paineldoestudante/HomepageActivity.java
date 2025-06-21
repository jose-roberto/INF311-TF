package com.inf311.paineldoestudante;

import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageActivity extends AppCompatActivity {

    private AutoCompleteTextView searchField;
    private Handler handler = new Handler(Looper.getMainLooper()); //isso aqui vai agendar a busca com um delay
    private Runnable searchRunnable;
    private List<UserData> lastSearchUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        searchField = findViewById(R.id.searchField);
        setupSearch();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homepage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupSearch();
    }

    //configurar toda a lógica de busca.
    private void setupSearch() {
        // cria um listener no campo de buscas
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // se tem alguma coisa no campo de busca, cancela a busca anterior pra nao ficar tendo varias buscas
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // pega o texto
                String query = s.toString().trim();

                // cria a busca a ser executada
                searchRunnable = () -> {
                    // so busca se for por pelo menos 2 caracteres
                    if (!query.isEmpty() && query.length() >= 2) {
                        executeSearch(query);
                    }
//                    else {
//                        //caso seja nao seja >= 2 tira da lista
//                        recentListLayout.removeAllViews();
//                    }
                };

                // faz a consulta esperar meio segundo pra ser feita, esperando o usuario digitar
                handler.postDelayed(searchRunnable, 500);
            }
        });

        searchField.setOnItemClickListener((parent, view, position, id) -> {
            String nomeSelecionado = (String) parent.getItemAtPosition(position);

            // Aqui você pode buscar pelo nome dentro da última lista de usuários encontrados
            // Para isso, guarde a lista numa variável de instância:

            for (UserData user : lastSearchUsers) {
                if (user.getNome().equals(nomeSelecionado)) {
                    Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
                    intent.putExtra("USER_ID", user.getId());
                    startActivity(intent);
                    break;
                }
            }
        });

    }

    private void executeSearch(String query) {
        // colocar depois no SharedPreferences
        int origem = 9;
        String token = "f70e467007e33f442d2b01c37e6e0397";

        UserRequest request = new UserRequest(query, origem, token);

        // chama a funcao de busca e o coloca na fila.
        RubeusClient.getInstance().searchContatos(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().string();
                        Gson gson = new Gson();
                        UserResponse userResponse = gson.fromJson(jsonString, UserResponse.class);
                        if (userResponse.isSuccess()) {
                            List<UserData> users = userResponse.getDados();

                            // Mostrar sugestões de autocomplete
                            List<String> sugestoes = new ArrayList<>();
                            for (UserData user : users) {
                                sugestoes.add(user.getNome()); // ou user.getNome() + " - " + user.getEmail()
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    HomepageActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    sugestoes
                            );

                            runOnUiThread(() -> {
                                lastSearchUsers = users;
                                searchField.setAdapter(adapter);
                                searchField.showDropDown(); // força abrir o dropdown
//                                updateSearchResults(users); // ainda atualiza a tela
                            });
                        }
                    } catch (Exception e) {
                        Log.e("SEARCH_ERROR", "Erro de parsing", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(HomepageActivity.this, "Erro de conexão na busca", Toast.LENGTH_SHORT).show();
                Log.e("SEARCH_FAILURE", "Erro de rede", t);
            }
        });
    }
}

//    private void updateSearchResults(List<UserData> users) {
//        recentListLayout.removeAllViews();
//
//        if (users == null || users.isEmpty()) {
//            return;
//        }
//
//        // define o que vai pegar o xml
//        LayoutInflater inflater = LayoutInflater.from(this);
//        // aqui vai rodar pra cada resultado
//        for (UserData user : users) {
//            //meio que literalmente infla uma view de cartao usando o que eu fiz no xml do cartao
//            View cardView = inflater.inflate(R.layout.item_search_result, recentListLayout, false);
//
//            // pega os textviews do xml e coloca com o nome e email do resultado atual do loop
//            TextView nameTextView = cardView.findViewById(R.id.textView_name);
//            TextView emailTextView = cardView.findViewById(R.id.textView_email);
//            nameTextView.setText(user.getNome());
//            emailTextView.setText(user.getEmail());
//
//            cardView.setOnClickListener(v -> {
//                Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
//                intent.putExtra("USER_ID", user.getId());
//                startActivity(intent);
//            });
//
//            recentListLayout.addView(cardView);
//        }
//    }