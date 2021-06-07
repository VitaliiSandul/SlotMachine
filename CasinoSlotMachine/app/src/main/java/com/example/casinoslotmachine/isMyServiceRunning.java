package com.example.casinoslotmachine;

import android.app.ActivityManager;
import android.content.Context;

public class isMyServiceRunning {

    static Context context;

    isMyServiceRunning(Context context) {
        this.context = context;
    }

    public static boolean isRunning (Class myClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (myClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
