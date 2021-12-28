package com.example.test;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

public class LoadResourcesUtils {

//    private final static String apkPath = "/sdcard/app-debug.apk";
    public final static String apkPath = "/data/data/com.example.test/app-debug.apk";

    private static Resources mResources;

    public static Resources getResources(Context context){
        if (mResources == null){
            mResources = loadResource(context);
        }
        return mResources;
    }

    public static Resources loadResource(Context context) {

        try {
            Class<?> assetManagerClass = AssetManager.class;
            AssetManager assetManager = (AssetManager) assetManagerClass.newInstance();
            Method addAssetPathMethod = assetManagerClass.getMethod("addAssetPath", String.class);

            addAssetPathMethod.invoke(assetManager, apkPath);
            //如果传入的是Activity的context死循环，导致崩溃
            Resources resources = context.getResources();
            //用来加载插件包中的资源
            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void example(){
//        mAsset = new AssetManager();
//        mAsset.addAssetPath("/system/framework/framework-res.apk");
//        mRes = new Resources(mAsset, null, null);
//
//        mTextId = mRes.getIdentifier("cancel", "string", "android");
//        mColorId = mRes.getIdentifier("transparent", "color", "android");
//        mIntegerId = mRes.getIdentifier("config_shortAnimTime", "integer", "android");
//        mLayoutId = mRes.getIdentifier("two_line_list_item", "layout", "android");
    }
}
