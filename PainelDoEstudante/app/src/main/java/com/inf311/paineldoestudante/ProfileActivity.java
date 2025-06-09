package com.inf311.paineldoestudante;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}