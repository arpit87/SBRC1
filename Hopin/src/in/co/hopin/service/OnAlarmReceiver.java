package in.co.hopin.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
        context.startService(new Intent(context, UploadEventService.class)); //start UploadEventService
    }
}
