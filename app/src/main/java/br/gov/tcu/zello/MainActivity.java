package br.gov.tcu.zello;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


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

            final String title = intent.getStringExtra("title");
            final String pkg = intent.getStringExtra("package");
            final String text = intent.getStringExtra("text");
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

//                try {
//                    PackageManager packageManager = context.getPackageManager();
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    String url = "https://api.whatsapp.com/send?phone=61993974601&text=" + URLEncoder.encode(text, "UTF-8");
//                    i.setPackage("com.whatsapp");
//                    i.setData(Uri.parse(url));
//                    if (i.resolveActivity(packageManager) != null) {
//                        context.startActivity(i);
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    };
}
