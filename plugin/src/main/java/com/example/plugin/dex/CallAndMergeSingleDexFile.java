package com.example.plugin.dex;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class CallAndMergeSingleDexFile {


    private static final String TAG = "CallAndMerge";

    private void loadFromDisk() {
//      InMemoryDexClassLoader pathClassLoader = new InMemoryDexClassLoader("/sdcard/zcl/output.dex", null);
//      DexClassLoader pathClassLoader = new DexClassLoader("/data/data/com.example.test/output.dex", null, null, null);
        PathClassLoader pathClassLoader = new PathClassLoader("/data/data/com.example.test/output.dex", null);
        try {
            Class<?> testClass = pathClassLoader.loadClass("com.example.plugin.Test");
            Method method = testClass.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
        }
    }

    private void load(Context currentContext) {
        //调用测试
        loadClass(currentContext,"/data/data/com.example.test/output.dex");
        try {
            Class<?> clazz = Class.forName("com.example.plugin.Test");
            Method method = clazz.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadClass(Context context,String dexPath){

        //合并dexElements

        try{
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

            //插件的类加载器
            ClassLoader dexClassLoader = new DexClassLoader(dexPath
                    ,context.getCacheDir().getAbsolutePath()
                    ,null
                    ,pathClassLoader);

            //DexPathList类的对象
            Object pluginPathList = pathListField.get(dexClassLoader);
            //宿主的dexElements
            Object[] pluginElements = (Object[]) dexElementsField.get(pluginPathList);

            //创建一个新数组
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType()
                    ,hostDexElements.length + pluginElements.length);
            System.arraycopy(hostDexElements,0,newDexElements,0,hostDexElements.length);
            System.arraycopy(pluginElements,0,newDexElements,hostDexElements.length,pluginElements.length);

            //赋值
            dexElementsField.set(hostPathList,newDexElements);
            /**
             * 获取宿主的dexElements
             * 获取插件dex的dexElements
             * 新建宿主类型的array并将宿主以及插件的dexElements合并到新的array中去
             * 用这个新的array的值替换宿主的classLoader的dexElements原来的值
             */

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
