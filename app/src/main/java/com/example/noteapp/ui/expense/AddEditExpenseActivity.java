package com.example.noteapp.ui.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.ExpenseDbHelper;
import com.example.noteapp.model.Expense;

import java.util.Calendar;

public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etCategory, etDescription;
    private Button btnPickDate, btnSave;

    private ExpenseDbHelper dbHelper;
    private Expense expense;

    private Calendar selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new ExpenseDbHelper(this);
        selectedDate = Calendar.getInstance();

        Intent intent = getIntent();
        int expenseId = intent.getIntExtra("expense_id", -1);
        String dateFromList = intent.getStringExtra("date");

        if (expenseId != -1) {
            // chỉnh sửa
            expense = getExpenseById(expenseId);
            if (expense != null) {
                etAmount.setText(String.valueOf(expense.getAmount()));
                etCategory.setText(expense.getCategory());
                etDescription.setText(expense.getNote());
                selectedDate = parseDate(expense.getDate());
            }
        } else {
            // thêm mới
            expense = new Expense();
            if (dateFromList != null) {
                selectedDate = parseDate(dateFromList);
            }
        }

        updateDateButtonText();

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private Expense getExpenseById(int id) {
        for (Expense e : dbHelper.getExpensesByDate(getDateString(selectedDate))) {

            if (e.getId() == id) return e;
        }
        return null;
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setNote(description);
        expense.setDate(getDateString(selectedDate));

        if (expense.getId() == 0) {
            dbHelper.addExpense(expense.getAmount(), expense.getDate(), expense.getCategory(), expense.getNote());
            Toast.makeText(this, "Đã thêm chi tiêu", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateExpenseItem(expense);
            Toast.makeText(this, "Đã cập nhật chi tiêu", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate.set(Calendar.YEAR, y);
            selectedDate.set(Calendar.MONTH, m);
            selectedDate.set(Calendar.DAY_OF_MONTH, d);
            updateDateButtonText();
        }, year, month, day);

        dpd.show();
    }

    private void updateDateButtonText() {
        btnPickDate.setText("Ngày: " + getDateString(selectedDate));
    }

    private String getDateString(Calendar calendar) {
        return String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private Calendar parseDate(String dateStr) {
        String[] parts = dateStr.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
        return cal;
    }
}
