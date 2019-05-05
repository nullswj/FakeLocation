package com.swj.fakelocation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import static com.swj.fakelocation.FakeLocationApplication.TAG;
import static com.swj.fakelocation.getMainsocket.getDir;
import static com.swj.fakelocation.getMainsocket.getLocation;

public class MessageReceiver extends PushMessageReceiver {

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {

        Log.e(TAG, "onReceivePassThroughMessage: " );

        Log.e(TAG, getDir());
        GpsLocation location = getLocation();
        Log.e(TAG, ""+location.lat + " " + location.lon);
        //getMainsocket.connMainsocket(TAG,FakeLocationApplication.getShandler());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {

    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {

    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {

    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {

    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        Log.e(FakeLocationApplication.TAG,
                "onRequirePermissions is called. need permission" + arrayToString(permissions));

        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), PermissionActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}
