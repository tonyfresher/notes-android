package com.tasks.notes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tasks.notes.classes.Filter;
import com.tasks.notes.R;

import java.util.List;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {
    public interface OnItemTouchListener {
        void onItemClick(View view, int position);
    }

    private final OnItemTouchListener listener;
    private final List<Filter> data;

    public FiltersAdapter(List<Filter> data, OnItemTouchListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View viewItem, OnItemTouchListener listener) {
            super(viewItem);
            name = (TextView) viewItem.findViewById(R.id.filter_edit_name);
            viewItem.setOnClickListener((v) -> listener.onItemClick(v, getLayoutPosition()));
        }
    }
}
