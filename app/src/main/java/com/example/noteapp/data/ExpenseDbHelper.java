package com.example.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import com.example.noteapp.model.DailyExpenseSummary;
import com.example.noteapp.model.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "expenses.db";
    private static final int DB_VERSION = 1;

    public ExpenseDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL," +
                "category TEXT," +
                "date TEXT," +
                "note TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }

    public void insertExpense(Expense expense) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        db.insert("expenses", null, values);
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null);
        while (cursor.moveToNext()) {
            list.add(new Expense(
                    cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("note"))
            ));
        }
        cursor.close();
        return list;
    }

    // ✅ Tổng chi tiêu theo ngày
    public double getTotalExpenseByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM expenses WHERE date = ?", new String[]{date});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    // ✅ Lấy chi tiêu theo ngày
    public List<Expense> getExpensesByDate(String date) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE date = ? ORDER BY id DESC",
                new String[]{date});
        while (cursor.moveToNext()) {
            Expense expense = new Expense(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("note"))
            );
            // Nếu có trường 'type' trong DB
            int typeIndex = cursor.getColumnIndex("type");
            if(typeIndex != -1) {
                expense.setType(cursor.getString(typeIndex));
            }
            list.add(expense);
        }
        cursor.close();
        return list;
    }


    // ✅ Lấy tất cả ngày đã có chi tiêu (không trùng lặp)
    public List<String> getDistinctExpenseDates() {
        List<String> dates = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT date FROM expenses ORDER BY date DESC", null);
        while (cursor.moveToNext()) {
            dates.add(cursor.getString(0));
        }
        cursor.close();
        return dates;
    }

    // ✅ Cập nhật chi tiêu
    public void updateExpense(int id, Expense expense) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());
        db.update("expenses", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // ✅ Xóa chi tiêu
    public void deleteExpense(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public void addExpense(double amount, String date, String category, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("date", date);
        values.put("category", category);
        values.put("note", note);

        db.insert("expenses", null, values);
        db.close();
    }

    public void updateExpenseItem(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate());
        values.put("category", expense.getCategory());
        values.put("note", expense.getNote());

        db.update("expenses", values, "id = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
    }

    private String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi"));
            String day = dayFormat.format(date); // ví dụ: "thứ hai"

            // Viết hoa chữ đầu
            return day.substring(0,1).toUpperCase() + day.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public double getTotalExpenseForMonth(String yearMonth) {
        double total = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM expenses WHERE date LIKE ?",
                new String[]{yearMonth + "%"}
        );

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
    public List<Expense> getExpensesByDateExpenses(String date) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, amount, category, date, note FROM expenses WHERE date = ? ORDER BY id DESC",
                new String[]{date}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double amount = cursor.getDouble(1);
            String category = cursor.getString(2);
            String dateStr = cursor.getString(3);
            String note = cursor.getString(4);

            Expense expense = new Expense(id, amount, category, dateStr, note);
            list.add(expense);
        }
        cursor.close();
        return list;
    }
    public List<DailyExpenseSummary> getDailySummariesForCurrentMonth() throws ParseException {
        List<DailyExpenseSummary> summaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy ngày đầu và cuối tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Query tổng chi tiêu theo ngày
        String query = "SELECT date, SUM(amount) as total " +
                "FROM expenses " +
                "WHERE date BETWEEN ? AND ? " +
                "GROUP BY date " +
                "ORDER BY date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{startOfMonth, endOfMonth});
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                Date d = sdf.parse(date);
                String dayOfWeek = d != null ? dayFormat.format(d) : "";
                summaries.add(new DailyExpenseSummary(date, dayOfWeek, total));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return summaries;
    }
    public List<DailyExpenseSummary> getDailyExpenseSummaries() throws ParseException {
        List<DailyExpenseSummary> summaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT date, SUM(amount) as total " +
                "FROM expenses GROUP BY date ORDER BY date DESC";

        Cursor cursor = db.rawQuery(query, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi"));

        if (cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));

                Date date = sdf.parse(dateStr);
                String dayOfWeek = dayFormat.format(date);

                summaries.add(new DailyExpenseSummary(dateStr, dayOfWeek, total));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return summaries;
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("expenses", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
            cursor.close();

            Expense expense = new Expense(amount, category, date, note);
            expense.setId(id);
            return expense;
        }
        return null;
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("date", expense.getDate());
        values.put("note", expense.getNote());

        db.update("expenses", values, "id = ?", new String[]{String.valueOf(expense.getId())});
    }


}
