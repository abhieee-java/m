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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CALL_PHONE = 1001;
    private final StringBuilder number = new StringBuilder();
    private TextView numberDisplay;
    private View dialerSheet; 
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize views
        dialerSheet = findViewById(R.id.dialer_sheet);
        fab = findViewById(R.id.fab_show_dialer);
        numberDisplay = findViewById(R.id.number_display);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. The "Switch" Logic (Floating Red Button)
        fab.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            toggleDialer();
        });

        // 3. Bottom Navigation Logic (Swapping screens)
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_favorites) {
                // Switches to the Favorites grid screen
                selectedFragment = new FavoritesFragment();
                fab.hide(); 
                dialerSheet.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_recents) {
                // Shows the main Recents list (Home)
                fab.show(); 
                selectedFragment = null; 
            } else if (itemId == R.id.nav_contacts) {
                // Switches to the Settings/Font screen
                selectedFragment = new SettingsFragment();
                fab.hide(); 
                dialerSheet.setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            } else {
                // If recents is clicked, remove fragment to show the main list background
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (current != null) {
                    getSupportFragmentManager().beginTransaction().remove(current).commit();
                }
            }
            return true;
        });

        // 4. Set up the dialer buttons (0-9, *, #)
        setupDialpad();
    }

    private void toggleDialer() {
        if (dialerSheet.getVisibility() == View.VISIBLE) {
            dialerSheet.setVisibility(View.GONE);
        } else {
            dialerSheet.setVisibility(View.VISIBLE);
        }
    }

    private void setupDialpad() {
        int[] dialIds = {R.id.dial_0, R.id.dial_1, R.id.dial_2, R.id.dial_3, R.id.dial_4, 
                         R.id.dial_5, R.id.dial_6, R.id.dial_7, R.id.dial_8, R.id.dial_9, 
                         R.id.dial_star, R.id.dial_hash};

        View.OnClickListener listener = v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            number.append(((TextView) v).getText());
            updateDisplay();
        };

        for (int id : dialIds) {
            View button = findViewById(id);
            if (button != null) button.setOnClickListener(listener);
        }

        findViewById(R.id.call_button).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            makeCall();
        });
        
        findViewById(R.id.backspace_button).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            if (number.length() > 0) {
                number.deleteCharAt(number.length() - 1);
                updateDisplay();
            }
        });
    }

    private void updateDisplay() {
        if (number.length() == 0) {
            numberDisplay.setText("");
            numberDisplay.setHint("Enter number");
        } else {
            numberDisplay.setText(number.toString());
        }
    }

    private void makeCall() {
        if (number.length() > 0) {
            String dialedNumber = number.toString();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialedNumber));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL_PHONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL_PHONE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
        }
    }
}
