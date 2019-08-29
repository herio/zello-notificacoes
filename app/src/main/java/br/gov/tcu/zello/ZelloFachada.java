package br.gov.tcu.zello;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import static android.app.RemoteInput.addResultsToIntent;

class ZelloFachada {
    private StatusBarNotification sbn;
    private String destinatario;
    private Context context;

    ZelloFachada(StatusBarNotification sbn, Context context) {
        this.sbn = sbn;
        this.destinatario =  sbn.getNotification().extras.getString("android.title");
        this.context = context;
    }

    void recuperaRespostaAutomatica(String title, String text) {
        if (!title.contains("WhatsApp") && !title.contains("Você") && !title.contains("You") && !title.contains(":")) {
            new ZelloClient(this).execute(title, text);
        }
    }

    void recuperouRespostaAutomatica(DtoRespostaZello dtoResposta) {
        if (dtoResposta != null) {
            Notification.Action action = findActionResponse(dtoResposta);
            if (action != null) {
                android.app.RemoteInput rem = action.getRemoteInputs()[0];

                for (String resposta: dtoResposta.getRespostas()) {
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

    private Notification.Action findActionResponse(DtoRespostaZello dtoResposta) {
        if(destinatario.equals(dtoResposta.getDestinatario())) {
            Notification.Action[] actions = sbn.getNotification().actions;
            if (actions != null) {
                for (Notification.Action act : actions) {
                    String resp = act.title.toString().replaceAll("[^A-Za-z]+", "").toUpperCase();
                    if (resp.contains("RESP") || resp.contains("REPLY")) {
                        return act;
                    }
                }
            }
        }
        return null;
    }

}
