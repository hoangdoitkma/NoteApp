// QrImageAdapter.java
package com.example.noteapp.ui.qr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QrImageAdapter extends RecyclerView.Adapter<QrImageAdapter.QrViewHolder> {

    private final List<Uri> qrImages;
    private final Context context;

    public QrImageAdapter(List<Uri> qrImages, Context context) {
        this.qrImages = qrImages;
        this.context = context;
    }

    @NonNull
    @Override
    public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_image, parent, false);
        return new QrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrViewHolder holder, int position) {
        Uri imageUri = qrImages.get(position);
        Picasso.get().load(imageUri).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(imageUri, "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return qrImages.size();
    }

    public static class QrViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public QrViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageQr);
        }
    }
}
