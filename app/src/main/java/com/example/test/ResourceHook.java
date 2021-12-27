package com.example.test;

import android.annotation.SuppressLint;

import com.example.test.callActivity.Singleton;

import java.lang.reflect.Method;

public class ResourceHook {

    public static ResourceHook getInstance(){
        return resourceHookSingleton.get();
    }

    private final static Singleton<ResourceHook> resourceHookSingleton= new Singleton<ResourceHook>() {
        @Override
        protected ResourceHook create() {
            return new ResourceHook();
        }
    };

    /**
     *
     * ActivityThread
     *         final ResourcesManager resourcesManager = ResourcesManager.getInstance();
     *
     *         // Create the base resources for which all configuration contexts for this Activity
     *         // will be rebased upon.
     *         context.setResources(resourcesManager.createBaseTokenResources(activityToken,
     *                 packageInfo.getResDir(),
     *                 splitDirs,
     *                 packageInfo.getOverlayDirs(),
     *                 packageInfo.getApplicationInfo().sharedLibraryFiles,
     *                 displayId,
     *                 overrideConfiguration,
     *                 compatInfo,
     *                 classLoader,
     *                 packageInfo.getApplication() == null ? null
     *                         : packageInfo.getApplication().getResources().getLoaders()));
     * ContextThemeWrapper
     *     public Resources getResources() {
     *         return getResourcesInternal();
     *     }
     *
     *     private Resources getResourcesInternal() {
     *         if (mResources == null) {
     *             if (mOverrideConfiguration == null) {
     *                 mResources = super.getResources();
     *             } else {
     *                 final Context resContext = createConfigurationContext(mOverrideConfiguration);
     *                 mResources = resContext.getResources();
     *             }
     *         }
     *         return mResources;
     *     }
     * ContextWrapper
     *     @Override
     *     public Resources getResources() {
     *         return mBase.getResources();//reated in ActivityThread performLaunchActivity
     *     }
     */


    //target SDK 30
    /**
     *  if (key.mLibDirs != null) {
     *             for (final String libDir : key.mLibDirs) {
     *                 if (libDir.endsWith(".apk")) {
     *                 try {
     *                         builder.addApkAssets(loadApkAssets(libDir, true , false ));
     *                 } catch (IOException e) {
     *                         Log.w(TAG, "Asset path" + libDir +"oes not exist or contains no resources.");
     *                         // continue.
     *                     }
     *                 }
    *             }
     *         }
     *         AssetManager.Build  get mUserApkAssets filed add ApkAsset   //mUserApkAssets.add(apkAssets);
     */

    //
    public void resourceHook(String apkPath){

        try {
            Class<?> assetBuilder = Class.forName("android.content.res.AssetManager$Builder");
            Object assetManagerBuilderIns = assetBuilder.newInstance();

            Method addApkAssets = assetBuilder.getDeclaredMethod("addApkAssets");
            addApkAssets.setAccessible(true);

            @SuppressLint("SoonBlockedPrivateApi") Method buildMethod = assetBuilder.getDeclaredMethod("build");
            buildMethod.setAccessible(true);

            //get ResourceManager sResourcesManagerMethod instance
            Class<?> clazz = Class.forName("android.app.ResourcesManager");
//            Field sResourcesManagerMethod = clazz.getDeclaredField("sResourcesManagerMethod");
            Method sResourcesManagerMethod = clazz.getDeclaredMethod("getInstance");
            sResourcesManagerMethod.setAccessible(true);
            Object sResourcesManagerInstance = sResourcesManagerMethod.invoke(null);//instance of sResourcesManagerMethod

            Method loadApkAssets = clazz.getDeclaredMethod("loadApkAssets");
            loadApkAssets.setAccessible(true);
            Object ApkAssetsObject = loadApkAssets.invoke(sResourcesManagerInstance, new Object[]{apkPath, true, false});

            addApkAssets.invoke(assetManagerBuilderIns,new Object[]{ApkAssetsObject});

            buildMethod.invoke(assetManagerBuilderIns);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}