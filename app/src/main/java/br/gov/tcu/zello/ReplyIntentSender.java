package br.gov.tcu.zello;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import java.util.List;

import static android.app.RemoteInput.addResultsToIntent;

class ReplyIntentSender {
    private StatusBarNotification sbn;
    private Context context;

    ReplyIntentSender(StatusBarNotification sbn, Context context) {
        this.sbn = sbn;
        this.context = context;
    }

    void recuperaRespostaAutomatica(String title, String text) {
        if (!title.contains("WhatsApp") && !title.contains("Você") && !title.contains("You") && !title.contains(":")) {
            new ZelloClient(this).execute(title, text);
        }
    }

    void recuperouRespostaAutomatica(List<String> respostas) {
        if (respostas != null) {
            Notification.Action action = findActionResponse(sbn);
            if (action != null) {
                android.app.RemoteInput rem = action.getRemoteInputs()[0];

                for (String resposta: respostas) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(rem.getResultKey(), resposta);
                    addResultsToIntent(action.getRemoteInputs(), intent, bundle);
                    try {
                        action.actionIntent.send(context, 0, intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Notification.Action findActionResponse(StatusBarNotification sbn) {
        Notification.Action[] actions = sbn.getNotification().actions;
        if (actions != null) {
            for (Notification.Action act : actions) {
                String resp = act.title.toString().replaceAll("[^A-Za-z]+", "").toUpperCase();
                if (resp.contains("RESP") || resp.contains("REPLY")) {
                    return act;
                }
            }
        }
        return null;
    }

}
