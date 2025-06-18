package com.example.noteapp.ui.expense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.data.ExpenseDbHelper;
import com.example.noteapp.model.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseItemActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private Button btnPickDate, btnSave;
    private ExpenseDbHelper dbHelper;
    private String selectedDate;

    private int expenseId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new ExpenseDbHelper(this);

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Tiền ăn", "Tiền nhà", "Đi chơi", "Khác"});
        spinnerCategory.setAdapter(adapter);

        // Set ngày mặc định là hôm nay
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(calendar.getTime());
        btnPickDate.setText(selectedDate);

        // Lấy dữ liệu nếu đang sửa
        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId != -1) {
            isEditMode = true;
            loadExpenseData(expenseId);
        }

        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(selectedDate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    btnPickDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadExpenseData(int id) {
        Expense expense = dbHelper.getExpenseById(id);
        if (expense != null) {
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDescription.setText(expense.getNote());
            selectedDate = expense.getDate();
            btnPickDate.setText(selectedDate);

            String[] categories = {"Tiền ăn", "Tiền nhà", "Đi chơi", "Khác"};
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(expense.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            btnSave.setText("Cập nhật");
        }
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = selectedDate;

        Expense expense = new Expense(amount, category, date, note);

        if (isEditMode) {
            expense.setId(expenseId);
            dbHelper.updateExpense(expense);
            Toast.makeText(this, "Đã cập nhật khoản chi", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertExpense(expense);
            Toast.makeText(this, "Đã thêm khoản chi", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
