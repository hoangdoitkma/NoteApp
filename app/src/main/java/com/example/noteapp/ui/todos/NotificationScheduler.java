package com.example.noteapp.ui.todos;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleNotification(Context context, int taskId, String title, String description,
                                            String ringtoneUri, long notifyTimeMillis) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("taskTitle", title);
        intent.putExtra("taskDescription", description);
        intent.putExtra("ringtoneUri", ringtoneUri);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId, // dùng taskId làm requestCode để phân biệt
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notifyTimeMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTimeMillis, pendingIntent);
            }
        }
    }

    public static void cancelNotification(Context context, int taskId) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
