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

    // Now correctly using CallItem instead of Entry
    private List<CallLogHelper.CallItem> callItems = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String number);
    }

    public RecentsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Now correctly accepting List<CallLogHelper.CallItem>
    public void submit(List<CallLogHelper.CallItem> items) {
        this.callItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallLogHelper.CallItem item = callItems.get(position);
        
        // Show number if name is unknown
        holder.nameView.setText(item.name.equals("Unknown") ? item.number : item.name);
        
        // Set the arrow based on call type
        String arrow = "↗"; // Outgoing default
        if ("Incoming".equals(item.type)) arrow = "↙";
        if ("Missed".equals(item.type)) {
            arrow = "❌";
            holder.nameView.setTextColor(0xFFFF0000); // Make name red for missed calls
        } else {
            holder.nameView.setTextColor(0xFFFFFFFF); // White for normal
        }
        
        holder.typeIconView.setText(arrow);
        holder.timeView.setText("Mobile, " + item.date);

        // Click to dial
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item.number));
    }

    @Override
    public int getItemCount() {
        return callItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView typeIconView;
        TextView timeView;

        ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.call_name);
            typeIconView = view.findViewById(R.id.call_type_icon);
            timeView = view.findViewById(R.id.call_time);
        }
    }
}
