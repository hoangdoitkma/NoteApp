package com.example.noteapp.ui.qr;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.noteapp.R;
public class QRCodeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_detail);

        ImageView imageView = findViewById(R.id.imageViewDetail);
        TextView titleView = findViewById(R.id.textViewDetailTitle);

        String path = getIntent().getStringExtra("path");
        String title = getIntent().getStringExtra("title");

        Glide.with(this).load(path).into(imageView);
        titleView.setText(title);
    }
}

