package com.gamespace;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.ApplicationInfo;
import java.util.List;

public class AppAdapter extends BaseAdapter {
    private Context context;
    private List<ApplicationInfo> apps;
    private PackageManager pm;

    public AppAdapter(Context context, List<ApplicationInfo> apps) {
        this.context = context;
        this.apps = apps;
        this.pm = context.getPackageManager();
    }

    @Override
    public int getCount() { return apps.size(); }

    @Override
    public Object getItem(int position) { return apps.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.activity_list_item, parent, false);
        }
        ImageView icon = convertView.findViewById(android.R.id.icon);
        TextView name = convertView.findViewById(android.R.id.text1);
        
        ApplicationInfo app = apps.get(position);
        icon.setImageDrawable(app.loadIcon(pm));
        name.setText(app.loadLabel(pm));
        name.setTextColor(0xFFFFFFFF);
        name.setTextSize(12f);
        name.setMaxLines(1);
        
        return convertView;
    }
}
