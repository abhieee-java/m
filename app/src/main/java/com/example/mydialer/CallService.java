package com.example.mydialer;

import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        // A call just started! (Incoming or Outgoing)
        Log.d("CallService", "A new call has started!");
        
        // In the next step, we will write code here to open YOUR custom 
        // "In-Call Screen" (with the big red hangup button) instead of the Oppo one.
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        // The call just ended!
        Log.d("CallService", "The call has ended.");
    }
}
