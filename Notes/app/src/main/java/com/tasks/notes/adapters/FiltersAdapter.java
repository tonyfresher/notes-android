package com.tasks.notes.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tasks.notes.classes.Filter;
import com.tasks.notes.R;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private static OnItemClickListener mListener;
    private Filter[] mData;

    public FiltersAdapter(Filter[] objects, OnItemClickListener listener) {
        mData = objects;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mData[position].getName());
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        public ViewHolder(View viewItem) {
            super(viewItem);
            name = (TextView) viewItem.findViewById(R.id.filter_name);
            viewItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, this.getLayoutPosition());
        }
    }
}
