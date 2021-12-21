package com.example.plugin.callActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.example.plugin.PluginActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


public class ActivityHook {

    public static ActivityHook getInstance() {
        return activityHook.get();
    }

    private final static Singleton<ActivityHook> activityHook= new Singleton<ActivityHook>() {
        @Override
        protected ActivityHook create() {
            return new ActivityHook();
        }
    };

    private static final String TARGET_INTENT = "TARGET_INTENT";


    public void firstHook() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        //获取Singleton<T>对象
        Field singletonField = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //8.0
            Class<?> clazz = Class.forName("android.app.ActivityManagerNative");
            singletonField = clazz.getDeclaredField("gDefault");
        } else {
            Class<?> clazz = Class.forName("android.app.ActivityManager");
            singletonField = clazz.getDeclaredField("IActivityManagerSingleton");
        }
        singletonField.setAccessible(true);
        /**
         * private static final Singleton<IActivityManager> IActivityManagerSingleton =
         *             new Singleton<IActivityManager>() {
         *                 @Override
         *                 protected IActivityManager create() {
         *                     final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
         *                     final IActivityManager am = IActivityManager.Stub.asInterface(b);
         *                     return am;
         *                 }
         *             };
         *
         *  singleton 对象类型为Singleton<IActivityManager>
         */
        Object singleton = singletonField.get(null); //静态的可以直接获取，传入null

        //获取mInstance对象,mInstance是非静态的,mInstance对象是系统的IActivityManager对象,也就是ActivityManager.getService()
        /**
         * public abstract class Singleton<T> {
         *     private T mInstance;
         *
         *     protected abstract T create();
         *
         *     public final T get() {
         *         synchronized (this) {
         *             if (mInstance == null) {
         *                 mInstance = create();
         *             }
         *             return mInstance;
         *         }
         *     }
         * }
         */
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        /**
         * singleton 对象类型为Singleton<IActivityManager>,从singleton里面获取mInstance属性对象，即IActivityManager对象
         */
        final Object mInstance = mInstanceField.get(singleton);

        //创建动态代理对象
        Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");

        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{iActivityManagerClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                        // do something
                        // Intent的修改 -- 过滤
                        /**
                         * IActivityManager类的方法
                         * startActivity(whoThread, who.getBasePackageName(), intent,
                         *                         intent.resolveTypeIfNeeded(who.getContentResolver()),
                         *                         token, target != null ? target.mEmbeddedID : null,
                         *                         requestCode, 0, null, options)
                         */
                        //过滤
                        if ("startActivity".equals(method.getName())) {
                            int index = -1;
                            //获取Intent参数在args数组中的index值
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] instanceof Intent) {
                                    index = i;
                                    break;
                                }
                            }
                            //得到原始的Intent对象
                            Intent intent = (Intent) args[index];

                            //生成代理proxyIntent
                            Intent proxyIntent = new Intent();
                            proxyIntent.setClassName("com.example.plugin", PluginActivity.class.getName());
                            //保存原始的Intent对象
                            proxyIntent.putExtra(TARGET_INTENT, intent);
                            //使用proxyIntent替换数组中的Intent
                            args[index] = proxyIntent;
                        }

                        //args method需要的参数  --不改变原有的执行流程
                        //mInstance 系统的IActivityManager对象
                        return method.invoke(mInstance, args);
                    }
                });

        //替换
        mInstanceField.set(singleton, proxyInstance);


    }

    public void secondHook() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        // 创建的 callback
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                // 通过msg  可以拿到 Intent，可以换回执行插件的Intent

                // 找到 Intent的方便替换的地方  --- 在这个类里面 ActivityClientRecord --- Intent intent 非静态
                // msg.obj == ActivityClientRecord
                switch (msg.what) {
                    case 100:
                        try {
                            Field intentField = msg.obj.getClass().getDeclaredField("intent");
                            intentField.setAccessible(true);
                            // 启动代理Intent
                            Intent proxyIntent = (Intent) intentField.get(msg.obj);
                            // 启动插件的 Intent
                            Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                            if (intent != null) {
                                intentField.set(msg.obj, intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 159:
                        try {
                            // 获取 mActivityCallbacks 对象
                            Field mActivityCallbacksField = msg.obj.getClass()
                                    .getDeclaredField("mActivityCallbacks");

                            mActivityCallbacksField.setAccessible(true);
                            List mActivityCallbacks = (List) mActivityCallbacksField.get(msg.obj);

                            for (int i = 0; i < mActivityCallbacks.size(); i++) {
                                if (mActivityCallbacks.get(i).getClass().getName()
                                        .equals("android.app.servertransaction.LaunchActivityItem")) {
                                    Object launchActivityItem = mActivityCallbacks.get(i);

                                    // 获取启动代理的 Intent
                                    Field mIntentField = launchActivityItem.getClass()
                                            .getDeclaredField("mIntent");
                                    mIntentField.setAccessible(true);
                                    Intent proxyIntent = (Intent) mIntentField.get(launchActivityItem);

                                    // 目标 intent 替换 proxyIntent
                                    Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                    if (intent != null) {
                                        mIntentField.set(launchActivityItem, intent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                /**
                 * public void dispatchMessage(Message msg) {
                 *     if (msg.callback != null) {
                 *         handleCallback(msg);
                 *     } else {
                 *         if (mCallback != null) {
                 *             if (mCallback.handleMessage(msg)) { //传false才能实现修改msg但是不处理msg的效果
                 *                 return;
                 *             }
                 *         }
                 *         handleMessage(msg);
                 *     }
                 * }
                 */
                // 必须 return false
                return false;
            }
        };

        /**
         * 通过反射给系统的H(Handler)设置一个Callback
         */

        // 获取 ActivityThread 类的 Class 对象
        Class<?> clazz = Class.forName("android.app.ActivityThread");

        // 获取 ActivityThread 对象
        Field activityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
        activityThreadField.setAccessible(true);
        Object activityThread = activityThreadField.get(null);

        // 获取 mH 对象
        Field mHField = clazz.getDeclaredField("mH");
        mHField.setAccessible(true);
        final Handler mH = (Handler) mHField.get(activityThread);

        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        // 创建的 callback


        // 替换系统的 callBack
        mCallbackField.set(mH, callback);

    }


}
