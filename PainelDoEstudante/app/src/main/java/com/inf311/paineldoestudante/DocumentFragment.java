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
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class DocumentFragment extends Fragment {

    private ProfileViewModel viewModel;
    private LinearLayout documentsListLayout;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_document, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        documentsListLayout = view.findViewById(R.id.layout_documents_list); // Garanta que este ID exista no seu XML
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getStudent().observe(getViewLifecycleOwner(), this::displayDocuments);
    }

    private void displayDocuments(StudentData studentData) {
        if (studentData == null || studentData.getCamposPersonalizados() == null) {
            // Se não houver dados ou campos personalizados, limpa a tela e sai.
            if(documentsListLayout != null) documentsListLayout.removeAllViews();
            return;
        }

        documentsListLayout.removeAllViews();

        for (UserProperties prop : studentData.getCamposPersonalizados()) {
            // Procura pelo campo personalizado com o nome "Documentos"
            if ("documentos".equalsIgnoreCase(prop.getNome())) {

                // Verifica se o valor não é nulo e se é de fato um array JSON
                if (prop.getValor() != null && prop.getValor().isJsonArray()) {

                    // ****** AQUI ESTÁ A CORREÇÃO PRINCIPAL ******
                    // 1. Definimos o tipo de destino como uma Lista de Strings, que é o que a API envia.
                    Type stringListType = new TypeToken<List<String>>() {}.getType();

                    // 2. Convertemos o JSON para uma lista de URLs (Strings).
                    List<String> urls = gson.fromJson(prop.getValor(), stringListType);

                    if (urls != null) {
                        // 3. Iteramos sobre a lista de URLs que acabamos de obter.
                        for (String url : urls) {
                            if (url == null || url.isEmpty()) continue;

                            // 4. Extraímos o nome do arquivo da URL para exibição.
                            String fileName = "Documento"; // Nome padrão caso a extração falhe
                            try {
                                String path = url;
                                int queryIndex = path.indexOf('?'); // Encontra o início dos parâmetros da URL
                                if (queryIndex != -1) {
                                    path = path.substring(0, queryIndex); // Remove os parâmetros
                                }
                                fileName = path.substring(path.lastIndexOf('/') + 1); // Pega a última parte do caminho
                            } catch (Exception e) {
                                // Se der erro, usamos o nome padrão.
                                e.printStackTrace();
                            }

                            // 5. Criamos nosso objeto FileItem manualmente com o nome extraído e a URL original.
                            FileItem file = new FileItem(fileName, url);

                            // 6. Adicionamos a view do documento na lista.
                            addDocumentViewToList(file);
                        }
                    }
                }
                // Como já encontramos o campo "documentos", podemos parar o loop.
                break;
            }
        }
    }

    private void addDocumentViewToList(FileItem file) {
        if (getContext() == null) return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View docView = inflater.inflate(R.layout.item_document, documentsListLayout, false);
        TextView docNameTextView = docView.findViewById(R.id.textView_document_name);
        docNameTextView.setText(file.getNome());
        docView.setOnClickListener(v -> downloadFile(file.getUrl(), file.getNome()));
        documentsListLayout.addView(docView);
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