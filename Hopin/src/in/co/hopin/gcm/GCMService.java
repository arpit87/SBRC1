package in.co.hopin.gcm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HttpClient.RegisterGCMIdRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import static in.co.hopin.HelperClasses.ThisAppConfig.*;

public class GCMService extends Service {
    private static final String TAG = "in.co.hopin.gcm.GCMService";

    public static final String EXTRA_MESSAGE = "message";
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    private static final String SENDER_ID = "348316953611";

    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private String regId;
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        regId = getRegistrationId(Platform.getInstance().getContext());
        Logger.i(TAG, "Registration Id:" + regId);
        if (regId.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    private String getRegistrationId(Context context) {
        ThisAppConfig thisAppConfig = ThisAppConfig.getInstance();
        String registrationId = thisAppConfig.getString(PROPERTY_REG_ID);

        if (registrationId.length() == 0) {
            Logger.v(TAG, "Registration not found.");
            return "";
        }

        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = thisAppConfig.getInt(ThisAppConfig.PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Logger.v(TAG, "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean isRegistrationExpired() {
        long expirationTime = ThisAppConfig.getInstance().getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {
        final Context context = Platform.getInstance().getContext();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regId;

                    setRegistrationId(context, regId);
                    SBHttpClient.getInstance().executeRequest(new RegisterGCMIdRequest());
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void setRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Logger.v(TAG, "Saving regId on app version " + appVersion);
        ThisAppConfig thisAppConfig = ThisAppConfig.getInstance();
        thisAppConfig.putString(PROPERTY_REG_ID, regId);
        thisAppConfig.putInt(PROPERTY_APP_VERSION, appVersion);

        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
        Logger.v(TAG, "Setting registration expiry time to " + new Timestamp(expirationTime));
        thisAppConfig.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
    }

    private void sendMessage() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Logger.d(TAG, "Sending message to GCM");
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("hello", "World");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.d(TAG, msg);
            }
        }.execute(null, null, null);
    }
}


