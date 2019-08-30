package br.gov.tcu.zello;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ZelloClient extends AsyncTask<String, Void, DtoRespostaZello> {
    private ZelloFachada replyIntentSender;

    ZelloClient(ZelloFachada replyIntentSender) {
        this.replyIntentSender = replyIntentSender;
    }

    @Override
    protected DtoRespostaZello doInBackground(String... params) {
        Log.i("ZelloClient", String.format("### doInBackground params0[%s] params1[%s]", params[0], params[1]));
        HttpURLConnection urlConnection = null;
        try {
            List<String> list = new ArrayList<>();
            URL url = new URL("https://chatbot.apps.tcu.gov.br/rasa/webhooks/whatsapp/webhook");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("sender", params[0]);
            jsonParam.put("message", params[1]);

            setPostRequestContent(urlConnection, jsonParam);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(inputStream);
            JsonNode textNode = jsonNode.get("text");
            JsonNode destinatarioNode = jsonNode.get("recipient_id");

            if (textNode.isArray()) {
                for (final JsonNode objNode : textNode) {
                    list.add(objNode.textValue());
                }
            }

            DtoRespostaZello dtoResposta = new DtoRespostaZello();
            dtoResposta.setRespostas(list);
            dtoResposta.setDestinatario(destinatarioNode.textValue());
            return dtoResposta;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    @Override
    protected void onPostExecute(DtoRespostaZello dtoResposta) {
        if (replyIntentSender != null) {
            replyIntentSender.recuperouRespostaAutomatica(dtoResposta);
        }
    }
}