package com.example.test;

import android.content.Context;
import android.util.Log;

import com.example.test.callActivity.Singleton;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class DexHook {

    private DexHook() {

    }

    public static DexHook getInstance() {
        return dexInstance.get();
    }

    private final static Singleton<DexHook> dexInstance = new Singleton<DexHook>() {
        @Override
        protected DexHook create() {
            return new DexHook();
        }
    };

    public void load(Context context) {
        String apkPath = LoadResourcesUtils.apkPath;
        //调用测试
        loadClass(context, apkPath);
        try {
//            Class<?> clazz = Class.forName("com.miui.branch.BranchLogger");
            Class<?> clazz = Class.forName("com.mi.android.globallauncher.CommercialLogger");
            Method method = clazz.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromDisk() {
//      DexClassLoader pathClassLoader = new DexClassLoader("/data/data/com.example.test/output.dex", null, null, null);
        //only through the special class loader load the class
        PathClassLoader pathClassLoader = new PathClassLoader("/data/data/com.example.test/output.dex", null);
        try {
            Class<?> testClass = pathClassLoader.loadClass("com.example.test.Test");
            Method method = testClass.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            Log.e("loadFromDisk", "onClick: " + e);
        }
    }


    public static void loadClass(Context context, String apkOrDexPath) {

        //合并dexElements

        try {
            //获取BaseDexClassLoader中的pathList（DexPathList）
            Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = clazz.getDeclaredField("pathList"); //private final DexPathList pathList;
            pathListField.setAccessible(true);

            //获取DexPathList中的dexElements数组
            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            //宿主的类加载器
            ClassLoader pathClassLoader = context.getClassLoader();
            //DexPathList类的对象
            Object hostPathList = pathListField.get(pathClassLoader);//在指定的对象上返回由此Field表示的字段的值。 如果该值具有原始类型，则该值会自动包装在一个对象中。
            //宿主的dexElements
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

//            String apkPath = "/data/data/com.example.test/output.dex";
            //插件的类加载器
            ClassLoader dexClassLoader = new DexClassLoader(apkOrDexPath
                    , context.getCacheDir().getAbsolutePath()
                    , null
                    , pathClassLoader);

            //DexPathList类的对象
            Object pluginPathList = pathListField.get(dexClassLoader);
            //宿主的dexElements
            Object[] pluginElements = (Object[]) dexElementsField.get(pluginPathList);

            //创建一个新数组
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType()
                    , hostDexElements.length + pluginElements.length);
            System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexElements.length);
            System.arraycopy(pluginElements, 0, newDexElements, hostDexElements.length, pluginElements.length);

            //赋值
            dexElementsField.set(hostPathList, newDexElements);
            /**
             * 获取宿主的dexElements
             * 获取插件dex的dexElements
             * 新建宿主类型的array并将宿主以及插件的dexElements合并到新的array中去
             * 用这个新的array的值替换宿主的classLoader的dexElements原来的值
             */

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
