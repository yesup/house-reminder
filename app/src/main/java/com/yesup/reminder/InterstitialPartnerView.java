package com.yesup.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yesup.ad.interstitial.PartnerBaseView;

/**
 * Created by derek on 7/5/16.
 */
public class InterstitialPartnerView extends PartnerBaseView {
    protected LayoutInflater mInflater;

    public InterstitialPartnerView(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.interstitial_partner, null);
        }
        return view;
    }
}
