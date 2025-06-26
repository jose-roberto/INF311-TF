package com.inf311.paineldoestudante;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

public class DocumentFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LinearLayout documentsListLayout;
    private LinearLayout certificatesListLayout;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_document, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        documentsListLayout = view.findViewById(R.id.layout_documents_list);
        certificatesListLayout = view.findViewById(R.id.layout_certificates_list);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::displayData);
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
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            DownloadManager dm = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) dm.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Não foi possível iniciar o download.", Toast.LENGTH_SHORT).show();
        }
    }
}