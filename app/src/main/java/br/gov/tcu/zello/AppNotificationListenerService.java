package br.gov.tcu.zello;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;


public class AppNotificationListenerService extends NotificationListenerService {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("AppNotificationListener", ">>> onNotificationRemoved()");
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String tag = sbn.getTag();
        Bundle extras = sbn.getNotification().extras;

        String title = extras.getString("android.title");
        String ticker = "";
        if (sbn.getNotification().tickerText != null) {
            ticker = sbn.getNotification().tickerText.toString();
        }

        Object data = extras.get("android.bigText");
        if (data == null) {
            data = extras.get("android.text");
        }

        final String text = data == null ? "" : data.toString();

        Bitmap id = sbn.getNotification().largeIcon;
        Log.i("NOTIFICATION-SBN", String.format(">>>>>>> sbn[%s]", sbn.toString()));

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        msgrcv.putExtra("actions", sbn.getNotification().actions);

        if (id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon", byteArray);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

        if (pack.replaceAll("[^A-Za-z]+", "").toUpperCase().contains("WHATSAPP") && tag != null) {
            if (title != null) {
                Log.i("AppNotificationListener", String.format(">>> Vai responder pack[%s] title[%s], text[%s]", pack, title, text));
                new ReplyIntentSender(sbn, context).recuperaRespostaAutomatica(title, text);
            }
            cancelNotification(sbn.getKey());
        }
    }
}
