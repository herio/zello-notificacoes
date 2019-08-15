package br.gov.tcu.zello;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AppListaBaseAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private ArrayList<NotificacaoBean> notificacaoBeanList;

    AppListaBaseAdapter(Context context, ArrayList<NotificacaoBean> notificacaoBeanList) {
        this.context = context;
        this.notificacaoBeanList = notificacaoBeanList;
    }

    @Override
    public int getCount() {
        return notificacaoBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificacaoBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.list_item, null, true);
        rowView.setOnClickListener(this);
        rowView.setTag(String.valueOf(position));

        TextView txtTitle = rowView.findViewById(R.id.Itemtitle);
        TextView txtText = rowView.findViewById(R.id.Itemtext);
        TextView txtPkg = rowView.findViewById(R.id.Itempkg);
        ImageView imageView = rowView.findViewById(R.id.icon);

        NotificacaoBean m = notificacaoBeanList.get(position);
        if (m != null) {
            txtTitle.setText(m.getTitle());
            txtText.setText(m.getText());
            txtPkg.setText(m.getPkg());
            if (m.getImage() != null) {
                imageView.setImageBitmap(m.getImage());
            }
        }
        return rowView;

    }

    public void onClick(View v) {
        int pos = Integer.parseInt(v.getTag().toString());
        Toast.makeText(context, String.format("Removendo notificação pos[%s] ", pos), Toast.LENGTH_LONG).show();
        notificacaoBeanList.remove(pos);
        notifyDataSetChanged();
    }
}