package br.gov.tcu.zello;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ReplyIntentSender {
    private StatusBarNotification sbn;
    private Context context;

    public ReplyIntentSender(StatusBarNotification sbn, Context context) {
        this.sbn = sbn;
        this.context = context;
    }

    public void recuperaRespostaAutomatica(String title, String text) {
        Log.i("ReplyIntentSender", String.format("### recuperaRespostaAutomatica() title[%s], text[%s]", title, text));
        if (!title.contains("WhatsApp") && !title.contains("Você") && !title.contains("You") && !title.contains(":")) {
            logNoApp("recuperaRespostaAutomatica", String.format("Vai chamar ZelloClient title[%s], text[%s]", title, text));
            new ZelloClient(this).execute(title, text);
        }
    }

    public void recuperouRespostaAutomatica(String resposta) {
        Log.i("ReplyIntentSender", String.format("### recuperouRespostaAutomatica() resposta[%s]", resposta));
        logNoApp("recuperouRespostaAutomatica", String.format("Recebeu resposta do ZelloClient resposta[%s]", resposta));
        if(resposta != null) {
            Notification.Action action = findActionResponse(sbn);
            if (action == null) {
                Log.i("ReplyIntentSender", "### recuperouRespostaAutomatica() action == null");
                logNoApp("recuperouRespostaAutomatica", "action == null");
            } else {
                Log.i("ReplyIntentSender", String.format("### recuperouRespostaAutomatica() action != null"));
                logNoApp("recuperouRespostaAutomatica", "action != null");
                android.app.RemoteInput rem = action.getRemoteInputs()[0];
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putCharSequence(rem.getResultKey(), resposta);
                android.app.RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
                try {
                    action.actionIntent.send(context, 0, intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Notification.Action findActionResponse(StatusBarNotification sbn) {
        Log.i("ReplyIntentSender", String.format("### findActionResponse() actions[%s]", sbn.getNotification().actions));
        Notification.Action[] actions = sbn.getNotification().actions;
        if(actions != null) {
            for (Notification.Action act : actions) {
                if (act != null && act.getRemoteInputs() != null) {
                    if (act.title.toString().contains("RESP") || act.title.toString().contains("REPLY")) {
                        logNoApp("findActionResponse", String.format("Encontrou action[%s]", act.title.toString()));
                        return act;
                    }
                }
            }
        }
        logNoApp("findActionResponse", "Não encontrou action");
        return null;
    }

    //TESTE ZELLO
    public void logNoApp(String titulo, String descricao) {
        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", "com.whatsapp");
        msgrcv.putExtra("ticker", "ticker");
        msgrcv.putExtra("title", titulo);
        msgrcv.putExtra("text", descricao);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }
}
