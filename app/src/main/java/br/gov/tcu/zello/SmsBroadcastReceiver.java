package br.gov.tcu.zello;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                if (pdusObj != null) {
                    for (Object o : pdusObj) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);
                        String senderNum = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();
                        Intent msgrcv = new Intent("Msg");

                        msgrcv.putExtra("package", "");
                        msgrcv.putExtra("ticker", senderNum);
                        msgrcv.putExtra("title", senderNum);
                        msgrcv.putExtra("text", message);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                    } // end for loop
                }
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", ">>> Exception " + e);
        }
    }
}