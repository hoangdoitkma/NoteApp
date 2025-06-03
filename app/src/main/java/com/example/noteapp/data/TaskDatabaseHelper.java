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
    private static final int DATABASE_VERSION = 1;

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
                "isCompleted INTEGER DEFAULT 0" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple drop & recreate for now
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public long insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("dueTimeMillis", task.getDueTimeMillis());
        values.put("isCompleted", task.isCompleted() ? 1 : 0);
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
                list.add(task);
            }
            cursor.close();
        }

        return list;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("dueTimeMillis", task.getDueTimeMillis());
        values.put("isCompleted", task.isCompleted() ? 1 : 0);

        return db.update(TABLE_TASKS, values, "id = ?", new String[]{String.valueOf(task.getId())});
    }

    public int deleteTask(int taskId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TASKS, "id = ?", new String[]{String.valueOf(taskId)});
    }
}
