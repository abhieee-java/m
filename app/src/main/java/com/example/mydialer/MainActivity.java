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
    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE,
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

        checkDefaultDialer();

        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQ_PERMISSIONS);
        }

        dialerSheet = findViewById(R.id.dialer_sheet);
        fab = findViewById(R.id.fab_show_dialer);
        numberDisplay = findViewById(R.id.number_display);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (fab != null) {
            fab.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                toggleDialer();
            });
        }

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_favorites) selectedFragment = new FavoritesFragment();
                else if (itemId == R.id.nav_recents) selectedFragment = new RecentsFragment();
                else if (itemId == R.id.nav_contacts) selectedFragment = new ContactsFragment();

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentsFragment()).commit();
        }

        setupDialpad();
    }

    private void checkDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
            if (roleManager != null && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                startActivityForResult(roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER), 999);
            }
        }
    }

    public void makeCallDirectly(String numberToDial) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToDial)));
        }
    }

    private boolean hasAllPermissions() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private void toggleDialer() {
        if (dialerSheet != null) dialerSheet.setVisibility(dialerSheet.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void setupDialpad() {
        int[] ids = {R.id.dial_0, R.id.dial_1, R.id.dial_2, R.id.dial_3, R.id.dial_4, R.id.dial_5, R.id.dial_6, R.id.dial_7, R.id.dial_8, R.id.dial_9, R.id.dial_star, R.id.dial_hash};
        View.OnClickListener l = v -> {
            number.append(((TextView) v).getText());
            if (numberDisplay != null) numberDisplay.setText(number.toString());
        };
        for (int id : ids) {
            View b = findViewById(id);
            if (b != null) b.setOnClickListener(l);
        }
        findViewById(R.id.call_button).setOnClickListener(v -> makeCallDirectly(number.toString()));
    }
}
