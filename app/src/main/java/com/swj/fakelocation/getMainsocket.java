package com.swj.fakelocation;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.security.auth.login.LoginException;

import static com.swj.fakelocation.MainActivity.latitude;
import static com.swj.fakelocation.MainActivity.longtitude;

class GpsLocation
{
    double lon;
    double lat;
}

public class getMainsocket
{
    private static String ip = "47.112.2.66";

    private static int port = 1234;

    private static int result;

    public static int  connMainsocket(final String tag, final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                try {

                    //int result;

                    JSONObject jsonOk = new JSONObject();

                    jsonOk.put("ok","ok");

                    Socket socket = new Socket(ip, port);



                    //socket.setSoTimeout(2000);

                    Log.e(tag, "连接成功" );

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    PrintWriter pw = new PrintWriter(out);

                    //out.writeUTF(send);

                    byte[] bytes = new byte[1024];

                    int len = in.read(bytes);

                    String receiver = new String(bytes,0,len);

                    Log.e(tag, "接收成功 ");



                    JSONObject jsonObject = new JSONObject(receiver);

                    String real = "";
                    if(jsonObject.getString("real")!=null)
                    {
                        real = jsonObject.getString("real");
                        Log.e(tag, "real:"+real);
                    }

                    String cheat = "";
                    if(jsonObject.getString("cheat") != null)
                    {
                        Log.e(tag, "cheat"+cheat);
                        cheat = jsonObject.getString("cheat");
                    }

                    String dire = "";
                    if(jsonObject.getString("dire")!=null)
                    {
                        dire = jsonObject.getString("dire");
                        Log.e(tag, "dire"+dire);
                    }

                    String exit = "";
                    if(jsonObject.getString("exit")!=null)
                    {
                        exit = jsonObject.getString("exit");
                        Log.e(tag, "exit"+exit);
                    }


                    Log.e(tag, "开始判断 " );
                    if (exit.equals("1"))
                    {
                        result = -2;
                        Log.e(tag, "exit_handle");
                        out.write(jsonOk.toString().getBytes());
                        Log.e(tag, "exit_handle_success");
                        out.close();
                        socket.close();
                        return;
                    }
                    if(real.equals("1") && dire.equals("1") && cheat.equals("1"))
                    {
                        result = -1;
                        Log.e(tag, "real_dire_cheat_handle");
                        out.write(getAll().getBytes());
                        Log.e(tag, "real_dire_handle_success");
                        setGPS(tag,jsonObject,handler);
                    }
                    else if (real.equals("1") && dire.equals("1"))
                    {
                        result = 0;
                        Log.e(tag, "real_dire_handle");
                        Log.e(tag, getAll());

                        out.write(getAll().getBytes());
                        Log.e(tag, "real_dire_handle_success");
                    }
                    else if(real.equals("1") && cheat.equals("1"))
                    {
                        result = 1;
                        Log.e(tag, "real_handle");
                        out.write(getReal().getBytes());
                        Log.e(tag, "real_dire_handle_real");
                        setGPS(tag,jsonObject,handler);
                    }
                    else if(real.equals("1"))
                    {
                        result = 1;
                        Log.e(tag, "real_handle");

                        out.write(getReal().getBytes());
                        Log.e(tag, "real_dire_handle_real");
                    }
                    else if (cheat.equals("1"))
                    {
                        setGPS(tag,jsonObject,handler);

                        out.write(jsonOk.toString().getBytes());
                        Log.e(tag, "real_dire_handle_ok");
                    }
                    else if (dire.equals("1"))
                    {
                        result = 3;
                        Log.e(tag, "dire_handle");
                        out.write(getDir().getBytes());
                        Log.e(tag, "dire_handle_ok");
                    }
                    Log.e(tag, "结束判断 " );

                    out.close();
                    in.close();
                    socket.close();

//                    Thread.sleep(1000);

//                    Message message = new Message();
//                    message.what = 0;
//                    message.arg1 = Integer.parseInt(jsonObject.getString("port1"));
//                    handler.sendMessage(message);
                } catch (IOException e) {
                    Log.e(tag, Log.getStackTraceString(e));
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag, "json 异常: ");
                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }

            }
        }.start();
        return result;
    }

    public static String getReal()
    {
        String send = "";
        try {
            JSONObject jsonRealSend = new JSONObject();
            JSONObject jsonReal = new JSONObject();
            jsonReal.put("lat",latitude);
            jsonReal.put("lon",longtitude);
            jsonRealSend.put("real",jsonReal);
            send = jsonRealSend.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return send;
    }

    public static String getDir()
    {
        String send = "";
        try
        {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonDir = new JSONObject();
            jsonDir.put("许江","13569856547");
            jsonDir.put("谢康","18956475896");
            jsonSend.put("dire",jsonDir);
            send = jsonSend.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return send;
    }
    public static String getAll()
    {
        String send = "";
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonReal = new JSONObject();
            jsonReal.put("lat",latitude);
            jsonReal.put("lng",longtitude);
            jsonSend.put("real",jsonReal);
            JSONObject jsonDir = new JSONObject();
            jsonDir.put("许江","13569856547");
            jsonDir.put("谢康","18956475896");
            jsonSend.put("dire",jsonDir);
            send = jsonSend.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return send;
    }

    private static void setGPS(final String tag,JSONObject jsonObject,Handler handler) throws JSONException
    {
        result = 2;
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
