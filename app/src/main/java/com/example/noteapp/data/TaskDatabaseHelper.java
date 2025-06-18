package com.example.noteapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.noteapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_db";
    private static final int DATABASE_VERSION = 2;  // Tăng version lên 2

    private static final String TABLE_TASKS = "tasks";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "dueTimeMillis INTEGER," +
                "isCompleted INTEGER," +
                "repeatDays TEXT" +  // Thêm cột repeatDays
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột repeatDays khi nâng cấp lên version 2
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN repeatDays TEXT");
        }
    }

    public long insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("dueTimeMillis", task.getDueTimeMillis());
        values.put("isCompleted", task.isCompleted() ? 1 : 0);
        values.put("repeatDays", task.getRepeatDays());  // Lưu repeatDays
        return db.insert(TABLE_TASKS, null, values);
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks ORDER BY dueTimeMillis ASC", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                task.setDueTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow("dueTimeMillis")));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1);
                task.setRepeatDays(cursor.getString(cursor.getColumnIndexOrThrow("repeatDays")));  // Đọc repeatDays
                list.add(task);
            }
            cursor.close();
        }
        return list;
    }

    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, "id=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Task task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            task.setDueTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow("dueTimeMillis")));
            task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1);
            task.setRepeatDays(cursor.getString(cursor.getColumnIndexOrThrow("repeatDays")));  // Đọc repeatDays
            cursor.close();
            return task;
        }
        return null;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("dueTimeMillis", task.getDueTimeMillis());
        values.put("isCompleted", task.isCompleted() ? 1 : 0);
        values.put("repeatDays", task.getRepeatDays());  // Cập nhật repeatDays
        return db.update(TABLE_TASKS, values, "id = ?", new String[]{String.valueOf(task.getId())});
    }

    public int deleteTask(int taskId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TASKS, "id = ?", new String[]{String.valueOf(taskId)});
    }
}
