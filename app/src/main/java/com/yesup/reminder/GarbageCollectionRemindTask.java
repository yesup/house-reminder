package com.yesup.reminder;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by derek on 6/28/16.
 */
public class GarbageCollectionRemindTask extends RemindTask {
    public static final String GC_PERIOD_NAME = "GcPeriodName";
    public static final String GC_BEGIN_NAME = "GcBeginName";

    @Override
    public void initTask(Context context) {
        setTitle("GARBAGE COLLECTION DAY");
        cycle = "Weekly";

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        setPeriod( sp.getInt(GC_PERIOD_NAME, PERIOD_CLOSED) );
        setBegin( sp.getInt(GC_BEGIN_NAME, 0) );

        if (PERIOD_CLOSED == period) {
            hasSet = false;
        } else {
            hasSet = true;
        }
    }

    @Override
    public void setPeriod(int period) {
        super.setPeriod(period);
        if (PERIOD_CLOSED == period) {
            hasSet = false;
        } else {
            hasSet = true;
        }
    }

    @Override
    protected void setDisplayData() {
        if (period > PERIOD_CLOSED && begin > 0) {
            Calendar calendar = Calendar.getInstance();
            // days
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int diffDays;
            if (dayOfWeek >= begin) {
                diffDays = 7 + begin - dayOfWeek;
                days = String.valueOf(diffDays);
            } else {
                diffDays = begin - dayOfWeek;
                days = String.valueOf(diffDays);
            }
            // nextDate
            calendar.add(Calendar.DAY_OF_YEAR, diffDays);
            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            nextDate = format.format(calendar.getTime());
            // cycle
            cycle = "Weekly";
        }
    }

    @Override
    public void saveTask(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(GC_PERIOD_NAME, period);
        editor.putInt(GC_BEGIN_NAME, begin);
        editor.commit();

        if (PERIOD_CLOSED == period) {
            // close alarm
            closeAlarm(context, REMIND_TYPE_GARBAGE);
        } else {
            // set alarm
            int intervalDays = 7;
            Calendar firstTime = Calendar.getInstance();
            firstTime.set(Calendar.AM_PM, Calendar.AM);
            firstTime.set(Calendar.HOUR, 8);
            firstTime.set(Calendar.MINUTE, 0);
            firstTime.set(Calendar.SECOND, 0);
            firstTime.set(Calendar.MILLISECOND, 0);
            // days
            int dayOfWeek = firstTime.get(Calendar.DAY_OF_WEEK) - 1;
            int diffDays;
            if (dayOfWeek >= begin) {
                diffDays = 7 + begin - dayOfWeek;
                days = String.valueOf(diffDays);
            } else {
                diffDays = begin - dayOfWeek;
                days = String.valueOf(diffDays);
            }
            // nextDate
            firstTime.add(Calendar.DAY_OF_YEAR, diffDays);
            //firstTime.add(Calendar.SECOND, 10);
            setAlarm(context, REMIND_TYPE_GARBAGE, firstTime, intervalDays);
        }
    }
}
