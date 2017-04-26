package com.tasks.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {
    private Context context;
    private NoteContent[] data;

    public DataAdapter(Context context, NoteContent[] objects) {
        this.context = context;
        data = objects;
    }

    @Override
    public NoteContent getItem(int i) {
        return data[i];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.note_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.note_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.note_description);
            viewHolder.color = (ImageView) convertView.findViewById(R.id.note_color);
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
