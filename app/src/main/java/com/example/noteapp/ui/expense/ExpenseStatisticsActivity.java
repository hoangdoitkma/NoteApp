package com.example.noteapp.ui.expense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.example.noteapp.R;
import com.example.noteapp.data.ExpenseDbHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseStatisticsActivity extends AppCompatActivity {

    private AnyChartView anyChartView;
    private ExpenseDbHelper dbHelper;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_statistics);

        anyChartView = findViewById(R.id.any_chart_view);
        dbHelper = new ExpenseDbHelper(this);
        calendar = Calendar.getInstance();

        loadDailyExpenseChart();
    }

    private void loadDailyExpenseChart() {
        Cartesian cartesian = AnyChart.column();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<DataEntry> data = new ArrayList<>();

        for (int day = 1; day <= maxDay; day++) {
            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            double total = dbHelper.getTotalExpenseByDate(dateStr);
            data.add(new ValueDataEntry(day, total));
        }

        cartesian.column(data);
        cartesian.title("Thống kê chi tiêu theo ngày");
        cartesian.yAxis(0).title("Số tiền (VND)");
        cartesian.xAxis(0).title("Ngày trong tháng");

        anyChartView.setChart(cartesian);
    }
}
