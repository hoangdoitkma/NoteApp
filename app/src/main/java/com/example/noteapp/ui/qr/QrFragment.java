// QrFragment.java
package com.example.noteapp.ui.qr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.data.QrDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class QrFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> qrImageUris = new ArrayList<>();
    private QrImageAdapter adapter;
    private QrDatabaseHelper qrDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        Button btnAdd = view.findViewById(R.id.btn_add_qr);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_qr);

        qrDatabaseHelper = new QrDatabaseHelper(getContext());

        List<String> savedUris = qrDatabaseHelper.getAllImageUris();
        for (String uriString : savedUris) {
            qrImageUris.add(Uri.parse(uriString));
        }

        adapter = new QrImageAdapter(qrImageUris, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> openGallery());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            qrDatabaseHelper.insertImage(imageUri.toString());
            qrImageUris.add(imageUri);
            adapter.notifyDataSetChanged();
        }
    }
}