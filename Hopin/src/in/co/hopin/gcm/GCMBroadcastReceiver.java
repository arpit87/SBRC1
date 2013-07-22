package in.co.hopin.gcm;

import in.co.hopin.R;
import in.co.hopin.Activities.StartStrangerBuddyActivity;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.GetNewUserInfoAndShowPopupRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "in.co.hopin.gcm.GCMBroadcastReceiver";
    public static final int NOTIFICATION_ID = 1;
    public static final String TYPE = "type";
    private NotificationManager mNotificationManager;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "Received GCM message");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            sendNotification("Deleted messages on server: " +
                    intent.getExtras().toString());
        } else {
            processMessage(intent.getStringExtra("message"));
        }
        setResultCode(Activity.RESULT_OK);
    }

    private void processMessage(String message) {
        try {
        	Logger.i(TAG, "Got GCM message:"+ message);
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.getInt(TYPE) == 1) {
                showUserPopup(jsonObject);
            }
        } catch (JSONException e) {
            Logger.e(TAG, "Unable to parse GCM message", e);
        }
    }

    private void showUserPopup(JSONObject jsonObject) {
        if (!ThisAppConfig.getInstance().getBool(ThisAppConfig.NEWUSERPOPUP)) {
            return;
        }

        try {
            String userId = jsonObject.getString("user_id");
            int dailyInstaType = jsonObject.getInt("insta");
            if(!userId.equals(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID)))
            {
	            GetNewUserInfoAndShowPopupRequest req = new GetNewUserInfoAndShowPopupRequest(userId, dailyInstaType);
	            SBHttpClient.getInstance().executeRequest(req);
            }
        } catch (JSONException e) {
            Logger.e(TAG, "Unable to parse new user gcm message", e);
        }
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, StartStrangerBuddyActivity.class), 0);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.launchernew)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
