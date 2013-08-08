package in.co.hopin.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import in.co.hopin.Activities.StartStrangerBuddyActivity;

public class OnBootReceiver extends BroadcastReceiver {
    private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BOOT_COMPLETED_ACTION)){
            Intent newIntent =  new Intent(context, OnAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, 0, StartStrangerBuddyActivity.UPLOAD_FREQUENCY, pendingIntent);

            WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
            context.startService(new Intent(context, UploadEventService.class)); //start UploadEventsService
        }
    }
}
