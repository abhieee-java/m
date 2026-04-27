package com.example.mydialer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecentsStore {

    private static final String PREFS = "mydialer_prefs";
    private static final String KEY = "recents";
    private static final int MAX_ENTRIES = 20;
    private static final String ENTRY_SEP = "\n";
    private static final String FIELD_SEP = "\t";

    public static class Entry {
        public final String number;
        public final long timestamp;

        public Entry(String number, long timestamp) {
            this.number = number;
            this.timestamp = timestamp;
        }
    }

    private final SharedPreferences prefs;

    public RecentsStore(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<Entry> load() {
        String raw = prefs.getString(KEY, "");
        List<Entry> out = new ArrayList<>();
        if (raw.isEmpty()) return out;
        for (String line : raw.split(ENTRY_SEP)) {
            String[] parts = line.split(FIELD_SEP);
            if (parts.length != 2) continue;
            try {
                out.add(new Entry(parts[0], Long.parseLong(parts[1])));
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }

    public void add(String number) {
        if (number == null || number.isEmpty()) return;
        List<Entry> entries = load();

        Iterator<Entry> it = entries.iterator();
        while (it.hasNext()) {
            if (it.next().number.equals(number)) it.remove();
        }
        entries.add(0, new Entry(number, System.currentTimeMillis()));

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) sb.append(ENTRY_SEP);
            Entry e = entries.get(i);
            sb.append(e.number).append(FIELD_SEP).append(e.timestamp);
        }
        prefs.edit().putString(KEY, sb.toString()).apply();
    }

    public void clear() {
        prefs.edit().remove(KEY).apply();
    }
}
