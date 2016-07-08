package com.yesup.reminder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yesup.ad.banner.BannerView;
import com.yesup.ad.interstitial.IInterstitialListener;
import com.yesup.ad.offerwall.OfferWallPartnerHelper;
import com.yesup.partner.YesupAd;
import com.yesup.partner.YesupInterstitial;
import com.yesup.partner.YesupOfferWall;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements CyclePickFragment.OnCycleSelectedListener,IInterstitialListener {
    private String TAG = "MainActivity";
    private BannerView bannerAdBottom;
    private YesupInterstitial interstitialAd;
    private InterstitialPartnerView partnerView;
    private YesupOfferWall offerwallAd;

    private TaskListAdapter mTaskListAdapter;
    private List taskList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689e39")));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.activity_title);
        Button btn = (Button)bar.getCustomView().findViewById(R.id.offerwall_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerwallAd.showDefaultOfferWall();
            }
        });

        initTaskList();

        ListView listTasks = (ListView)findViewById(R.id.list_tasks);
        mTaskListAdapter = new TaskListAdapter(this);
        listTasks.setAdapter(mTaskListAdapter);
        listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onTaskClick(position);
            }
        });

        // init custom information
        String subId = "Reminder";  // optional, you app user id
        String optValue1 = "";    // optional, additional event value you want to keep track
        String optValue2 = "";    // optional, additional event value you want to keep track
        String optValue3 = "";    // optional, additional event value you want to keep track
        YesupAd.setSubId(subId);
        YesupAd.setOption(optValue1, optValue2, optValue3);

        // banner ad
        bannerAdBottom = (BannerView)findViewById(R.id.yesupBannerAdBottom);
        // interstitial ad
        interstitialAd = new YesupInterstitial(this);
        partnerView = new InterstitialPartnerView(this);
        // offer wall
        offerwallAd = new YesupOfferWall(this);
        offerwallAd.setOfferWallPartnerHelper(new OfferWallHelper(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != bannerAdBottom) {
            bannerAdBottom.onResume();
        }
        if (null != offerwallAd) {
            offerwallAd.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != bannerAdBottom) {
            bannerAdBottom.onPause();
        }
    }

    private int mCurTaskType;
    private void onTaskClick(int position) {
        Log.i(TAG, "OnTaskClick: "+position);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("CycleFragmentDialog");
        if (null !=  prev) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        CyclePickFragment cyclePickFragment;
        RemindTask task;
        switch (position) {
            case 1:
                // furnace filter cycle
                mCurTaskType = 1;
                task = (RemindTask)taskList.get(1);
                cyclePickFragment = CyclePickFragment.newInstance(CyclePickFragment.CYCLE_TYPE_MONTHLY, task.getPeriod());
                break;
            default:
                // garbage collection day
                mCurTaskType = 0;
                task = (RemindTask)taskList.get(0);
                cyclePickFragment = CyclePickFragment.newInstance(CyclePickFragment.CYCLE_TYPE_WEEKLY, task.getBegin());
                break;
        }
        cyclePickFragment.show(ft, "CycleFragmentDialog");
    }

    private void initTaskList() {
        RemindTask task1 = new GarbageCollectionRemindTask();
        task1.initTask(getApplicationContext());
        taskList.add(task1);
        RemindTask task2 = new FurnaceFilterCycleRemindTask();
        task2.initTask(getApplicationContext());
        taskList.add(task2);
    }

    @Override
    public void onCycleSelected(int position) {
        RemindTask task;
        switch (mCurTaskType) {
            case 0:
                task = (RemindTask)taskList.get(0);
                if (task.getBegin() != position) {
                    if (position > 0) {
                        task.setPeriod(RemindTask.PERIOD_WEEKLY);
                        task.setBegin(position);
                    } else {
                        task.setPeriod(RemindTask.PERIOD_CLOSED);
                        task.setBegin(0);
                    }
                    task.saveTask(getApplicationContext());
                    mTaskListAdapter.notifyDataSetChanged();
                    // show interstitial ad
                    interstitialAd.showDefaultInterstitial(false, true, partnerView);
                }
                break;
            case 1:
                task = (RemindTask)taskList.get(1);
                if (task.getPeriod() != position) {
                    if (position > 0) {
                        task.setPeriod(position);
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        task.setBegin(day);
                    } else {
                        task.setPeriod(RemindTask.PERIOD_CLOSED);
                        task.setBegin(0);
                    }
                    task.saveTask(getApplicationContext());
                    mTaskListAdapter.notifyDataSetChanged();
                    // show interstitial ad
                    interstitialAd.showInterstitial(104925, false, true, partnerView);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onInterstitialShown() {
        Log.i(TAG, "On Interstitial Shown");
        //interstitialAd.safeClose();
    }

    @Override
    public void onInterstitialCredited() {
        Log.i(TAG, "On Interstitial Credited");
    }

    @Override
    public void onInterstitialClosed() {
        Log.i(TAG, "On Interstitial Closed");
    }

    @Override
    public void onInterstitialError() {
        Log.i(TAG, "On Interstitial Error");
        interstitialAd.closeNow();
    }

    public class TaskViewHolder {
        public int viewIndex = -1;
        public TextView tvTitle;
        public TextView tvDays;
        public TextView tvDate;
        public TextView tvCycle;
    }
    private class TaskListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public TaskListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RemindTask task = (RemindTask)taskList.get(position);
            TaskViewHolder holder;
            if (convertView == null) {
                if (task.hasSet()) {
                    convertView = mInflater.inflate(R.layout.list_item_task_full, null);
                    setFullItem(convertView, task);
                } else {
                    convertView = mInflater.inflate(R.layout.list_item_task_init, null);
                    setInitItem(convertView, task);
                }
            } else {
                holder = (TaskViewHolder)convertView.getTag();
                if (1 == holder.viewIndex) {
                    if (!task.hasSet()) {
                        // init item
                        convertView = mInflater.inflate(R.layout.list_item_task_init, null);
                        setInitItem(convertView, task);
                    } else {
                        setFullItem(convertView, task);
                    }
                } else {
                    if (task.hasSet()) {
                        // full item
                        convertView = mInflater.inflate(R.layout.list_item_task_full, null);
                        setFullItem(convertView, task);
                    } else {
                        setInitItem(convertView, task);
                    }
                }
            }

            return convertView;
        }

        private void setInitItem(View convertView, RemindTask task) {
            TaskViewHolder holder = (TaskViewHolder)convertView.getTag();
            if (holder == null) {
                holder = new TaskViewHolder();
                holder.viewIndex = 0;
                holder.tvTitle = (TextView)convertView.findViewById(R.id.text_title);
                convertView.setTag(holder);
            }
            holder.tvTitle.setText(task.getTitle());
        }
        private void setFullItem(View convertView, RemindTask task) {
            TaskViewHolder holder = (TaskViewHolder)convertView.getTag();
            if (holder == null) {
                holder = new TaskViewHolder();
                holder.viewIndex = 1;
                holder.tvTitle = (TextView)convertView.findViewById(R.id.title_text);
                holder.tvDays = (TextView)convertView.findViewById(R.id.days_text);
                holder.tvDate = (TextView)convertView.findViewById(R.id.date_text);
                holder.tvCycle = (TextView)convertView.findViewById(R.id.cycle_text);
                convertView.setTag(holder);
            }
            holder.tvTitle.setText(task.getTitle());
            holder.tvDays.setText(task.getDays());
            holder.tvDate.setText(task.getNextDate());
            holder.tvCycle.setText(task.getCycle());
        }
    }

    public class OfferWallHelper extends OfferWallPartnerHelper {

        public OfferWallHelper(Context context) {
            super(context);
        }

        @Override
        public String calculateReward(int payout, int incentRate) {
            String result = "0";
            double reward = (double)payout * (double)incentRate / 100000.0D;
            if(0.0D == reward) {
                result = "";
            } else {
                result = (new DecimalFormat("#.##")).format(reward);
            }

            return result;
        }

        @Override
        public Drawable getRewardIcon() {
            Drawable drawable = context.getResources().getDrawable(R.drawable.coins);
            return drawable;
        }

    }

}
