package br.gov.tcu.zello;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
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
            new ZelloClient(this).execute(title, text);
        }
    }

    public void recuperouRespostaAutomatica(String resposta) {
        Log.i("ReplyIntentSender", String.format("### recuperouRespostaAutomatica() resposta[%s]", resposta));
        if(resposta != null) {
            String nomeAcaoResposta = "Resp";
            Notification.Action action = findActionResponse(sbn, nomeAcaoResposta);
            if (action == null) {
                Log.i("ReplyIntentSender", "### recuperouRespostaAutomatica() action == null");
            } else {
                Log.i("ReplyIntentSender", String.format("### recuperouRespostaAutomatica() action != null"));
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

    private Notification.Action findActionResponse(StatusBarNotification sbn, String name) {
        Log.i("ReplyIntentSender", String.format("### findActionResponse() actions[%s]", sbn.getNotification().actions));
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
