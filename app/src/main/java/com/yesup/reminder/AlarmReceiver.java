package com.yesup.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by derek on 7/7/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = "Reminder";
        String remindType = intent.getType();
        if (remindType != null) {
            if (remindType.equals(RemindTask.REMIND_TYPE_GARBAGE)) {
                title = "Garbage collection remind";
            } else if (remindType.equals(RemindTask.REMIND_TYPE_FURNACE)) {
                title = "Furnace filter cycle remind";
            }
        }

        Intent playIntent = new Intent(context, MainActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //playIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title)
                .setContentText("Touch to open reminder.")
                .setSmallIcon(R.drawable.calendar)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
