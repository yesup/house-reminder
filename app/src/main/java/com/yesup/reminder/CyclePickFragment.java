package com.yesup.reminder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by derek on 7/4/16.
 */
public class CyclePickFragment extends DialogFragment {
    private final String TAG = "CyclePickFragment";
    public static final String CYCLE_SELECTED_NAME = "CycleSelected";
    public static final String CYCLE_TYPE_NAME = "CycleType";
    public static final int CYCLE_TYPE_WEEKLY = 0;
    public static final int CYCLE_TYPE_MONTHLY = 1;

    private CycleListAdapter mCycleListAdapter;

    static CyclePickFragment newInstance(int cycleType, int cycleSelected) {
        CyclePickFragment fragment = new CyclePickFragment();

        Bundle args = new Bundle();
        args.putInt(CYCLE_TYPE_NAME, cycleType);
        args.putInt(CYCLE_SELECTED_NAME, cycleSelected);
        fragment.setArguments(args);

        return fragment;
    }

    public interface OnCycleSelectedListener {
        void onCycleSelected(int position);
    }
    private OnCycleSelectedListener mOnCycleSelectedListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCycleSelectedListener = (OnCycleSelectedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCycleSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int cycleType = getArguments().getInt(CYCLE_TYPE_NAME);
        int cycleSelected = getArguments().getInt(CYCLE_SELECTED_NAME);
        initCycleModel(cycleType, cycleSelected);
        return dlg;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_pick_fragment, container, false);
        ListView listView = (ListView)view.findViewById(R.id.cycle_list);
        mCycleListAdapter = new CycleListAdapter(getContext());
        listView.setAdapter(mCycleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onCycleClick(position);
            }
        });

        return view;
    }

    private class CycleModel {
        int selectedIndex;
        List<String> itemList = new ArrayList<>();
    }
    private CycleModel mCycleModel = new CycleModel();
    private void initCycleModel(int cycleType, int cycleSelected) {
        switch (cycleType) {
            case CYCLE_TYPE_WEEKLY:
                if (cycleSelected >= 0 && cycleSelected <= 5) {
                    mCycleModel.selectedIndex = cycleSelected;
                } else {
                    mCycleModel.selectedIndex = 0;
                }
                mCycleModel.itemList.add("No remind");
                mCycleModel.itemList.add("Monday");
                mCycleModel.itemList.add("Tuesday");
                mCycleModel.itemList.add("Wednesday");
                mCycleModel.itemList.add("Thursday");
                mCycleModel.itemList.add("Friday");
                break;
            case CYCLE_TYPE_MONTHLY:
            default:
                if (cycleSelected >= 0 && cycleSelected <= 3) {
                    mCycleModel.selectedIndex = cycleSelected;
                } else {
                    mCycleModel.selectedIndex = 0;
                }
                mCycleModel.itemList.add("No remind");
                mCycleModel.itemList.add("1 Month");
                mCycleModel.itemList.add("2 Month");
                mCycleModel.itemList.add("3 Month");
                break;
        }
    }

    private void onCycleClick(int position) {
        Log.i(TAG, "OnCycleListItemClick: " + position);
        mCycleModel.selectedIndex = position;
        mCycleListAdapter.notifyDataSetChanged();

        this.dismiss();
        mOnCycleSelectedListener.onCycleSelected(position);
    }

    public class CycleItemViewHolder {
        public TextView textView;
        public RadioButton radioButton;
    }
    private class CycleListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public CycleListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return mCycleModel.itemList.size();
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
            CycleItemViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cycle_list_item, null);
                holder = new CycleItemViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.cycle_text_view);
                holder.radioButton = (RadioButton)convertView.findViewById(R.id.cycle_radio_button);
                holder.radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCycleClick(position);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (CycleItemViewHolder)convertView.getTag();
            }
            holder.textView.setText(mCycleModel.itemList.get(position));
            holder.radioButton.setChecked(position == mCycleModel.selectedIndex);
            return convertView;
        }
    }
}
