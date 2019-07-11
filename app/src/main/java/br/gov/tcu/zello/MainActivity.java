package br.gov.tcu.zello;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ListView list;
    private AppListaBaseAdapter adapter;
    private ArrayList<NotificacaoBean> notificacaoBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificacaoBeanList = new ArrayList<>();
        adapter = new AppListaBaseAdapter(getApplicationContext(), notificacaoBeanList);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuracoes:
                Intent intent = new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                return true;
            case R.id.limpar_notificacoes:
                notificacaoBeanList.clear();
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sair:
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enviaRespostasWhatsApp(final Notification.Action[] actions, String title, String text) {
        Log.i("AppNotificationListener", String.format("### enviaRespostasWhatsApp() title[%s], text[%s]", title, text));
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Notificação é do WhatsApp")
                .setMessage(String.format("### enviaRespostasWhatsApp() title[%s], text[%s]", title, text))
                .setPositiveButton(android.R.string.yes, new DialogEnviaResposta(actions, title, text))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String title = intent.getStringExtra("title");
            final String pack = intent.getStringExtra("package");
            final String text = intent.getStringExtra("text");
            //final Notification.Action[] actions = intent.getParcelableExtra("actions");

            Log.i("MainActivity",
                    String.format(">>> onReceive() title[%s], text[%s] pkg[%s]: ", title, text, pack));

            try {
                byte[] byteArray = intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if (byteArray != null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                NotificacaoBean notificacaoBean = new NotificacaoBean();
                notificacaoBean.setTitle(title);
                notificacaoBean.setText(text);
                notificacaoBean.setPkg(pack);
                notificacaoBean.setImage(bmp);

                if (notificacaoBeanList != null) {
                    notificacaoBeanList.add(notificacaoBean);
                    adapter.notifyDataSetChanged();
                } else {
                    notificacaoBeanList = new ArrayList<NotificacaoBean>();
                    notificacaoBeanList.add(notificacaoBean);
                    adapter = new AppListaBaseAdapter(getApplicationContext(), notificacaoBeanList);
                    list = (ListView) findViewById(R.id.list);
                    list.setAdapter(adapter);
                }

//            if(pack.equals("com.whatsapp")) {
//                enviaRespostasWhatsApp(actions, title, text);
//            }

            } catch(Exception e){
                e.printStackTrace();
            }
        }

    };

    class DialogEnviaResposta implements DialogInterface.OnClickListener {
        private final Notification.Action[] actions;
        private String title;
        private String text;

        public DialogEnviaResposta(final Notification.Action[] actions, String title, String text) {
            this.actions = actions;
            this.title = title;
            this.text = text;
        }

        public void onClick(DialogInterface dialog, int which) {
            Log.i("AppNotificationListener", String.format("### DialogEnviaResposta.onClick() title[%s], text[%s]", title, text));
            new ReplyIntentSender(actions, title, text).sendNativeIntent();
        }
    }

    class ReplyIntentSender {
        private Notification.Action[] actions;
        private String title;
        private String text;

        public ReplyIntentSender(Notification.Action[] actions, String title, String text) {
            this.actions = actions;
            this.title = title;
            this.text = text;
        }

        private boolean sendNativeIntent() {
            Log.i("AppNotificationListener", String.format("### ReplyIntentSender.sendNativeIntent() title[%s], text[%s]", title, text));
            String nomeAcaoResposta = "Resp.";
            Notification.Action action = recuperaActionResposta(actions, nomeAcaoResposta);
            if(action == null) {
                Log.i("AppNotificationListener", "### ReplyIntentSender.sendNativeIntent() action == null");
            } else {
                String respostaAutomatica =  String.format("Olá, essa é uma resposta automática do Zello TCU title[%s], text[%s]", title, text);
                Log.i("AppNotificationListener", String.format("### ReplyIntentSender.sendNativeIntent() action != null respostaAutomatica[%s]", respostaAutomatica));
                for (android.app.RemoteInput rem : action.getRemoteInputs()) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(rem.getResultKey(), respostaAutomatica);
                    android.app.RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
                    try {
                        action.actionIntent.send(MainActivity.this, 0, intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private Notification.Action recuperaActionResposta(Notification.Action[] actions, String name) {
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
