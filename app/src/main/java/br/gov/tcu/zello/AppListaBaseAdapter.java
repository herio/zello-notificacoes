package br.gov.tcu.zello;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppListaBaseAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NotificacaoBean> notificacaoBeanList;

    public AppListaBaseAdapter(Context context, ArrayList<NotificacaoBean> notificacaoBeanList) {
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

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemtitle);
        TextView txtText = (TextView) rowView.findViewById(R.id.Itemtext);
        TextView txtPkg = (TextView) rowView.findViewById(R.id.Itempkg);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        NotificacaoBean m = notificacaoBeanList.get(position);
        if(m != null) {
            txtTitle.setText(m.getTitle());
            txtText.setText(m.getText());
            txtPkg.setText(m.getPkg());
            if (m.getImage() != null) {
                imageView.setImageBitmap(m.getImage());
            }
        }
        return rowView;

    };
}