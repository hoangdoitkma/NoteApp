package com.example.noteapp.ui.todos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.noteapp.R;
import com.example.noteapp.data.TaskDatabaseHelper;
import com.example.noteapp.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnPickDate, btnPickTime, btnRepeatDays, btnSave;
    private static final int REQUEST_CODE_RINGTONE = 123;
    private int year, month, day, hour, minute;
    private long dueTimeMillis = 0;
    private List<Integer> repeatDaysSelected = new ArrayList<>();
    private Uri selectedRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private TextView txtRingtone;
    private Button btnChooseRingtone;
    private TaskDatabaseHelper dbHelper;
    private int taskId = -1;
    private Task editingTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thêm công việc");
        txtRingtone = findViewById(R.id.txtRingtone);
        btnChooseRingtone = findViewById(R.id.btnChooseRingtone);
        btnChooseRingtone.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Chọn nhạc chuông");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        });
        // Hiển thị tên nhạc chuông mặc định
        updateRingtoneTitle();
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnRepeatDays = findViewById(R.id.btnRepeatDays);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new TaskDatabaseHelper(this);

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnRepeatDays.setOnClickListener(v -> showRepeatDaysDialog());

        btnSave.setOnClickListener(v -> saveTask());
        taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            editingTask = dbHelper.getTaskById(taskId);
            if (editingTask != null) {
                // Gán dữ liệu lên giao diện
                edtTitle.setText(editingTask.getTitle());
                edtDescription.setText(editingTask.getDescription());

                // Gán thời gian nếu có
                if (editingTask.getDueTimeMillis() > 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(editingTask.getDueTimeMillis());

                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    hour = cal.get(Calendar.HOUR_OF_DAY);
                    minute = cal.get(Calendar.MINUTE);

                    btnPickDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                    btnPickTime.setText(String.format("%02d:%02d", hour, minute));
                    dueTimeMillis = editingTask.getDueTimeMillis();
                }

                // Gán ngày lặp lại nếu có
                String repeatDaysStr = editingTask.getRepeatDays(); // VD: "0,1,3"
                if (repeatDaysStr != null && !repeatDaysStr.isEmpty()) {
                    String[] dayArr = repeatDaysStr.split(",");
                    repeatDaysSelected.clear();
                    String[] days = {"Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
                    StringBuilder sb = new StringBuilder();
                    for (String s : dayArr) {
                        int d = Integer.parseInt(s);
                        repeatDaysSelected.add(d);
                        sb.append(days[d]).append(", ");
                    }
                    btnRepeatDays.setText(sb.substring(0, sb.length() - 2));
                }
            }
        }

    }

    private void scheduleNotification(Task task) {
        long notifyTime = task.getDueTimeMillis();
        long fiveMinutesBefore = notifyTime - 5 * 60 * 1000; // 5 phút trước

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskId", task.getId());

        PendingIntent pendingIntentBefore = PendingIntent.getBroadcast(
                this,
                task.getId() * 10,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent pendingIntentOnTime = PendingIntent.getBroadcast(
                this,
                task.getId() * 10 + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            // Đặt báo thức 5 phút trước
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, fiveMinutesBefore, pendingIntentBefore);
            // Đặt báo thức đúng giờ
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntentOnTime);
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    year = y;
                    month = m;
                    day = d;
                    updateDueTime();
                    btnPickDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                }, cYear, cMonth, cDay);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int cHour = c.get(Calendar.HOUR_OF_DAY);
        int cMinute = c.get(Calendar.MINUTE);

        boolean is24Hour = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, h, m) -> {
            hour = h;
            minute = m;
            updateDueTime();
            String timeStr;
            if (is24Hour) {
                timeStr = String.format("%02d:%02d", hour, minute);
            } else {
                int hour12 = hour % 12;
                if (hour12 == 0) hour12 = 12;
                String ampm = (hour < 12) ? "AM" : "PM";
                timeStr = String.format("%02d:%02d %s", hour12, minute, ampm);
            }
            btnPickTime.setText(timeStr);
        }, cHour, cMinute, is24Hour);
        timePickerDialog.show();
    }

    private void updateDueTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, 0);
        dueTimeMillis = cal.getTimeInMillis();
    }

    private void showRepeatDaysDialog() {
        final String[] days = {"Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
        final boolean[] checkedItems = new boolean[7];
        for (int i = 0; i < 7; i++) {
            checkedItems[i] = repeatDaysSelected.contains(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ngày lặp lại")
                .setMultiChoiceItems(days, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!repeatDaysSelected.contains(which)) {
                            repeatDaysSelected.add(which);
                        }
                    } else {
                        repeatDaysSelected.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (repeatDaysSelected.isEmpty()) {
                        btnRepeatDays.setText("Không lặp lại");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int day : repeatDaysSelected) {
                            sb.append(days[day]).append(", ");
                        }
                        btnRepeatDays.setText(sb.substring(0, sb.length() - 2));
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề công việc", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder repeatDaysStr = new StringBuilder();
        for (int day : repeatDaysSelected) {
            repeatDaysStr.append(day).append(",");
        }
        String repeatDays = repeatDaysStr.length() > 0 ? repeatDaysStr.substring(0, repeatDaysStr.length() - 1) : "";

        Task task;
        if (editingTask != null) {
            // Cập nhật task hiện tại
            editingTask.setTitle(title);
            editingTask.setDescription(description);
            editingTask.setDueTimeMillis(dueTimeMillis);
            editingTask.setRepeatDays(repeatDays);
            editingTask.setRingtoneUri(selectedRingtoneUri != null ? selectedRingtoneUri.toString() : null);

            dbHelper.updateTask(editingTask);
            task = editingTask;
            Toast.makeText(this, "Cập nhật công việc thành công", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm task mới
            task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueTimeMillis(dueTimeMillis);
            task.setCompleted(false);
            task.setRepeatDays(repeatDays);
            task.setRingtoneUri(selectedRingtoneUri != null ? selectedRingtoneUri.toString() : null);

            long id = dbHelper.insertTask(task);
            task.setId((int) id);
            Toast.makeText(this, "Lưu công việc thành công", Toast.LENGTH_SHORT).show();
        }

        // Đặt thông báo cho task vừa thêm/cập nhật
        scheduleNotification(task);

        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RINGTONE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                selectedRingtoneUri = uri;
                String ringtoneTitle = RingtoneManager.getRingtone(this, uri).getTitle(this);
                txtRingtone.setText(ringtoneTitle);
            } else {
                txtRingtone.setText("Không có nhạc chuông");
                selectedRingtoneUri = null;
            }
        }
    }

    private void updateRingtoneTitle() {
        if (selectedRingtoneUri == null) {
            txtRingtone.setText("Không có nhạc chuông");
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, selectedRingtoneUri);
            if (ringtone != null) {
                txtRingtone.setText(ringtone.getTitle(this));
            } else {
                txtRingtone.setText("Nhạc chuông mặc định");
            }
        }
    }


}
