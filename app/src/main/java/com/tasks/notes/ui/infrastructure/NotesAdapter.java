package com.tasks.notes.ui.infrastructure;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tasks.notes.data.model.Note;
import com.tasks.notes.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    public interface OnItemTouchListener {
        void onItemClick(View v, int position);
    }

    private final OnItemTouchListener listener;
    private final List<Note> data;

    public NotesAdapter(List<Note> data, OnItemTouchListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.description.setText(data.get(position).getDescription());
        holder.container.setBackgroundColor(data.get(position).getColor());
        holder.title.setVisibility("".equals(data.get(position).getTitle()) ?
                View.GONE : View.VISIBLE);
        holder.description.setVisibility("".equals(data.get(position).getDescription()) ?
                View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_title)
        TextView title;
        @BindView(R.id.list_item_description)
        TextView description;
        @BindView(R.id.list_item_container)
        RelativeLayout container;

        public ViewHolder(View viewItem, OnItemTouchListener listener) {
            super(viewItem);
            ButterKnife.bind(this, viewItem);
            viewItem.setOnClickListener((v) -> listener.onItemClick(v, getLayoutPosition()));
        }
    }
}
