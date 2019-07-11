package br.gov.tcu.zello;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

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
        msgrcv.putExtra("actions", sbn.getNotification().actions);
        if (id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon", byteArray);
        }

        if(pack.equals("com.whatsapp")) {
            enviaRespostasWhatsApp(sbn, title, text);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }

    private void enviaRespostasWhatsApp(final StatusBarNotification sbn, String title, String text) {
        Log.i("AppNotificationListener", String.format("### enviaRespostasWhatsApp() title[%s], text[%s]", title, text));
        new ReplyIntentSender(sbn, title, text).sendNativeIntent();
    }

    class ReplyIntentSender {
        private StatusBarNotification sbn;
        private String title;
        private String text;

        public ReplyIntentSender(StatusBarNotification sbn, String title, String text) {
            this.sbn = sbn;
            this.title = title;
            this.text = text;
        }

        private boolean sendNativeIntent() {
            Log.i("AppNotificationListener", String.format("### ReplyIntentSender.sendNativeIntent() title[%s], text[%s]", title, text));
            String nomeAcaoResposta = "Resp.";
            Notification.Action action = recuperaActionResposta(sbn, nomeAcaoResposta);
            if(action == null) {
                Log.i("AppNotificationListener", "### ReplyIntentSender.sendNativeIntent() action == null");
            } else {
                if (!title.equalsIgnoreCase("Você")) {
                    String respostaAutomatica = "Olá, essa é uma resposta automática do Zello TCU, logo logo Hério Thiago irá visualizar a notificação e te responder";
                    Log.i("AppNotificationListener", String.format("### ReplyIntentSender.sendNativeIntent() action != null respostaAutomatica[%s]", respostaAutomatica));
                    android.app.RemoteInput rem = action.getRemoteInputs()[0];
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(rem.getResultKey(), respostaAutomatica);
                    android.app.RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
                    try {
                        cancelNotification(sbn.getKey());
                        action.actionIntent.send(context, 0, intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private Notification.Action recuperaActionResposta(StatusBarNotification sbn, String name) {
            Log.i("AppNotificationListener", String.format("### ReplyIntentSender.recuperaActionResposta() actions[%s]", sbn.getNotification().actions));
            Notification.Action[] actions = sbn.getNotification().actions;
            if(actions != null) {
                for (Notification.Action act : actions) {
                    if (act != null && act.getRemoteInputs() != null) {
                        if (act.title.toString().contains(name)) {
                            if (act.getRemoteInputs() != null)
                                return act;
                        }
                    }
                }
            }
            return null;
        }
    }
}
