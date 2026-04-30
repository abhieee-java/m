package com.example.mydialer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FavoritesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // We will build the actual grid of faces later. For now, just load an empty layout so it doesn't crash!
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }
}
