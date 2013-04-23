package in.co.hopin.ChatService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectivityMonitor extends BroadcastReceiver {
    private static final String TAG = "in.co.hopin.ChatService.ConnectivityMonitor";
    private static final int RETRY_FREQ = 1 * 60 * 1000;

    private XMPPConnectionListenersAdapter mConnectionAdapter;
    private  Timer timer;

    public ConnectivityMonitor(XMPPConnectionListenersAdapter mConnectionAdapter) {
        this.mConnectionAdapter = mConnectionAdapter;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SBConnectivity.isOnline()) {
            if (Platform.getInstance().isLoggingEnabled()) {
                Log.d(TAG, "Checking if connected");
            }
            final String login = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
            final String password = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
            if (SBConnectivity.isConnected()) {
                if (Platform.getInstance().isLoggingEnabled()) {
                    Log.d(TAG, "Connected.. trying to login");
                }
                if (!login.equals("") && !password.equals("")) {
                    mConnectionAdapter.loginAsync(login, password);
                }
            } else {
                if (Platform.getInstance().isLoggingEnabled()) {
                    Log.d(TAG, "Not connected");
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    int counter = 0;
                    @Override
                    public void run() {
                        counter++;
                        if (counter == 5){
                            if (Platform.getInstance().isLoggingEnabled()) {
                                Log.d(TAG, "Cancelling timer");
                            }
                            timer.cancel();
                            timer.purge();
                        }
                        if (Platform.getInstance().isLoggingEnabled()) {
                            Log.d(TAG, "Trying to reconnect");
                        }
                        mConnectionAdapter.loginAsync(login, password);
                    }
                }, 0, RETRY_FREQ);
            }
        }
    }
}
