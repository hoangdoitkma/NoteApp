package com.example.noteapp.ui.qr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;

public class CropPreviewActivity extends AppCompatActivity {

    private ImageView imageViewPreview;
    private Button btnSave, btnCancel;
    private Uri croppedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_preview);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        btnSave = findViewById(R.id.btnSaveImage);
        btnCancel = findViewById(R.id.btnCancel);

        croppedImageUri = getIntent().getParcelableExtra("croppedImageUri");

        if (croppedImageUri != null) {
            imageViewPreview.setImageURI(croppedImageUri);
        } else {
            Toast.makeText(this, "Không nhận được ảnh đã cắt", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSave.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.setData(croppedImageUri);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
