package in.co.hopin.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import in.co.hopin.Platform.Platform;
import in.co.hopin.provider.EventsLoggingProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {
    private static final String TAG = "in.co.hopin.HelperClasses.EventsLogger";
    private static Uri mUriFetch = Uri.parse("content://" + EventsLoggingProvider.AUTHORITY + "/db_fetch_only");
    private static Uri mUri = Uri.parse("content://" + EventsLoggingProvider.AUTHORITY + "/events");
    private static String[] columns = new String[]{"eventJSON", "date"};

    String jsonDescription;
    long time;

    public long getTime() {
        return time;
    }

    public String getJsonDescription() {
        return jsonDescription;
    }

    public Event(String jsonDescription, long time) {
        this.jsonDescription = jsonDescription;
        this.time = time;
    }

    public static List<Event> getEvents() {
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Fetching logged events");
        List<Event> events;

        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Empty result");
            events = Collections.emptyList();
        } else {
            events = new ArrayList<Event>();
            if (cursor.moveToFirst()) {
                do {
                    Event event = new Event(cursor.getString(0), cursor.getLong(1));
                    events.add(event);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return events;
    }

    public static void addEvent(final String jsonDescription) {
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Saving event");
        final long time = System.currentTimeMillis();
        new Thread("addEvent") {
            @Override
            public void run() {
                saveEvent(jsonDescription, time);
            }
        }.start();

    }

    private static void saveEvent(String jsonDescription, long time) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[0], jsonDescription);
        contentValues.put(columns[1], time);
        cr.insert(mUri, contentValues);
    }

    public static void deleteEvent(long lastTimestamp){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, "date <= ?", new String[]{Long.toString(lastTimestamp)});
        } catch (RuntimeException e){
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error in deleting events for timestamp : " + lastTimestamp, e);
        }
    }
}
