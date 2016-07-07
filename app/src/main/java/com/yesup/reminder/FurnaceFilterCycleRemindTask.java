package com.yesup.reminder;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by derek on 6/28/16.
 */
public class FurnaceFilterCycleRemindTask extends RemindTask {
    public static final String FFC_PERIOD_NAME = "FfcPeriodName";
    public static final String FFC_BEGIN_NAME = "FfcBeginName";

    @Override
    public void initTask(Context context) {
        setTitle("FURNACE FILTER CYCLE");
        setPeriod(RemindTask.PERIOD_ONE_MONTHLY);
        hasSet = true;

        cycle = "Weekly";

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        setPeriod( sp.getInt(FFC_PERIOD_NAME, PERIOD_CLOSED) );
        setBegin( sp.getInt(FFC_BEGIN_NAME, 0) );

        switch (period) {
            case PERIOD_ONE_MONTHLY:
                hasSet = true;
                break;
            case PERIOD_TWO_MONTHLY:
                hasSet = true;
                break;
            case PERIOD_THREE_MONTHLY:
                hasSet = true;
                break;
            default:
                // closed
                hasSet = false;
                break;
        }
    }

    @Override
    public void setBegin(int begin) {
        if (begin > 28) {
            this.begin = 28;
        } else {
            this.begin = begin;
        }
        setDisplayData();
    }

    @Override
    public void setPeriod(int period) {
        super.setPeriod(period);
        switch (period) {
            case PERIOD_ONE_MONTHLY:
                hasSet = true;
                break;
            case PERIOD_TWO_MONTHLY:
                hasSet = true;
                break;
            case PERIOD_THREE_MONTHLY:
                hasSet = true;
                break;
            default:
                // closed
                hasSet = false;
                break;
        }
    }

    @Override
    protected void setDisplayData() {
        if (period > PERIOD_CLOSED && begin > 0) {
            Calendar calToday = Calendar.getInstance();
            Calendar calNext = Calendar.getInstance();
            // days
            int dayOfMonth = calToday.get(Calendar.DAY_OF_MONTH);
            int diffDays = begin - dayOfMonth;
            calNext.add(Calendar.MONTH, period);
            calNext.add(Calendar.DAY_OF_YEAR, diffDays);
            int d = calNext.get(Calendar.DAY_OF_YEAR) - calToday.get(Calendar.DAY_OF_YEAR);
            days = String.valueOf(d);
            // nextDate
            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            nextDate = format.format(calNext.getTime());
            // cycle
            switch (period) {
                case PERIOD_ONE_MONTHLY:
                    cycle = "One month";
                    break;
                case PERIOD_TWO_MONTHLY:
                    cycle = "Two month";
                    break;
                case PERIOD_THREE_MONTHLY:
                    cycle = "Three month";
                    break;
                default:
                    cycle = "";
                    break;
            }
        }
    }

    @Override
    public void saveTask(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(FFC_PERIOD_NAME, period);
        editor.putInt(FFC_BEGIN_NAME, begin);
        editor.commit();

        if (PERIOD_CLOSED == period) {
            // close alarm
            closeAlarm(context, REMIND_TYPE_FURNACE);
        } else {
            // set alarm
            int intervalDays = 30;
            switch (period) {
                case PERIOD_ONE_MONTHLY:
                    intervalDays = 30;
                    break;
                case PERIOD_TWO_MONTHLY:
                    intervalDays = 60;
                    break;
                case PERIOD_THREE_MONTHLY:
                    intervalDays = 90;
                    break;
                default:
                    intervalDays = 30;
                    break;
            }
            Calendar calToday = Calendar.getInstance();
            Calendar calNext = Calendar.getInstance();
            // days
            int dayOfMonth = calToday.get(Calendar.DAY_OF_MONTH);
            int diffDays = begin - dayOfMonth;
            calNext.add(Calendar.MONTH, period);
            calNext.add(Calendar.DAY_OF_YEAR, diffDays);
            calNext.set(Calendar.AM_PM, Calendar.AM);
            calNext.set(Calendar.HOUR, 10);
            calNext.set(Calendar.MINUTE, 0);
            calNext.set(Calendar.SECOND, 0);
            calNext.set(Calendar.MILLISECOND, 0);
            //firstTime.add(Calendar.SECOND, 10);
            setAlarm(context, REMIND_TYPE_FURNACE, calNext, intervalDays);
        }
    }
}
