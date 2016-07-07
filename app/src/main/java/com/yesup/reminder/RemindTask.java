package com.yesup.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by derek on 6/28/16.
 */
public abstract class RemindTask {
    public static final String REMIND_TYPE_GARBAGE = "REMIND_TYPE_GARBAGE";
    public static final String REMIND_TYPE_FURNACE = "REMIND_TYPE_FURNACE";

    public static final String PREFS_NAME = "RemindPrefsFile";
    public static final int PERIOD_CLOSED = 0;
    public static final int PERIOD_ONE_MONTHLY = 1;
    public static final int PERIOD_TWO_MONTHLY = 2;
    public static final int PERIOD_THREE_MONTHLY = 3;
    public static final int PERIOD_WEEKLY = 1;

    protected boolean hasSet;
    protected String title;
    protected int period;
    protected int begin;

    protected String days;
    protected String nextDate;
    protected String cycle;

    public boolean hasSet() {
        return hasSet;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
        setDisplayData();
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
        setDisplayData();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCycle() {
        return cycle;
    }

    public String getDays() {
        return days;
    }

    public String getNextDate() {
        return nextDate;
    }

    protected abstract void setDisplayData();
    public abstract void initTask(Context context);
    public abstract void saveTask(Context context);

    public static void setAlarm(Context context, String remindType, Calendar firstTime, int intervalDays) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setType(remindType);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY*intervalDays, sender);
        //manager.set(AlarmManager.RTC_WAKEUP, firstTime.getTimeInMillis(), sender);

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm:ss");
        String nextDate = "Next remind is " + format.format(firstTime.getTime());
        Toast.makeText(context, nextDate, Toast.LENGTH_LONG).show();
    }

    public static void closeAlarm(Context context, String remindType) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setType(remindType);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(sender);

        Toast.makeText(context, "Remind alarm has been closed!", Toast.LENGTH_LONG).show();
    }
}
