package com.example.noteapp.ui.qr;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.data.QRDatabaseHelper;
import com.example.noteapp.model.QRCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class QRCodeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QRCodeAdapter adapter;
    private QRDatabaseHelper dbHelper;
    private FloatingActionButton fabAdd;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_list);

        recyclerView = findViewById(R.id.recyclerViewQRCodes);
        fabAdd = findViewById(R.id.fabAddQRCode);
        dbHelper = new QRDatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadQRCodes();

        fabAdd.setOnClickListener(v -> openImagePicker());

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            saveImageToInternalStorage(imageUri);
                        }
                    }
                }
        );
    }

    private void loadQRCodes() {
        List<QRCode> list = dbHelper.getAllQRCodes();
        if (adapter == null) {
            adapter = new QRCodeAdapter(this, list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setQrCodeList(list);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            String fileName = queryFileName(uri);
            if (fileName == null) fileName = "qr_" + System.currentTimeMillis() + ".png";

            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            if (inputStream != null) inputStream.close();

            dbHelper.insertQRCode(fileName, file.getAbsolutePath(), System.currentTimeMillis());
            loadQRCodes();

            Toast.makeText(this, "Lưu mã QR thành công!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private String queryFileName(Uri uri) {
        String result = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        }
        return result;
    }

    private void shareQRCode(QRCode qrCode) {
        File file = new File(qrCode.getImagePath());
        if (!file.exists()) {
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ mã QR qua"));
    }
}
