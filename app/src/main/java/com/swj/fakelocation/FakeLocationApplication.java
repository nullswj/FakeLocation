package com.swj.fakelocation;

import android.annotation.SuppressLint;
import android.app.ActivityManager;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

    public static final String AUTHORITY = "com.swj.fakelocation.provider";

    public static final Uri uri = Uri.parse("content://"+AUTHORITY+"/Location");

    public static final Uri realuri = Uri.parse("content://"+AUTHORITY+"/RealLocation");

    private static final String APP_ID = "2882303761517998471";

    private static final String APP_KEY = "5821799894471";

    public static final String TAG = "FakelocationApplication";

    private static FakeLocationHandle handle = null;

    private static ServerHandler shandler = null;

    private static MainActivity mainActivity = null;

    private static Context context;

    private static double latitude;

    private static double longtitude;


    public static void setMainActivity(MainActivity activity)
    {
        mainActivity = activity;
    }
    public static void setLatitude(double lat)
    {
        latitude = lat;
    }

    public static void setLongtitude(double lon)
    {
        longtitude = lon;
    }
    public static double getLatitude()
    {
        return latitude;
    }
    public static double getLongtitude()
    {
        return longtitude;
    }


    public static Context getContext()
    {
        return context;
    }

    public static ServerHandler getShandler()
    {
        return shandler;
    }


    public static class ServerHandler extends Handler
    {
        private Context context;

        public ServerHandler(Context context)
        {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0)
            {
                ContentValues values = new ContentValues();
                GpsLocation ServerLoc = (GpsLocation) msg.obj;
                values.put("lat",ServerLoc.lat);
                values.put("lon",ServerLoc.lon);
                getContext().getContentResolver().insert(uri,values);
                Log.e(TAG, "mock_handle_success");
            }
        }
    }

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
                Toast.makeText(context,s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "进入onCreate: " );
        if(shouldInit())
        {
            Log.e(TAG, "onCreate: " );
            MiPushClient.registerPush(this,APP_ID,APP_KEY);
            MiPushClient.setAlias(FakeLocationApplication.this, "fake", null);
            MiPushClient.setUserAccount(FakeLocationApplication.this, "fake", null);
            MiPushClient.subscribe(FakeLocationApplication.this, "fake", null);
            MiPushClient.unsubscribe(FakeLocationApplication.this, "fake", null);
            Log.e(TAG, "注册成功" );
            //Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
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

        //Logger.disablePushFileLog(context);

        if(handle == null)
        {
            handle = new FakeLocationHandle(getApplicationContext());
        }
        if(shandler == null)
        {
            shandler = new ServerHandler(getApplicationContext());
        }
        this.context = getApplicationContext();

        Log.e(TAG, "初始化完成" );
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
        MiPushClient.setAlias(ctx.getApplicationContext(), "fake", null);
        MiPushClient.setUserAccount(ctx.getApplicationContext(), "fake", null);
        MiPushClient.subscribe(ctx.getApplicationContext(), "fake", null);
        MiPushClient.unsubscribe(ctx.getApplicationContext(), "fake", null);
    }

}
