package com.example.mydialer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSIONS = 1001;

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG
    };

    private final StringBuilder number = new StringBuilder();
    private TextView numberDisplay;
    private View dialerSheet;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions properly
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQ_PERMISSIONS);
        }

        dialerSheet = findViewById(R.id.dialer_sheet);
        fab = findViewById(R.id.fab_show_dialer);
        numberDisplay = findViewById(R.id.number_display);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // FAB toggle dialer
        fab.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            toggleDialer();
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
                fab.hide();
                dialerSheet.setVisibility(View.GONE);

            } else if (itemId == R.id.nav_recents) {
                selectedFragment = new RecentsFragment();
                fab.show();

            } else if (itemId == R.id.nav_contacts) {
                selectedFragment = new ContactsFragment();
                fab.hide();
                dialerSheet.setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Load default screen (Recents)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RecentsFragment())
                    .commit();
        }

        setupDialpad();
    }

    // Check all permissions
    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Show/hide dialer
    private void toggleDialer() {
        if (dialerSheet.getVisibility() == View.VISIBLE) {
            dialerSheet.setVisibility(View.GONE);
        } else {
            dialerSheet.setVisibility(View.VISIBLE);
        }
    }

    // Dialpad logic
    private void setupDialpad() {
        int[] dialIds = {
                R.id.dial_0, R.id.dial_1, R.id.dial_2, R.id.dial_3,
                R.id.dial_4, R.id.dial_5, R.id.dial_6,
                R.id.dial_7, R.id.dial_8, R.id.dial_9,
                R.id.dial_star, R.id.dial_hash
        };

        View.OnClickListener listener = v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            number.append(((TextView) v).getText());
            updateDisplay();
        };

        for (int id : dialIds) {
            View button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(listener);
            }
        }

        // Call button
        findViewById(R.id.call_button).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            makeCall();
        });

        // Backspace
        findViewById(R.id.backspace_button).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            if (number.length() > 0) {
                number.deleteCharAt(number.length() - 1);
                updateDisplay();
            }
        });
    }

    // Update display
    private void updateDisplay() {
        if (number.length() == 0) {
            numberDisplay.setText("");
            numberDisplay.setHint("Enter number");
        } else {
            numberDisplay.setText(number.toString());
        }
    }

    // Call logic
    private void makeCall() {
        if (number.length() == 0) return;

        String dialedNumber = number.toString();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialedNumber));
            startActivity(intent);

        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQ_PERMISSIONS);
        }
    }

    // Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSIONS) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted && number.length() > 0) {
                makeCall();
            } else if (!allGranted) {
                Toast.makeText(this,
                        "Permissions are required for calling and contacts",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}