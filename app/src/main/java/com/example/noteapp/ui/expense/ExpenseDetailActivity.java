package com.example.noteapp.ui.expense;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.data.ExpenseDbHelper;
import com.example.noteapp.model.Expense;

import java.util.List;
import com.example.noteapp.R;
public class ExpenseDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseDetailAdapter adapter;
    private ExpenseDbHelper dbHelper;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        recyclerView = findViewById(R.id.recyclerViewExpenseDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new ExpenseDbHelper(this);

        date = getIntent().getStringExtra("date");
        setTitle("Chi tiêu ngày " + date);

        loadExpenseDetails();
    }

    private void loadExpenseDetails() {
        List<Expense> expenses = dbHelper.getExpensesByDate(date);
        adapter = new ExpenseDetailAdapter(expenses, this::editExpense, this::deleteExpense);
        recyclerView.setAdapter(adapter);
    }

    private void editExpense(Expense expense) {
        // TODO: Mở màn hình edit (tương tự AddExpenseItemActivity nhưng truyền dữ liệu để chỉnh sửa)
    }

    private void deleteExpense(Expense expense) {
        dbHelper.deleteExpense(expense.getId());
        loadExpenseDetails();
        Toast.makeText(this, "Đã xóa khoản chi", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseDetails();
    }
}

