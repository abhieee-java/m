package com.example.mydialer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.ViewHolder> {

    // Must use CallLogHelper.CallItem to match the Fragment
    private List<CallLogHelper.CallItem> callItems = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String number);
    }

    public RecentsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<CallLogHelper.CallItem> items) {
        this.callItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogHelper.CallItem item = callItems.get(position);
        holder.nameView.setText(item.name.equals("Unknown") ? item.number : item.name);
        holder.timeView.setText(item.date);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item.number));
    }

    @Override
    public int getItemCount() {
        return callItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, timeView;
        ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.call_name);
            timeView = view.findViewById(R.id.call_time);
        }
    }
}
