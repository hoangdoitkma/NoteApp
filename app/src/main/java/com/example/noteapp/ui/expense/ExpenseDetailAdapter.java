package com.example.noteapp.ui.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Expense;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseDetailAdapter extends RecyclerView.Adapter<ExpenseDetailAdapter.ViewHolder> {

    private List<Expense> expenses;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnEditClickListener {
        void onEdit(Expense expense);
    }

    public interface OnDeleteClickListener {
        void onDelete(Expense expense);
    }

    public ExpenseDetailAdapter(List<Expense> expenses, OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.expenses = expenses;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ExpenseDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseDetailAdapter.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.tvCategory.setText(expense.getCategory());
        holder.tvAmount.setText(formatCurrency(expense.getAmount()));
        holder.tvNote.setText(expense.getNote());

        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(expense));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(expense));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvNote;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }
    public void updateData(List<Expense> newExpenses) {
        this.expenses.clear();
        this.expenses.addAll(newExpenses);
        notifyDataSetChanged();
    }

}

