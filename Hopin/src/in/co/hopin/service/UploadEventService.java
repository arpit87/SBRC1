package in.co.hopin.service;

import android.content.Intent;
import in.co.hopin.HelperClasses.Event;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.UploadEventsRequest;
import in.co.hopin.Util.Logger;

import java.util.List;

public class UploadEventService extends WakefulIntentService {
    public static final String TAG = "in.co.hopin.service.UploadEventService";

    public UploadEventService() {
        super("EventUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d(TAG, "I am here");
        List<Event> events = Event.getEvents();
        if (events.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<events.size(); i++) {
            Logger.d(TAG, events.get(i).getJsonDescription());
            sb.append(events.get(i).getJsonDescription());
            if (i != events.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        long lastTimeStamp = events.get(events.size() - 1).getTime();
        UploadEventsRequest request = new UploadEventsRequest(sb.toString(), lastTimeStamp);
        SBHttpClient.getInstance().executeRequest(request);

        super.onHandleIntent(intent);
    }
}