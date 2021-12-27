package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.test.callActivity.PluginNotRegister;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityHook.getInstance().hook();
        findViewById(R.id.main_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String appName = getResources().getString(R.string.app_name);
                try {
//                    InputStream is = getAssets().open("ic_launcher.png");
                    Intent intent = new Intent();
                    intent.setClassName(getApplicationContext(), ResourcePluginActivity.class.getName());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                String apkPath = "/data/data/com.example.test/app-debug.apk";
//                ResourceHook.getInstance().resourceHook(apkPath);

            }
        });


        findViewById(R.id.main_test1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

//                    Intent intent = new Intent();
                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), SecondActivity.class);
//                    intent.setClassName("com.example.test", SecondActivity.class.getName());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ZCLZCL", "onClick: exception: " + e);
                    e.printStackTrace();
                    //Unable to find explicit activity class {com.zcl.currentapp/com.example.dl.DlMainActivity}; have you declared this activity in your AndroidManifest.xml?
                }
            }
        });


        findViewById(R.id.main_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    Intent intent = new Intent();
                    Intent intent = new Intent(MainActivity.this, PluginNotRegister.class);
//                    intent.setClassName("com.example.test.callActivity", PluginNotRegister.class.getName());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ZCLZCL", "onClick: exception: " + e);
                    e.printStackTrace();
                    //Unable to find explicit activity class {com.zcl.currentapp/com.example.dl.DlMainActivity}; have you declared this activity in your AndroidManifest.xml?
                }
            }
        });
    }

    private void activityHook() {
        try {

            Intent intent = new Intent(MainActivity.this.getApplicationContext(), DlMainActivity.class);
//                    intent.setClassName("com.example.test", DlMainActivity.class.getName());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ZCLZCL", "onClick: exception: " + e);
            e.printStackTrace();
            //Unable to find explicit activity class {com.zcl.currentapp/com.example.dl.DlMainActivity}; have you declared this activity in your AndroidManifest.xml?
        }
    }

}