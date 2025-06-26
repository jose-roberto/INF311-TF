package com.inf311.paineldoestudante;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocumentFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LinearLayout documentsListLayout;
    private LinearLayout certificatesListLayout;
    private Gson gson = new Gson();
    private TextView profileUsername;
    private ImageView profilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_document, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::updateStudentHeader);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::displayData);
    }

    private void initializeViews(View view) {
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePicture = view.findViewById(R.id.profilePicture);
        documentsListLayout = view.findViewById(R.id.layout_documents_list);
        certificatesListLayout = view.findViewById(R.id.layout_certificates_list);
    }

    private void updateStudentHeader(StudentData student) {
        if (student != null) {
            profileUsername.setText(student.getNome());
        }
    }

    private void displayData(StudentData studentData) {
        if (studentData == null || studentData.getCamposPersonalizados() == null) {
            if (documentsListLayout != null) documentsListLayout.removeAllViews();
            if (certificatesListLayout != null) certificatesListLayout.removeAllViews();
            return;
        }

        // Limpa as listas antes de adicionar novos itens
        documentsListLayout.removeAllViews();
        certificatesListLayout.removeAllViews();

        // Itera por todos os campos personalizados
        for (UserProperties prop : studentData.getCamposPersonalizados()) {
            if (prop.getValor() == null || !prop.getValor().isJsonArray()) {
                continue;
            }

            // Usamos um switch para direcionar os dados para a lista correta
            // Usamos toLowerCase() para evitar problemas com "Documentos" vs "documentos"
            switch (prop.getNome().toLowerCase()) {
                case "documentos":
                    populateFileList(prop.getValor(), documentsListLayout);
                    break;
                case "certificados":
                    populateFileList(prop.getValor(), certificatesListLayout);
                    break;
            }
        }
    }

    private void populateFileList(JsonElement jsonValue, LinearLayout targetLayout) {
        Type stringListType = new TypeToken<List<String>>() {}.getType();
        List<String> urls = gson.fromJson(jsonValue, stringListType);

        if (urls != null && !urls.isEmpty()) {
            for (String url : urls) {
                if (url == null || url.isEmpty()) continue;

                String fileName = extractFileNameFromUrl(url);
                FileItem file = new FileItem(fileName, url);
                addDocumentViewToList(file, targetLayout);
            }
        }
    }


    private void addDocumentViewToList(FileItem file, LinearLayout parentLayout) {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View docView = inflater.inflate(R.layout.item_document, parentLayout, false);

        TextView docNameTextView = docView.findViewById(R.id.textView_document_name);
        docNameTextView.setText(file.getNome());

        docView.setOnClickListener(v -> downloadFile(file.getUrl(), file.getNome()));
        parentLayout.addView(docView);
    }
    private String extractFileNameFromUrl(String url) {
        String fileName = "Arquivo"; // Nome padrão
        try {
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            String path = decodedUrl;
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                path = path.substring(0, queryIndex);
            }
            String fullFileName = path.substring(path.lastIndexOf('/') + 1);
            int hyphenIndex = fullFileName.indexOf('-');
            //os arquivos vem da rubebus com varias letras e numeros, separados com hifen
            //do nome do arquivo. entao aqui a gente so tira o hifen
            if (hyphenIndex != -1 && hyphenIndex < fullFileName.length() - 1) {
                // Se encontrou um hífen, pega tudo que vem DEPOIS dele.
                fileName = fullFileName.substring(hyphenIndex + 1);
            } else {
                // Se não houver hífen, usa o nome completo como está.
                fileName = fullFileName;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // Lidar com o erro de decodificação
        } catch (Exception e) {
            e.printStackTrace(); // Lidar com outros erros de parsing
        }
        return fileName;
    }
    private void downloadFile(String url, String fileName) {
        if (getContext() == null || url == null || url.isEmpty()) return;

        new Thread(() -> {
            try {
                File cacheDir = requireContext().getCacheDir();
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
                        Toast.makeText(getContext(), "Erro ao baixar o PDF", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void openPdf(File pdfFile) {
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
                    null
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
                    "Instale o Google PDF Viewer para melhor compatibilidade",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Erro técnico: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}