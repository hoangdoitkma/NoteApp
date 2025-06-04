package com.example.noteapp.ui.qr;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

        int width = getResources().getDisplayMetrics().widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = width; // chiều cao bằng chiều ngang (vuông)
        imageView.setLayoutParams(params);

        imageView.setVisibility(View.VISIBLE);

        String path = getIntent().getStringExtra("path");
        String title = getIntent().getStringExtra("title");

        Glide.with(this).load(path).into(imageView);
        titleView.setText(title);

    }
}

