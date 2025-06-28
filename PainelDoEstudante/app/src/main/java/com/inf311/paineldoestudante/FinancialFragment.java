package com.inf311.paineldoestudante;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinancialFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LinearLayout paymentsContainer;
    private TextView profileUsername;
    private ImageView profilePicture;
    private TextView valueOpenPayments;
    private TextView valueValuePayments;
    private TextView yearFilterText;

    private List<PaymentItem> allPayments = new ArrayList<>();
    private Map<String, List<PaymentItem>> paymentsByYear = new HashMap<>();
    private String currentYearFilter = "2025";
    private List<String> boletosUrls = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_financial, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), student -> {
            updateStudentHeader(student);
            processFinancialData(student);
        });

        Log.d("DEBUG_FINANCEIRO", "Fragment carregado!");
    }

    private void initializeViews(View view) {
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePicture = view.findViewById(R.id.profilePicture);
        paymentsContainer = view.findViewById(R.id.paymentsContainer);
        valueOpenPayments = view.findViewById(R.id.valueOpenPayments);
        valueValuePayments = view.findViewById(R.id.valueValuePayments);
        yearFilterText = view.findViewById(R.id.yearFilterText);

        yearFilterText.setOnClickListener(v -> showYearFilterDialog());
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

    private void processFinancialData(StudentData student) {
        if (student == null || student.getCamposPersonalizados() == null) {
            return;
        }

        allPayments.clear();
        paymentsByYear.clear();

        String boletoUrl = null;
        for (UserProperties prop : student.getCamposPersonalizados()) {
            if ("Boleto".equals(prop.getNome()) && prop.getValor() != null) {
                JsonElement valorElement = prop.getValor();
                if (valorElement != null && !valorElement.isJsonNull()) {
                    boletoUrl = valorElement.getAsString();
                }
                if (boletoUrl != null && !boletoUrl.isEmpty()) {
                    break;
                }
            }
        }

        for (UserProperties prop : student.getCamposPersonalizados()) {
            Type listType = new TypeToken<List<String>>() {}.getType();

            if ("Mensalidade em aberto".equals(prop.getNome())) {
                List<String> openMonths = new Gson().fromJson(prop.getValor(), listType);
                if (openMonths != null) {
                    for (String monthYear : openMonths) {
                        PaymentItem item = new PaymentItem(monthYear, false);
                        if (boletoUrl != null) {
                            item.setBoletoUrl(boletoUrl);
                        }
                        allPayments.add(item);
                        addToYearMap(item);
                    }
                }
            }

            if ("Mensalidade pagas".equals(prop.getNome())) {
                List<String> paidMonths = new Gson().fromJson(prop.getValor(), listType);
                if (paidMonths != null) {
                    Log.d("DEBUG_FINANCEIRO", "Meses pagos: " + paidMonths);
                    for (String monthYear : paidMonths) {
                        PaymentItem item = new PaymentItem(monthYear, true);

                        if (boletoUrl != null) {
                            item.setBoletoUrl(boletoUrl);
                        }

                        allPayments.add(item);
                        addToYearMap(item);
                    }
                }
            }
        }

        updatePaymentList();
        updateOverview();
    }


    private void addToYearMap(PaymentItem item) {
        String year = item.getYear();
        if (!paymentsByYear.containsKey(year)) {
            paymentsByYear.put(year, new ArrayList<>());
        }
        paymentsByYear.get(year).add(item);
    }

    private void updatePaymentList() {
        paymentsContainer.removeAllViews();

        List<PaymentItem> currentYearPayments = paymentsByYear.get(currentYearFilter);
        if (currentYearPayments == null || currentYearPayments.isEmpty()) {
            addEmptyPaymentView("Nenhum registro encontrado para " + currentYearFilter);
            return;
        }

        for (int i = 0; i < currentYearPayments.size(); i++) {
            PaymentItem payment = currentYearPayments.get(i);
            addPaymentItemView(payment, i);
        }
    }

    private void addPaymentItemView(PaymentItem payment, int position) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(R.layout.item_financial, paymentsContainer, false);

        TextView monthText = itemView.findViewById(R.id.month);
        TextView valueText = itemView.findViewById(R.id.value);
        TextView situationText = itemView.findViewById(R.id.situation);

        String month = payment.monthYear.split("_")[0];
        monthText.setText(month);
        valueText.setText("R$ 150,00");
        situationText.setText(payment.isPaid() ? "Pago" : "Aberto");

        if (payment.getBoletoUrl() != null) {
            monthText.setOnClickListener(v -> {
                downloadAndOpenBoleto(payment.getBoletoUrl());

                v.setPressed(true);
                new Handler().postDelayed(() -> v.setPressed(false), 200);
            });

            monthText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_download, 0, 0, 0);
            monthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            monthText.setClickable(true);
            monthText.setFocusable(true);
        } else {
            monthText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            monthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            monthText.setClickable(false);
        }

        paymentsContainer.addView(itemView);
    }

    private void downloadAndOpenBoleto(String url) {
        if (getContext() == null || url == null || url.isEmpty()) return;

        new Thread(() -> {
            try {
                File cacheDir = requireContext().getCacheDir();
                String fileName = extractFileNameFromUrl(url);
                File tempFile = new File(cacheDir, fileName);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    requireActivity().runOnUiThread(() -> openPdf(tempFile));
                } else {
                    throw new IOException("Falha no download");
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Erro ao baixar o boleto", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private String extractFileNameFromUrl(String url) {
        String fileName = "boleto.pdf";
        try {
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            String path = decodedUrl;
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                path = path.substring(0, queryIndex);
            }
            String fullFileName = path.substring(path.lastIndexOf('/') + 1);
            int hyphenIndex = fullFileName.indexOf('-');
            if (hyphenIndex != -1 && hyphenIndex < fullFileName.length() - 1) {
                fileName = fullFileName.substring(hyphenIndex + 1);
            } else {
                fileName = fullFileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void openPdf(File pdfFile) {
        if (!pdfFile.exists()) {
            return;
        }

        try {
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    pdfFile
            );

            String[] pdfViewers = {
                    "com.google.android.apps.pdfviewer",
                    "com.adobe.reader",
                    "cn.wps.moffice_eng",
                    null // Tenta com o visualizador padrão
            };

            for (String viewer : pdfViewers) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    if (viewer != null) {
                        intent.setPackage(viewer);
                    }

                    startActivity(intent);
                    pdfFile.deleteOnExit();
                    return;
                } catch (ActivityNotFoundException e) {
                    continue;
                }
            }

            Toast.makeText(getContext(),
                    "Instale um visualizador de PDF como Adobe Reader",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Erro ao abrir o boleto: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addEmptyPaymentView(String message) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TextView emptyText = (TextView) inflater.inflate(R.layout.empty_message, paymentsContainer, false);
        emptyText.setText(message);
        paymentsContainer.addView(emptyText);
    }

    private void updateOverview() {
        List<PaymentItem> currentPayments = paymentsByYear.get(currentYearFilter);
        if (currentPayments == null) {
            valueOpenPayments.setText("0");
            valueValuePayments.setText("R$ 0,00");
            return;
        }

        int openCount = 0;
        double totalValue = 0.0;

        for (PaymentItem item : currentPayments) {
            if (!item.isPaid()) {
                openCount++;
                totalValue += 150.00;
            }
        }

        valueOpenPayments.setText(String.valueOf(openCount));
        valueValuePayments.setText(String.format("R$ %.2f", totalValue));
    }

    private void showYearFilterDialog() {
        List<String> availableYears = new ArrayList<>(paymentsByYear.keySet());
        if (availableYears.isEmpty()) {
            availableYears.add(currentYearFilter);
        }

        availableYears.sort((a, b) -> b.compareTo(a));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecione o ano");
        builder.setItems(availableYears.toArray(new String[0]), (dialog, which) -> {
            currentYearFilter = availableYears.get(which);
            yearFilterText.setText(currentYearFilter);
            updatePaymentList();
            updateOverview();
        });
        builder.show();
    }

    private static class PaymentItem {
        String monthYear;
        boolean paid;
        String boletoUrl;

        PaymentItem(String monthYear, boolean paid) {
            this.monthYear = monthYear;
            this.paid = paid;
        }

        public void setBoletoUrl(String url) {
            this.boletoUrl = url;
        }

        public String getBoletoUrl() {
            return boletoUrl;
        }

        String getYear() {
            return monthYear.split("_")[1];
        }

        boolean isPaid() {
            return paid;
        }
    }
}