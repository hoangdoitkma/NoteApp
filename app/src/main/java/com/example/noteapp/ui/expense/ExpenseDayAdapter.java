package com.example.noteapp.ui.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.DailyExpenseSummary;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseDayAdapter extends RecyclerView.Adapter<ExpenseDayAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String date);
    }

    private final List<DailyExpenseSummary> data;
    private final OnItemClickListener listener;

    public ExpenseDayAdapter(List<DailyExpenseSummary> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyExpenseSummary item = data.get(position);

        holder.tvDate.setText(item.getDate());
        holder.tvDayOfWeek.setText(item.getDayOfWeek());
        holder.tvTotal.setText(formatCurrency(item.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item.getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDayOfWeek, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvTotal = itemView.findViewById(R.id.tvTotalAmount);
        }
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
}

