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
        // Loads your custom Nothing OS layout
        View view = inflater.inflate(R.layout.fragment_recents, container, false);

        recentsList = view.findViewById(R.id.recents_list);
        
        // If the user clicks a past call, dial it immediately!
        recentsAdapter = new RecentsAdapter(number -> {
            ((MainActivity) requireActivity()).makeCallDirectly(number);
        });
        
        recentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        recentsList.setAdapter(recentsAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs(); // Reload logs every time this screen is shown
    }

    public void loadLogs() {
        if (getContext() != null && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            List<CallLogHelper.CallItem> logs = CallLogHelper.getRecentCalls(getContext());
            recentsAdapter.submit(logs);
        }
    }
}
