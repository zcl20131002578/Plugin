package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class ResourcePluginActivity extends BaseActivity {
    private static final String TAG = "PluginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ======启动插件的Activity");
        int mLayoutId = mContext.getResources().getIdentifier("activity_branch", "layout", "com.miui.branch");
        int mLayoutId2 = mContext.getResources().getIdentifier("com.miui.branch:layout/activity_branch", null, null);
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, null);
        setContentView(view);
    }
}
