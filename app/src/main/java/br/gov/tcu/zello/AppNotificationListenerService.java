package br.gov.tcu.zello;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class AppNotificationListenerService extends NotificationListenerService {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker = "";
        if (sbn.getNotification().tickerText != null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        Object data = extras.get("android.bigText");
        if (data == null) {
            data = extras.get("android.text");
        }
        final String text = data.toString();
        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap id = sbn.getNotification().largeIcon;

        Log.i("AppNotificationListener",
                String.format(">>> onNotificationPosted() Title[%s] Text[%s] Package[%s] Ticker[%s]", title, text, pack, ticker));

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if (id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon", byteArray);
        }

        if(pack.equals("com.whatsapp")) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(">>>Vai chamar zello " + text);
                    try {
                        StringBuilder sb = new StringBuilder();
                        URL url;
                        HttpURLConnection urlConn;
                        DataOutputStream printout;
                        DataInputStream input;
                        url = new URL ("https://chatbot.apps.tcu.gov.br/rasa/webhooks/whatsapp/webhook");
                        urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput (true);
                        urlConn.setDoOutput (true);
                        urlConn.setUseCaches (false);
                        urlConn.setRequestProperty("Content-Type","application/json");
                        urlConn.connect();
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("sender", "61981656123");
                        jsonParam.put("message", text);
                        printout = new DataOutputStream(urlConn.getOutputStream ());
                        printout.writeBytes(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
                        printout.flush ();
                        printout.close ();
                        OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream());
                        out.write(jsonParam.toString());
                        out.close();

                        if(urlConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"utf-8"));
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            System.out.println(">>>" + sb.toString());
                        }else{
                            System.out.println(">>>" + urlConn.getResponseMessage());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("AppNotificationListener", ">>> onNotificationRemoved()");
    }
}
