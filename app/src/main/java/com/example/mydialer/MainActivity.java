package com.example.mydialer;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

    // 1. ADVANCED PERMISSIONS: Required for the system to trust a default dialer
    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MANAGE_OWN_CALLS
    };

    private final StringBuilder number = new StringBuilder();
    private TextView numberDisplay;
    private View dialerSheet;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. TRIGGER DEFAULT APP PROMPT
        checkDefaultDialer();

        // Request permissions
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

        // Default to Recents
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RecentsFragment())
                    .commit();
        }

        setupDialpad();

        // 3. INTENT HANDLING: Proves to Android this is a real dialer
        handleExternalDialIntent(getIntent());
    }

    // Handles numbers passed from other apps while My Dialer is already open
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleExternalDialIntent(intent);
    }

    private void handleExternalDialIntent(Intent intent) {
        if (intent != null && intent.getData() != null) {
            if (Intent.ACTION_DIAL.equals(intent.getAction()) || Intent.ACTION_VIEW.equals(intent.getAction())) {
                String uriNumber = intent.getData().getSchemeSpecificPart();
                number.setLength(0);
                number.append(uriNumber);
                updateDisplay();
                dialerSheet.setVisibility(View.VISIBLE); // Pop up the sheet automatically
            }
        }
    }

    private void checkDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                    startActivityForResult(intent, 999);
                }
            }
        } else {
            Intent intent = new Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void toggleDialer() {
        if (dialerSheet.getVisibility() == View.VISIBLE) {
            dialerSheet.setVisibility(View.GONE);
        } else {
            dialerSheet.setVisibility(View.VISIBLE);
        }
    }

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
        if (number.length() == 0) return;
        String dialedNumber = number.toString();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialedNumber));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQ_PERMISSIONS);
        }
    }

    // NEW: Properly placed inside the class so the RecentsFragment can use it!
    public void makeCallDirectly(String numberToDial) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new android.content.Intent(android.content.Intent.ACTION_CALL, android.net.Uri.parse("tel:" + numberToDial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted && number.length() > 0) makeCall();
        }
    }
}
