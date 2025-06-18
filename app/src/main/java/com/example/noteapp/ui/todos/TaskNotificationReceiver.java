package com.example.noteapp.ui.todos;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.noteapp.R;

public class TaskNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("taskTitle");
        String description = intent.getStringExtra("taskDescription");
        int taskId = intent.getIntExtra("taskId", 0);
        String ringtoneUriStr = intent.getStringExtra("ringtoneUri");

        createNotificationChannel(context);

        Uri ringtoneUri;
        if (ringtoneUriStr != null) {
            ringtoneUri = Uri.parse(ringtoneUriStr);
        } else {
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // thay icon phù hợp trong drawable
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(ringtoneUri)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Có thể yêu cầu cấp quyền nếu thiếu
            return;
        }

        notificationManager.notify(taskId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Notifications";
            String description = "Thông báo nhắc việc cần làm";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
