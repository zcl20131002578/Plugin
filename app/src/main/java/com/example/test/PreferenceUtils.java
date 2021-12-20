package com.example.test;


import android.content.Context;

/**
 * Created by suntongxuan on 18-9-18.
 */

public class PreferenceUtils extends BaseSharePreference {

    private static PreferenceUtils ins;

    private PreferenceUtils(Context context) {
        super("PREF_NAME", context);
    }


    public synchronized static PreferenceUtils getInstance(Context context) {
        if (ins == null) {
            ins = new PreferenceUtils(context);
        }
        return ins;
    }
}
