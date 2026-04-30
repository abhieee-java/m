package com.example.mydialer;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        // Hand the call data over to our Activity
        InCallActivity.setCall(call);

        // Force the app to open the In-Call screen!
        Intent intent = new Intent(this, InCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        InCallActivity.setCall(null);
    }
}
