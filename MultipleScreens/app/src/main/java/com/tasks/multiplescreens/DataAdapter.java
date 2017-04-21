package com.tasks.multiplescreens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {
    private Context context;
    private ItemContent[] data;

    public DataAdapter(Context context, ItemContent[] objects) {
        this.context = context;
        data = objects;
    }

    @Override
    public ItemContent getItem(int i) {
        return data[i];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.item_description);
            viewHolder.color = (ImageView) convertView.findViewById(R.id.item_color);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        reuse(viewHolder, position);

        return convertView;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    private void reuse(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(getItem(position).name);
        viewHolder.description.setText(getItem(position).description);
        viewHolder.color.setBackgroundColor(getItem(position).color);
    }

    static class ViewHolder {
        TextView name;
        TextView description;
        ImageView color;
    }
}
