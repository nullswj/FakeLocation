package com.swj.fakelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.swj.fakelocation.FakeLocationApplication.realuri;
import static com.swj.fakelocation.FakeLocationApplication.uri;


public  class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private FloatingActionButton btn_start;

    private FloatingActionButton btn_stop;

    private static double fake_lat;

    private static double fake_lon;


    private WebView map;

    public LocationClient locationClient = null;

    private MyLocationListener locationListener = new MyLocationListener();

    public static List<String> logList = new CopyOnWriteArrayList<>();


    private ContentValues values = new ContentValues();

    private TextView text_log = null;

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            String Url = "https://apis.map.qq.com/tools/locpicker?type=0&backurl=https://callback&coordtype=5&coord="+FakeLocationApplication.getLatitude()
                    +"," +FakeLocationApplication.getLongtitude()+"&radius=2000&total=20&key=FAGBZ-66IWV-L4KPW-UODMT-3WUQZ-B6FMX&referer=fakelocation";
            map.loadUrl(Url);

            values.put("lat",FakeLocationApplication.getLatitude());
            values.put("lon",FakeLocationApplication.getLongtitude());
            getContentResolver().insert(realuri,values);
        }

    };


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

        }
    }

    public class MyLocationListener extends BDAbstractLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            FakeLocationApplication.setLatitude(bdLocation.getLatitude());
            FakeLocationApplication.setLongtitude(bdLocation.getLongitude());

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

        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        map = findViewById(R.id.web_map);

        WebSettings settings = map.getSettings();
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
        settings.setAllowFileAccess(false);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) //利用分支语句进行判断
        {
            case 1:
                boolean flag = true;
                if (grantResults.length > 0)
                {
                    for (int result : grantResults)  //在grantResult中提取数据
                    {
                        if (result != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            flag = false;
                            break;
                        }
                    }
                    //开始执行
                    if(flag == true)
                    {
                        setLocation();
                        locationClient.start();
                    }

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





    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
