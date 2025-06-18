package com.example.noteapp.ui.expense;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.data.ExpenseDbHelper;
import com.example.noteapp.model.DailyExpenseSummary;
import com.example.noteapp.model.Expense;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseFragment extends Fragment {

    private TextView tvTotalThisMonth, tvTotalLastMonth;
    private RecyclerView recyclerViewDays;
    private Button btnAddExpense;
    private ExpenseDayAdapter adapter;
    private ExpenseDbHelper dbHelper;
    private RecyclerView recyclerViewExpenseDetails;
    private ExpenseDetailAdapter expenseDetailAdapter;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private TextView tvDailyTotal;
    private LinearLayout layoutMonthlySummary;

    private Button btnBackToDays;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        tvTotalThisMonth = view.findViewById(R.id.tvTotalThisMonth);
        tvTotalLastMonth = view.findViewById(R.id.tvTotalLastMonth);
        recyclerViewDays = view.findViewById(R.id.recyclerViewExpenseDay);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        layoutMonthlySummary = view.findViewById(R.id.layoutMonthlySummary);
        tvDailyTotal = view.findViewById(R.id.tvDailyTotal);

        dbHelper = new ExpenseDbHelper(requireContext());

        recyclerViewDays.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewExpenseDetails = view.findViewById(R.id.recyclerViewExpenseDetails);
        recyclerViewExpenseDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewExpenseDetails.setVisibility(View.GONE);
        btnAddExpense.setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), AddExpenseItemActivity.class));
        });
        btnBackToDays = view.findViewById(R.id.btnBackToDays);
        btnBackToDays.setOnClickListener(v -> {
            recyclerViewExpenseDetails.setVisibility(View.GONE);
            btnBackToDays.setVisibility(View.GONE);
            recyclerViewDays.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Expense> allExpenses = dbHelper.getAllExpenses();

        double totalThisMonth = 0;
        double totalLastMonth = 0;

        Calendar now = Calendar.getInstance();
        int thisMonth = now.get(Calendar.MONTH);
        int thisYear = now.get(Calendar.YEAR);

        Calendar lastMonth = (Calendar) now.clone();
        lastMonth.add(Calendar.MONTH, -1);
        int lastMonthInt = lastMonth.get(Calendar.MONTH);
        int lastMonthYear = lastMonth.get(Calendar.YEAR);

        Map<String, List<Expense>> groupedByDate = new TreeMap<>(Collections.reverseOrder());

        for (Expense e : allExpenses) {
            try {
                Date date = sdf.parse(e.getDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                if (month == thisMonth && year == thisYear) {
                    totalThisMonth += e.getAmount();
                } else if (month == lastMonthInt && year == lastMonthYear) {
                    totalLastMonth += e.getAmount();
                }

                String dateStr = sdf.format(date);
                if (!groupedByDate.containsKey(dateStr)) {
                    groupedByDate.put(dateStr, new ArrayList<>());
                }
                groupedByDate.get(dateStr).add(e);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        tvTotalThisMonth.setText(formatCurrency(totalThisMonth));
        tvTotalLastMonth.setText(formatCurrency(totalLastMonth));

        List<DailyExpenseSummary> summaries = new ArrayList<>();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));

        for (Map.Entry<String, List<Expense>> entry : groupedByDate.entrySet()) {
            String date = entry.getKey();
            List<Expense> expenses = entry.getValue();

            double total = 0;
            for (Expense e : expenses) {
                total += e.getAmount();
            }

            String dayOfWeek = "";
            try {
                Date d = sdf.parse(date);
                if (d != null) {
                    dayOfWeek = dayOfWeekFormat.format(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            summaries.add(new DailyExpenseSummary(date, dayOfWeek, total));
        }

        adapter = new ExpenseDayAdapter(summaries, date -> {
            loadExpenseDetailsByDate(date);
            recyclerViewExpenseDetails.setVisibility(View.VISIBLE);
            recyclerViewDays.setVisibility(View.GONE);
            layoutMonthlySummary.setVisibility(View.GONE);
            tvDailyTotal.setVisibility(View.VISIBLE);
            btnBackToDays.setVisibility(View.VISIBLE);

            // Tính tổng chi trong ngày
            double total = 0;
            for (Expense e : dbHelper.getExpensesByDate(date)) {
                total += e.getAmount();
            }
            tvDailyTotal.setText("Tổng chi ngày " + date + ": " + formatCurrency(total));
        });


        recyclerViewDays.setAdapter(adapter);
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }
    private void loadExpenseDetailsByDate(String date) {
        List<Expense> expenses = dbHelper.getExpensesByDate(date);

        if (expenses.isEmpty()) {
            recyclerViewExpenseDetails.setVisibility(View.GONE);
            // Nếu muốn có thông báo "Chưa có khoản chi nào" bạn có thể show TextView ở đây
        } else {
            recyclerViewExpenseDetails.setVisibility(View.VISIBLE);

            if (expenseDetailAdapter == null) {
                expenseDetailAdapter = new ExpenseDetailAdapter(expenses,
                        expense -> {
                            Intent intent = new Intent(getContext(), AddExpenseItemActivity.class);
                            intent.putExtra("expense_id", expense.getId()); // truyền ID khoản chi
                            startActivity(intent);
                        },
                        expense -> {
                            // Xử lý xóa khoản chi
                            dbHelper.deleteExpense(expense.getId());
                            loadExpenseDetailsByDate(date); // load lại sau khi xóa
                            loadData(); // cập nhật tổng chi tiêu
                        });
                recyclerViewExpenseDetails.setAdapter(expenseDetailAdapter);
            } else {
                // Cập nhật lại dữ liệu adapter
                expenseDetailAdapter.updateData(expenses);
            }
        }
    }

}
