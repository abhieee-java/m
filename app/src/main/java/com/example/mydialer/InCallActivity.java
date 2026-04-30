package com.example.mydialer;

import android.os.Bundle;
import android.telecom.Call;
import androidx.appcompat.app.AppCompatActivity;

public class InCallActivity extends AppCompatActivity {

    private static Call currentCall;

    public static void setCall(Call call) {
        currentCall = call;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // This keeps the app from crashing when a call comes in.
        // We will design the actual R.layout.activity_in_call next!
    }
}
