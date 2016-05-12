package cm.android.app.core;

import android.content.Context;

import cm.android.app.test.TestContext;
import cm.android.app.test.server.TestManager;
import cm.android.framework.core.AppContext;
import cm.android.framework.core.global.GlobalData;
import cm.android.framework.ext.alarm.TimerManager;

public class MyManager {

    public static GlobalData getGlobalData() {
        return GlobalData.getInstance();
    }

    public static <T> T getData(String tag) {
        return GlobalData.getInstance().getData(tag);
    }

    public static <T> void putData(String tag, T value) {
        GlobalData.getInstance().putData(tag, value);
    }

    public static Context getAppContext() {
        return MainApp.getApp().getApplicationContext();
    }

    public static TestManager getTestManager() {
//        TestManager test = BinderFactory.getProxy(TestContext.TEST, TestManager.class);
        TestManager test = AppContext.getService(TestContext.TEST, TestManager.class);
        return test;
    }

    public static TimerManager getTimerManager() {
//        TimerManager test = BinderFactory.getProxy(TestContext.TIMER_TASK_SERVER,TimerManager.class);
        TimerManager test = AppContext.getService(TestContext.TIMER_TASK_SERVER, TimerManager.class);
        return test;
    }
}
