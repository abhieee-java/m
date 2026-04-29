package com.example.mydialer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CALL_PHONE = 1001;

    private final StringBuilder number = new StringBuilder();
    private TextView numberDisplay;
    private RecyclerView recentsList;
    private View recentsEmpty;
    private View dialerSheet; // The sliding panel for the dialer
    private RecentsStore recentsStore;
    private RecentsAdapter recentsAdapter;

    private final int[] dialIds = new int[]{
            R.id.dial_0, R.id.dial_1, R.id.dial_2, R.id.dial_3, R.id.dial_4,
            R.id.dial_5, R.id.dial_6, R.id.dial_7, R.id.dial_8, R.id.dial_9,
            R.id.dial_star, R.id.dial_hash
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        numberDisplay = findViewById(R.id.number_display);
        recentsList = findViewById(R.id.recents_list);
        recentsEmpty = findViewById(R.id.recents_empty);
        dialerSheet = findViewById(R.id.dialer_sheet);
        FloatingActionButton fab = findViewById(R.id.fab_show_dialer);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set up Recents List
        recentsStore = new RecentsStore(this);
        recentsAdapter = new RecentsAdapter(this::onRecentTapped);
        recentsList.setLayoutManager(new LinearLayoutManager(this));
        recentsList.setAdapter(recentsAdapter);

        // THE SWITCH: Show/Hide Dialer
        fab.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (dialerSheet.getVisibility() == View.VISIBLE) {
                dialerSheet.setVisibility(View.GONE);
            } else {
                dialerSheet.setVisibility(View.VISIBLE);
            }
        });

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_favorites) {
                Toast.makeText(this, "Favorites Coming Soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_recents) {
                // Already on Recents
            } else if (itemId == R.id.nav_contacts) {
                Toast.makeText(this, "Contacts Coming Soon", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Dialpad Number Listeners
        View.OnClickListener digitListener = v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            CharSequence label = ((TextView) v).getText();
            if (label != null && label.length() > 0) {
                number.append(label.charAt(0));
                refreshDisplay();
            }
        };
        for (int id : dialIds) {
            findViewById(id).setOnClickListener(digitListener);
        }

        // Backspace Logic
        ImageButton backspace = findViewById(R.id.backspace_button);
        backspace.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            if (number.length() > 0) {
                number.deleteCharAt(number.length() - 1);
                refreshDisplay();
            }
        });
        backspace.setOnLongClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            number.setLength(0);
            refreshDisplay();
            return true;
        });

        // Call Button Logic
        findViewById(R.id.call_button).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            attemptCall();
        });

        refreshDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadRecents();
    }

    private void reloadRecents() {
        java.util.List<RecentsStore.Entry> items = recentsStore.load();
        recentsAdapter.submit(items);
        boolean empty = items.isEmpty();
        recentsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recentsList.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void onRecentTapped(String tappedNumber) {
        number.setLength(0);
        number.append(tappedNumber);
        refreshDisplay();
        dialerSheet.setVisibility(View.VISIBLE); // Show dialer if it was hidden
        attemptCall();
    }

    private void refreshDisplay() {
        if (number.length() == 0) {
            numberDisplay.setText("");
            numberDisplay.setHint("Enter number");
        } else {
            numberDisplay.setText(number.toString());
        }
    }

    private void attemptCall() {
        if (number.length() == 0) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQ_CALL_PHONE);
            return;
        }
        placeCall();
    }

    private void placeCall() {
        try {
            String dialed = number.toString();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialed));
            startActivity(intent);
            recentsStore.add(dialed);
            reloadRecents();
        } catch (SecurityException ignored) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL_PHONE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            placeCall();
        }
    }
}
