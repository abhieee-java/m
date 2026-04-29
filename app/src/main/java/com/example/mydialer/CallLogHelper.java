package com.example.mydialer;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogHelper {

    public static class CallItem {
        public String name;
        public String number;
        public String date;
        public String type; // Incoming, Outgoing, Missed
    }

    public static List<CallItem> getRecentCalls(Context context) {
        List<CallItem> callList = new ArrayList<>();
        
        // Check if we have permission first
        if (context.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return callList;
        }

        Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC LIMIT 50"); // Get last 50 calls

        if (cursor != null) {
            int numberCol = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateCol = cursor.getColumnIndex(CallLog.Calls.DATE);
            int nameCol = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            while (cursor.moveToNext()) {
                CallItem item = new CallItem();
                item.number = cursor.getString(numberCol);
                item.name = cursor.getString(nameCol);
                if (item.name == null || item.name.isEmpty()) {
                    item.name = "Unknown";
                }

                long dateInMillis = cursor.getLong(dateCol);
                item.date = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(new Date(dateInMillis));

                int callType = cursor.getInt(typeCol);
                switch (callType) {
                    case CallLog.Calls.OUTGOING_TYPE: item.type = "Outgoing"; break;
                    case CallLog.Calls.INCOMING_TYPE: item.type = "Incoming"; break;
                    case CallLog.Calls.MISSED_TYPE: item.type = "Missed"; break;
                    default: item.type = "Other"; break;
                }
                callList.add(item);
            }
            cursor.close();
        }
        return callList;
    }
}
