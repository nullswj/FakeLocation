package com.swj.fakelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public  class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "MainActivity";

    FloatingActionButton btn_start;

    FloatingActionButton btn_stop;

    public static double latitude;

    public static double longtitude;

    private double fake_lat;

    private double fake_lon;

    WebView map;

    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    private static final String AUTHORITY = "com.swj.fakelocation.provider";

    private static final Uri uri = Uri.parse("content://"+AUTHORITY+"/Location");

    public LocationClient locationClient = null;

    private MyLocationListener locationListener = new MyLocationListener();

    public static List<String> logList = new CopyOnWriteArrayList<>();

    private Button btn_connect;

    private ContentValues values = new ContentValues();

    private TextView text_log = null;

    @SuppressLint("HandlerLeak")
    public MainActivity() {
        ServerHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == 0)
                {
                    GpsLocation ServerLoc = (GpsLocation) msg.obj;
                    values.put("lat",ServerLoc.lat);
                    values.put("lon",ServerLoc.lon);
                    getContentResolver().insert(uri,values);
                    Log.e(TAG, "real_dire_handle_success");
                }
            }
        };
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                String Url = "https://apis.map.qq.com/tools/locpicker?type=0&backurl=https://callback&coordtype=5&coord="+latitude+","+longtitude+"&radius=2000&total=20&key=FAGBZ-66IWV-L4KPW-UODMT-3WUQZ-B6FMX&referer=fakelocation";
                map.loadUrl(Url);
            }

        };
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_start:
                values.put("lat",fake_lat);
                values.put("lon",fake_lon);
                getContentResolver().insert(uri,values);
                btn_start.setVisibility(View.INVISIBLE);
                btn_stop.setVisibility(View.VISIBLE);
//                Toast.makeText(MainActivity.this,"插入成功",Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this,"lat"+values.get("lat"),Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this,"lon"+values.get("lon"),Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this,"经度："+ fake_lon + " 纬度：" + fake_lat,Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_stop:
                values.put("lat",0.0);
                values.put("lon",0.0);
                getContentResolver().insert(uri,values);
                btn_stop.setVisibility(View.INVISIBLE);
                btn_start.setVisibility(View.VISIBLE);
                break;

            case R.id.connect:
                getMainsocket.connMainsocket(TAG,ServerHandler);
//                if(getMainsocket.connMainsocket(TAG,ServerHandler) == -1)
//                {
//
//                }
//                else if(getMainsocket.connMainsocket(TAG,ServerHandler) == 0)
//                {
//
//                }
//                else if(getMainsocket.connMainsocket(TAG,ServerHandler) == 1)
//                {
//
//                }
//                else if(getMainsocket.connMainsocket(TAG,ServerHandler) == 2)
//                {
//
//                }
//                else if(getMainsocket.connMainsocket(TAG,ServerHandler) == 3)
//                {
//
//                }
//                else if(getMainsocket.connMainsocket(TAG,ServerHandler) == 4)
//                {
//
//                }
                break;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            latitude = bdLocation.getLatitude();
            longtitude = bdLocation.getLongitude();

            //bd09TOgcj02();
        }
    }

    private void setLocation()
    {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setOpenGps(true);
        locationClient.setLocOption(option);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FakeLocationApplication.setMainActivity(this);

        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_connect = findViewById(R.id.connect);
        text_log = findViewById(R.id.text_log);

        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_connect.setOnClickListener(this);

        map = findViewById(R.id.web_map);

        WebSettings settings = map.getSettings();
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
        settings.setAllowFileAccess(false);
        map.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!url.startsWith("https://callback"))
                {
                    view.loadUrl(url);
                }
                else
                {
                    try
                    {
                        String decode = URLDecoder.decode(url,"UTF-8");
                        Uri uriReturn = Uri.parse(decode);
                        String location = uriReturn.getQueryParameter("latng");
                        String[] split = location.split(",");
                        String lat = split[0];
                        String lon = split[1];

                        fake_lat = Double.parseDouble(lat);
                        fake_lon = Double.parseDouble(lon);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
//


        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(locationListener);

        /*权限的申请判断*/
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)
                !=PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if (!permissionList.isEmpty())
        {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
        else
        {
            //开始执行
            setLocation();
            locationClient.start();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        }

    private void bd09TOgcj02()
    {
        double x = longtitude - 0.0065, y = latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        longtitude = z * Math.cos(theta);
        latitude = z * Math.sin(theta);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) //利用分支语句进行判断
        {
            case 1:
                if (grantResults.length > 0)
                {
                    for (int result : grantResults)  //在grantResult中提取数据
                    {
                        if (result != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //开始执行
                    setLocation();
                    locationClient.start();
                }
                else
                {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }

    }

    @SuppressLint("HandlerLeak")
    public Handler handler;

    @SuppressLint("HandlerLeak")
    public Handler ServerHandler;

    @Override
    protected void onResume() {
        super.onResume();
//        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
//        if(cursor != null && cursor.moveToLast())
//        {
//            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
//            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
//
//            edit_lat.setText(""+lat);
//            edit_lon.setText(""+lon);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        FakeLocationApplication.setMainActivity(null);
    }

    public void refreshLogInfo()
    {
        String AllLog = "";
        for(String log : logList)
        {
            AllLog = AllLog + log +"\n\n";
        }
        text_log.setText(AllLog);
    }
}
