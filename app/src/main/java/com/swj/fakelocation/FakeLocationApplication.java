package com.swj.fakelocation;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import android.os.Process;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class FakeLocationApplication extends Application {

    private static final String APP_ID = "2882303761517998471";

    private static final String APP_KEY = "5821799894471";

    public static final String TAG = "com.swj.fakelocation";

    private static FakeLocationHandle handle = null;

    private static MainActivity mainActivity = null;

    private Context context;

    public static class FakeLocationHandle extends Handler
    {
        private Context context;

        public FakeLocationHandle(Context context)
        {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = (String)msg.obj;

            if(mainActivity != null)
            {
                mainActivity.refreshLogInfo();
            }
            if(!TextUtils.isEmpty(s))
            {
                Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(shouldInit())
        {
            MiPushClient.registerPush(this,APP_ID,APP_KEY);
        }
        LoggerInterface newLogger = new LoggerInterface()
        {
            @Override
            public void setTag(String s) {

            }

            @Override
            public void log(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void log(String s, Throwable t) {
                Log.d(TAG, s, t);
            }
        };

        Logger.setLogger(this,newLogger);

        if(handle == null)
        {
            handle = new FakeLocationHandle(getApplicationContext());
        }
    }

    private boolean shouldInit()
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();

        for(ActivityManager.RunningAppProcessInfo info : processInfos)
        {
            if(info.pid == myPid && mainProcessName.equals(info.processName))
            {
                return true;
            }
        }
        return false;
    }

    public static void reInitPush(Context ctx) {
        MiPushClient.registerPush(ctx.getApplicationContext(), APP_ID, APP_KEY);
    }

    public static FakeLocationHandle getHandle()
    {
        return handle;
    }

    public static void setMainActivity(MainActivity activity)
    {
        mainActivity = activity;
    }



    public static Context getContext()
    {
        return getContext();
    }
}
