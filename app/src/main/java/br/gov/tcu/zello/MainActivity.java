package br.gov.tcu.zello;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ListView list;
    private AppListaBaseAdapter adapter;
    private ArrayList<NotificacaoBean> notificacaoBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificacaoBeanList = new ArrayList<NotificacaoBean>();
        adapter = new AppListaBaseAdapter(getApplicationContext(), notificacaoBeanList);
        list = (ListView) findViewById(R.id.list);
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

    private final BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String title = intent.getStringExtra("title");
            String pkg = intent.getStringExtra("package");
            String text = intent.getStringExtra("text");
            Log.i("MainActivity",
                    String.format(">>> onReceive() title[%s], text[%s] pkg[%s]: ", title, text, pkg));

            try {
                byte[] byteArray = intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if (byteArray != null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                NotificacaoBean notificacaoBean = new NotificacaoBean();
                notificacaoBean.setTitle(title);
                notificacaoBean.setText(text);
                notificacaoBean.setPkg(pkg);
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
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    };
}