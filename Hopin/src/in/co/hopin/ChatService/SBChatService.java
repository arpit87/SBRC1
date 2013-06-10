package in.co.hopin.ChatService;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import in.co.hopin.ChatClient.ChatWindow;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Util.Logger;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SBChatService extends Service {

    private static String TAG = "in.co.hopin.ChatService.SBChatService";
    private static final String INTENT_ACTION = "in.co.hopin.ChatService.ConnectivityMonitor";
    private static final int POLL_FREQ = 2 * 60 * 1000;
    private XMPPConnection mXMPPConnection = null;
    NotificationManager mNotificationManager = null;
    private ConnectionConfiguration mConnectionConfiguration = null;
    private XMPPConnectionListenersAdapter mConnectionAdapter;
    private XMPPAPIs mXMPPAPIs = null;
    private int DEFAULT_XMPP_PORT = 5222;
    int mPort;
    private SBChatBroadcastReceiver mReceiver = new SBChatBroadcastReceiver();
    private String mHost = ServerConstants.CHATSERVERIP;
    String mErrorMsg = "";
    public static boolean isRunning = false;
    private Timer timer;
    private PingManager mPingManager;
    private ChatManager chatManager;

    /**
     * Broadcast intent type.
     */
    public static final String SBCHAT_CONNECTION_CLOSED = "SBConnectionClosed";
    public static final String SBLOGIN_TO_CHAT = "SBLoginToChatServer";


    @Override
    public void onCreate() {
        super.onCreate();
        if (isRunning) {
            //Toast.makeText(this, "Already running ChatService", Toast.LENGTH_SHORT);
            if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Already running ChatService");
            return;
        }
        if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Started ChatService");
        //Toast.makeText(this, "started service", Toast.LENGTH_SHORT).show();
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(mReceiver, new IntentFilter(SBLOGIN_TO_CHAT));
        registerReceiver(mReceiver, new IntentFilter(BroadCastConstants.NEARBY_USER_UPDATED));
        mPort = DEFAULT_XMPP_PORT;

        initializeConfigration();
        SASLAuthentication.supportSASLMechanism("PLAIN");
        mXMPPConnection = new XMPPConnection(mConnectionConfiguration);

        if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "made xmpp connection");

        ServiceDiscoveryManager.setIdentityName("Hopin");
        ServiceDiscoveryManager.setIdentityType("Bot");

        //service has connection adapter which has all listeners
        mConnectionAdapter = new XMPPConnectionListenersAdapter(mXMPPConnection, this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);

        mXMPPAPIs = new XMPPAPIs(mConnectionAdapter);
        if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Created ChatService");
        isRunning = true;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Platform.getInstance().isLoggingEnabled())
            Log.i("LocalService", "Received start id " + startId + ": " + intent);
        //ToastTracker.showToast("service strted with id:"+startId);

        String login = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
        String password = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
        if (!login.equals("") && !password.equals(""))
            mConnectionAdapter.loginAsync(login, password);

        timer = new Timer();
        timer.scheduleAtFixedRate(new ConnectionMonitorTask(), POLL_FREQ, POLL_FREQ);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ToastTracker.showToast("stopping service and xmpp disconnecting");
        isRunning = false;
        mNotificationManager.cancelAll();
        unregisterReceiver(mReceiver);
        if (mConnectionAdapter.isAuthenticated() && SBConnectivity.isConnected())
            mConnectionAdapter.disconnect();

        Intent connectivityMonitorIntent = new Intent(INTENT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                connectivityMonitorIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Stopping the service");
    }

    private void initializeConfigration() {
        mConnectionConfiguration = new ConnectionConfiguration(mHost, mPort);
        mConnectionConfiguration.setReconnectionAllowed(true);
        mConnectionConfiguration.setDebuggerEnabled(false);
        mConnectionConfiguration.setSendPresence(true);
        mConnectionConfiguration.setRosterLoadedAtLogin(false);
        SmackConfiguration.setPacketReplyTimeout(10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mConnectionConfiguration.setTruststoreType("AndroidCAStore");
            mConnectionConfiguration.setTruststorePassword(null);
            mConnectionConfiguration.setTruststorePath(null);
        } else {
            mConnectionConfiguration.setTruststoreType("BKS");
            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") + File.separator + "etc"
                        + File.separator + "security" + File.separator
                        + "cacerts.bks";
            mConnectionConfiguration.setTruststorePath(path);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "ONBIND()");
        return mXMPPAPIs;
    }


    public void sendNotification(int id, String participant, String participant_name, String chatMessage) {

        Intent chatIntent = new Intent(this, ChatWindow.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatIntent.putExtra(ChatWindow.PARTICIPANT, participant);
        chatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, participant_name);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Sending notification");
        PendingIntent pintent = PendingIntent.getActivity(this, id, chatIntent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notif = new Notification(R.drawable.launchernew, "New message from " + participant_name, System.currentTimeMillis());
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.setLatestEventInfo(this, participant_name, chatMessage, pintent);
        /*
        .setContentText("New message from:"+participant)
        .setSmallIcon(R.drawable.chat_horn3)
        .setAutoCancel(true)
        .setContentIntent(pintent)
        .build();*/

        notif.ledARGB = 0xff0000ff; // Blue color
        notif.ledOnMS = 1000;
        notif.ledOffMS = 1000;
        notif.defaults |= Notification.DEFAULT_LIGHTS;
        notif.sound = sound_uri;

        mNotificationManager.notify(id, notif);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "notification sent");
    }

    public void deleteNotification(int id) {
        mNotificationManager.cancel(id);
    }


    class SBChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String intentAction = intent.getAction();
            
            if (intentAction.equals(SBCHAT_CONNECTION_CLOSED)) {
                CharSequence message = intent.getCharSequenceExtra("message");
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if (context instanceof Activity) {
                    Activity act = (Activity) context;
                    act.finish();
                    // The service will be unbinded in the destroy of the activity.
                }
            } else if (intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "connectivity changed");
                if (!SBConnectivity.isOnline()) {
                    if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Connectivity Lost");
                } else {
                    //network came up again
                    //ToastTracker.showToast("NEtwork up yippe,ll login",  Toast.LENGTH_SHORT);
                    String login = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
                    String password = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
                    mConnectionAdapter.loginAsync(login, password);
                }
            } else if (intentAction.equals(SBLOGIN_TO_CHAT)) {
                String login = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                mConnectionAdapter.loginAsync(login, password);
            } else if (intentAction.equals(BroadCastConstants.NEARBY_USER_UPDATED)) {
                if (Platform.getInstance().isLoggingEnabled())
                    Log.i(TAG, "update intent in chat rece ,might broadcast");
                //send broad chat msg to all fb loggeged in nearby users
                if (!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
                    return;

                SBChatManager chatManager = mConnectionAdapter.getChatManager();

                if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Starting broadcast");
                List<NearbyUser> nearbyUserList = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
                if (nearbyUserList != null)
                    for (NearbyUser n : nearbyUserList) {
                        String fbid = n.getUserFBInfo().getFbid();
                        if (!fbid.equals(""))
                            try {
                                Message msg = new Message(fbid, Message.MSG_TYPE_NEWUSER_BROADCAST);
                                if (chatManager != null) {
                                    if (Platform.getInstance().isLoggingEnabled())
                                        Log.i(TAG, "broadcasting to fbid:" + fbid);
                                    chatManager.getChat(fbid).sendMessage(msg);
                                }
                            } catch (RemoteException e) {
                                if (Platform.getInstance().isLoggingEnabled())
                                    Log.i(TAG, "Unable to send broadcast msg");
                                e.printStackTrace();
                            }
                    }

            }
        }
    }

    class ConnectionMonitorTask extends TimerTask {

        public void run() {
            Logger.i(TAG, "ConnectivityMonitor task resumed");
            //ToastTracker.showToast("ConnectivityMonitor task resumed");
            if (mPingManager == null) {
                if (ServiceDiscoveryManager.getInstanceFor(mXMPPConnection) != null) {
                    mPingManager = PingManager.getInstanceFor(mXMPPConnection);
                } else {
                    //Logger.d(TAG, "No service discovery manager found");
                    return;
                }
            }

            boolean isServerReachable = mPingManager.pingMyServer();
            Logger.i(TAG, "Is server reachable? " + isServerReachable);

            if (isServerReachable) {
                //ToastTracker.showToast("Connected. Trying to login");
                login();
            } else {
                Logger.d(TAG, "Server not reachable.");
                //ToastTracker.showToast("Server not reachable.");
                mConnectionAdapter.setWasConnectionLost(true);
                login();
            }
        }
        
        public void login() {
            final String login = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
            final String password = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
            if (!login.equals("") && !password.equals("")) {
                mConnectionAdapter.loginAsync(login, password);
            }

            ChatManager tempChatManager = mXMPPConnection.getChatManager();
            if (tempChatManager != chatManager && tempChatManager != null) {
                mConnectionAdapter.resetOnConnection();
            }
            chatManager = tempChatManager;
        }
    }
}
