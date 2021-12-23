package com.example.test;

import android.app.Activity;
import android.os.Bundle;

public class SecondActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

//    private AssetManager mAssetManager;
//    private Resources mResources;
//    private Resources.Theme mTheme;
//
//    protected void loadResources(String mDexPath) {
//        try {
//
//            AssetManager assetManager = AssetManager.class.newInstance();
//            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
//            addAssetPath.invoke(assetManager, mDexPath);
//            mAssetManager = assetManager;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Resources superRes = super.getResources();
//        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
//                superRes.getConfiguration());
//        mTheme = mResources.newTheme();
//        mTheme.setTo(super.getTheme());
//    }
//
//    private DlMainActivity() {
//        //no instance
//    }
//
//
//    public Resources.Theme getTheme() {
//        return mTheme;
//    }
//
//    @Override
//    public AssetManager getAssets() {
//        return mAssetManager == null ? super.getAssets() : mAssetManager;
//    }
//
//    @Override
//    public Resources getResources() {
//        return mResources == null ? super.getResources() : mResources;
//    }
}
