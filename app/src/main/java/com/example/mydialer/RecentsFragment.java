package com.example.mydialer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentsFragment extends Fragment {

    private RecyclerView recentsList;
    private RecentsAdapter recentsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);

        recentsList = view.findViewById(R.id.recents_list);
        
        // Setup the adapter. When a user clicks a past call, we will handle it here later.
        recentsAdapter = new RecentsAdapter(number -> {
            // For now, doing nothing. Later we can make this click call the person back!
        });
        
        recentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        recentsList.setAdapter(recentsAdapter);

        loadLogs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs(); // Refresh the list every time you look at this screen
    }

    private void loadLogs() {
        if (getContext() != null && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            List<CallLogHelper.CallItem> logs = CallLogHelper.getRecentCalls(getContext());
            recentsAdapter.submit(logs);
        }
    }
}
