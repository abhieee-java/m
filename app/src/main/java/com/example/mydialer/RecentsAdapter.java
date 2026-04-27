package com.example.mydialer;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.VH> {

    public interface OnRecentClick {
        void onClick(String number);
    }

    private final List<RecentsStore.Entry> items = new ArrayList<>();
    private final OnRecentClick listener;

    public RecentsAdapter(OnRecentClick listener) {
        this.listener = listener;
    }

    public void submit(List<RecentsStore.Entry> next) {
        items.clear();
        items.addAll(next);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RecentsStore.Entry e = items.get(position);
        holder.number.setText(e.number);
        holder.time.setText(DateUtils.getRelativeTimeSpanString(
                e.timestamp,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));
        holder.itemView.setOnClickListener(v -> listener.onClick(e.number));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView number;
        final TextView time;

        VH(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.recent_number);
            time = itemView.findViewById(R.id.recent_time);
        }
    }
}
