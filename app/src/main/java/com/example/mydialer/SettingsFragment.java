package com.example.mydialer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // This is the button we created in the XML
        View fontBtn = view.findViewById(R.id.btn_upload_font);

        // This opens the File Picker when clicked
        fontBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // Look for all files, we will filter for .ttf
            fontPickerLauncher.launch(intent);
        });

        return view;
    }

    // This handles what happens after you pick a file
    private final ActivityResultLauncher<Intent> fontPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && uri.getPath().endsWith(".ttf")) {
                        // Success! In the next step, we will save this to the app memory
                        Toast.makeText(getContext(), "Font Selected: " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please select a valid .ttf file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
