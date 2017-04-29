package com.tasks.notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private static OnItemClickListener mListener;
    private Note[] mData;

    public ContentAdapter(Note[] objects, OnItemClickListener listener) {
        mData = objects;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mData[position].getTitle());
        holder.mDescription.setText(mData[position].getDescription());
        holder.mContainer.setBackgroundColor(mData[position].getColor());
        holder.mTitle.setVisibility("".equals(mData[position].getTitle()) ?
                View.GONE : View.VISIBLE);
        holder.mDescription.setVisibility("".equals(mData[position].getDescription()) ?
                View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;
        TextView mDescription;
        RelativeLayout mContainer;

        public ViewHolder(View viewItem) {
            super(viewItem);
            mTitle = (TextView) viewItem.findViewById(R.id.list_item_title);
            mDescription = (TextView) viewItem.findViewById(R.id.list_item_description);
            mContainer = (RelativeLayout) viewItem.findViewById(R.id.list_item_container);

            viewItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, this.getLayoutPosition());
        }
    }
}
