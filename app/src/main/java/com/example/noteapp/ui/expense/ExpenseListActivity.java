package com.example.noteapp.ui.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.data.ExpenseDbHelper;
import com.example.noteapp.model.Expense;

import java.util.Calendar;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private Button btnAddExpense, btnPickDate;
    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private ExpenseDbHelper dbHelper;

    private Calendar selectedDate;

    private static final int REQUEST_ADD_EDIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnPickDate = new Button(this);
        btnPickDate.setText("Chọn ngày");
        btnPickDate.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout) findViewById(R.id.rootLayout)).addView(btnPickDate, 1); // thêm nút chọn ngày phía dưới nút thêm

        rvExpenses = findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new ExpenseDbHelper(this);

        selectedDate = Calendar.getInstance();

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("date", getDateString(selectedDate));
            startActivityForResult(intent, REQUEST_ADD_EDIT);
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());

        loadExpensesForDate(getDateString(selectedDate));
    }

    private void loadExpensesForDate(String date) {
        List<Expense> expenses = dbHelper.getExpensesByDate(date);
        if (adapter == null) {
            adapter = new ExpenseAdapter(expenses, new ExpenseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Expense expense) {
                    Intent intent = new Intent(ExpenseListActivity.this, AddEditExpenseActivity.class);
                    intent.putExtra("expense_id", expense.getId());
                    startActivityForResult(intent, REQUEST_ADD_EDIT);
                }

                @Override
                public void onItemLongClick(Expense expense) {
                    dbHelper.deleteExpense(expense.getId());
                    Toast.makeText(ExpenseListActivity.this, "Đã xóa khoản chi", Toast.LENGTH_SHORT).show();
                    loadExpensesForDate(getDateString(selectedDate));
                }
            });
            rvExpenses.setAdapter(adapter);
        } else {
            adapter.updateList(expenses);
        }
    }

    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate.set(Calendar.YEAR, y);
            selectedDate.set(Calendar.MONTH, m);
            selectedDate.set(Calendar.DAY_OF_MONTH, d);
            loadExpensesForDate(getDateString(selectedDate));
        }, year, month, day);

        dpd.show();
    }

    private String getDateString(Calendar calendar) {
        return String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDIT) {
            loadExpensesForDate(getDateString(selectedDate));
        }
    }
}
