package br.gov.tcu.zello;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZelloClient extends AsyncTask<String, Void, String> {
       private ReplyIntentSender replyIntentSender;

       public ZelloClient(ReplyIntentSender replyIntentSender) {
              this.replyIntentSender = replyIntentSender;
       }

       @Override
       protected String doInBackground(String... params) {
              Log.i("ZelloClient", String.format("### doInBackground params0[%s] params1[%s]", params[0], params[1]));
              HttpURLConnection urlConnection = null;
              try {
                     URL url = new URL("https://chatbot.apps.tcu.gov.br/rasa/webhooks/whatsapp/webhook");
                     urlConnection = (HttpURLConnection) url.openConnection();
                     urlConnection.setRequestMethod("POST");
                     JSONObject jsonParam = new JSONObject();
                     jsonParam.put("sender", params[0]);
                     jsonParam.put("message", params[1]);
                     setPostRequestContent(urlConnection, jsonParam);
                     urlConnection.connect();
                     Log.i("ZelloClient", String.format("### doInBackground url[%s] jsonParam[%s] response[%s]", url, jsonParam.toString(), urlConnection.getResponseCode()));
                     if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            return urlConnection.getResponseMessage();
                     }
              } catch (Exception e) {
                     e.printStackTrace();
                     Log.e("ZelloClient", String.format(">>> doInBackground() message[%s], cause[%s], e[%s]", e.getMessage(), e.getCause(), e));
              } finally {
                     if (urlConnection != null) {
                            urlConnection.disconnect();
                     }
              }
              return null;
       }

       private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
              OutputStream os = conn.getOutputStream();
              BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
              writer.write(jsonObject.toString());
              writer.flush();
              writer.close();
              os.close();
       }

       @Override
       protected void onPostExecute(String resposta) {
              if(replyIntentSender != null) {
                     replyIntentSender.recuperouRespostaAutomatica(resposta);
              }
       }
}