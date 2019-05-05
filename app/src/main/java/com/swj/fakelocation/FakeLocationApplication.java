package com.swj.fakelocation;

import android.app.Application;
import android.content.Context;

import android.os.Handler;
import android.os.Message;

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
                //mainActivity.
            }
        }
    }



    public static Context getContext()
    {
        return getContext();
    }
}
