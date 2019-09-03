package com.ruby.workmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReferrerReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrerString = intent.getStringExtra("referrer");
        if(referrerString != null) {
            //解析 referrerString
        }
    }
}
