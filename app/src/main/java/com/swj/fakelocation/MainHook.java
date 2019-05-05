package com.swj.fakelocation;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Looper;

import android.os.Handler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {

    private static String AUTHORITY;

    private static final Uri NOTIFY_URI;

    private static double lat;

    private static double lon;

    static
    {
        AUTHORITY = "com.swj.fakelocation.provider";
        StringBuilder localString = new StringBuilder();
        localString.append("content://");
        localString.append(AUTHORITY);
        localString.append("/Location");
        NOTIFY_URI = Uri.parse(localString.toString());
    }

//    private static void loadLocation(Context context)
//    {
//
//    }

//    public static void LocationUpdateListen(final Context context)
//    {
//        final ContentResolver resolver = context.getContentResolver();
//
////        Handler handler = new Handler(Looper.getMainLooper());
//        loadLocation(resolver);
////        resolver.registerContentObserver(NOTIFY_URI, true, new ContentObserver(handler) {
////            @Override
////            public void onChange(boolean selfChange) {
////                super.onChange(selfChange);
////                MainHook.loadLocation(resolver);
////            }
////        });
//    }
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedBridge.log("loaded: " + lpparam.packageName);

        XposedBridge.log("开始劫持");

//        if(!lpparam.packageName.equals("android") && !lpparam.packageName.startsWith("com.android."))
//        {
//            XposedHelpers.findAndHookMethod(Location.class, "onCreate", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    LocationUpdateListen();
//                    XposedBridge.log("onCreate change" + "lat: " + lat + " lon:" +lon);
//                }
//            });
//        }

        if (lpparam.packageName.equals("com.autonavi.minimap")) {

//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if(param.hasThrowable())
//                    return;
//
//                XposedHelpers.findAndHookMethod(param.getClass(), "getLatitude", new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        param.setResult(39);
//                    }
//                });
//                XposedHelpers.findAndHookMethod(param.getClass(), "getLongitude", new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        param.setResult(106);
//                    }
//                });
//            }
//        });


            XposedBridge.log("进入");

            XposedHelpers.findAndHookMethod(Application.class, "attach",Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    ContentResolver resolver = ((Context)param.args[0]).getContentResolver();
                    Cursor cursor = resolver.query(NOTIFY_URI,null,null,null,null);

                    if(cursor.moveToLast())
                    {
                        lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                        lon = cursor.getDouble(cursor.getColumnIndex("lon"));
                    }

                    XposedBridge.log("attach修改：" + "经度： "+ lon + "\n 纬度："+lat);
                }
            });

            XposedHelpers.findAndHookMethod(Location.class, "getLongitude", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                   // LocationUpdateListen();

                    XposedBridge.log("进入，开始修改经度坐标: ");

                    XposedBridge.log("经度坐标: "+lon);
//                    MainHook.loadLocation((Context)param.args[0]);


                    if(lon == 0.0)
                        return;
                    else
                        param.setResult(lon);

                }

            });

            XposedHelpers.findAndHookMethod(Location.class, "getLatitude", new XC_MethodHook() {

                @Override

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {


                    //LocationUpdateListen();

                    XposedBridge.log("进入，开始修改纬度坐标:" );

                    //MainHook.loadLocation((Context)param.args[0]);
                    XposedBridge.log("纬度坐标:"+lat );


                    if(lat == 0.0)
                        return;
                    else
                        param.setResult(lat);
                }

            });

        }
    }
}
