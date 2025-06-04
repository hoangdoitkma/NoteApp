package com.example.noteapp.ui.qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noteapp.R;
import com.example.noteapp.model.QRCode;

import java.util.List;

public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(QRCode qrCode);
    }

    private Context context;
    private List<QRCode> qrCodeList;
    private OnItemClickListener listener;

    public QRCodeAdapter(Context context, List<QRCode> qrCodeList) {
        this.context = context;
        this.qrCodeList = qrCodeList;
    }

    public void setQrCodeList(List<QRCode> qrCodeList) {
        this.qrCodeList = qrCodeList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qr_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QRCode qrCode = qrCodeList.get(position);
        Glide.with(context)
                .load(qrCode.getImagePath())
                .placeholder(R.drawable.ic_qr_placeholder)
                .centerCrop()
                .into(holder.imageView);
        holder.textView.setText(qrCode.getTitle());

        // Hiệu ứng click ripple (tương thích Android 5+)
        holder.cardView.setForeground(ContextCompat.getDrawable(context, R.drawable.ripple_effect));

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(qrCode);
        });
    }

    @Override
    public int getItemCount() {
        return qrCodeList != null ? qrCodeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageQr);
            textView = itemView.findViewById(R.id.textViewTitle);
            cardView = itemView.findViewById(R.id.cardViewQr);
        }
    }
}
