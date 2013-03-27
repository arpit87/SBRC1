package in.co.hopin.ChatService;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;
import in.co.hopin.ChatClient.ISBChatConnAndMiscListener;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.util.concurrent.atomic.AtomicBoolean;

public class XMPPConnectionListenersAdapter {
	
	private final XMPPConnection mXMPPConnection;
	private String TAG = "in.co.hopin.ChatService.XMPPConnectionListenersAdapter";
	SBChatService mService = null;
	private String mLogin;
    private String mPassword;
    private SBChatConnectionListener mConnectionListener = new SBChatConnectionListener();
    private String mErrorMsg = "";
    public AtomicBoolean tryinLogging = new AtomicBoolean(false); 
    public AtomicBoolean tryinConnecting = new AtomicBoolean(false); 
	private ConnectToChatServerTask connectToServer = null;
	private LoginToChatServerTask loginToServer = null;
	private SBChatManager mChatManager = null;
	private Handler handler = new Handler();
	private final RemoteCallbackList<ISBChatConnAndMiscListener> mRemoteMiscListeners = new RemoteCallbackList<ISBChatConnAndMiscListener>();
	
	
 public XMPPConnectionListenersAdapter(final ConnectionConfiguration config,  final SBChatService service) {
		this(new XMPPConnection(config), service);
	    }
	
 public void addMiscCallBackListener(ISBChatConnAndMiscListener listener) throws RemoteException {
	if (listener != null)
		mRemoteMiscListeners.register(listener);
 }
	
public void removeMiscCallBackListener(ISBChatConnAndMiscListener listener) throws RemoteException {
	if (listener != null)
		mRemoteMiscListeners.unregister(listener);
 }


	 public XMPPConnectionListenersAdapter(final XMPPConnection con,
			     final SBChatService service) {
		 mXMPPConnection = con;

		mLogin = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);		
		mPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);		
		mService = service;	
		mChatManager = new SBChatManager(mXMPPConnection, mService);
		//Log.d(TAG, "xmpp connection listener will connect");
		loginAsync(mLogin, mPassword);
		Toast.makeText(mService, "connecting to xmpp", Toast.LENGTH_SHORT).show();
		//Log.d(TAG, "connecting to xmpp");
	
	 }
	 
	 
	 public SBChatManager getChatManager() {
			return mChatManager;
		}
	 
	 	 
	 public XMPPConnection getmXMPPConnection() {
		return mXMPPConnection;
	}


	public boolean connect() throws RemoteException {
		if (mXMPPConnection.isConnected())
			return true;
		else {
		    try {
		    	if(SBConnectivity.isConnected())
		    	{
		    		mXMPPConnection.connect();
		    		mXMPPConnection.addConnectionListener(mConnectionListener);
		    	}
		    	else
		    		ToastTracker.showToast("Not connected to internet");
		    } catch (XMPPException e) {
			//Log.e(TAG, "Error while connecting", e);
			mErrorMsg = e.getMessage();
			return false;
		    }  
		    return true;
		}		    
	    }

	
	public boolean isAuthenticated()
	{
		if (mXMPPConnection.isAuthenticated())		
			return true;
		else
			return false;
	}
	
	public boolean disconnect() {
		if (mXMPPConnection != null && mXMPPConnection.isConnected())
			mXMPPConnection.disconnect();
		return true;
	    }
	
	
	public void loginAsync(String login,String password)
	{		
		
		mLogin = login;
		mPassword = password;
		//Log.d(TAG, "login async called");
		if(!mXMPPConnection.isConnected())
		{
			if(tryinConnecting.getAndSet(true))
				return;
			//Log.d(TAG, "xmpp isconnected is false and none tryin to connect so i will");
			connectToServer = new ConnectToChatServerTask();
			connectToServer.execute(this);
			
		}
		else if(!mXMPPConnection.isAuthenticated())
		{	
			if(tryinLogging.getAndSet(true))
				return;
			//Log.d(TAG, "xmpp isAuthenticated is false and none tryin to login so i will");
			loginToServer = new LoginToChatServerTask();
			loginToServer.execute(this);
		}
		else if(mChatManager!=null)				
			mChatManager.notifyAllPendingQueue(); ///means we already logged in so send the msgs
			
	}
	
	//this should be called in separate thread
	    private boolean login() throws RemoteException {
	    	//Log.d(TAG, "login called ");
	    if(mLogin == "" || mPassword == "")
	    		return false;	    
		if (mXMPPConnection.isAuthenticated())
		{
			//Log.d(TAG, "login called and is already authenticated");
			return true;
		}
	    	//ToastTracker.showToast("tryin login is not authenticated", Toast.LENGTH_SHORT);
		if (!mXMPPConnection.isConnected())
		{			
			//Log.d(TAG, "tryin login but xmpp not connected,ll return false");
			return false; //blocking
		}		
		try {
			//Log.d(TAG, "login called and willl login");
			mXMPPConnection.login(mLogin, mPassword);		    
		} catch (XMPPException e) {
		    //Log.e(TAG, "Error while log in", e);
		    mErrorMsg = "Error while log in";
		    return false;
		}catch (IllegalStateException e) {
		    //Log.i(TAG, "Already logged in", e);		    
		    return true;
		}
		return true;
	    }
	    
	    public String getErrorMessage() {
	    	return mErrorMsg;
	        }
	 
	
private class SBChatConnectionListener implements ConnectionListener {
		
		@Override
		public void connectionClosed() {
		    //Log.d(TAG, "closing connection,stopping service");
		    //ToastTracker.showToast("xmpp connection closed,should reconnect");	    
		    
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void connectionClosedOnError(Exception exception) {
		    //Log.d(TAG, "connectionClosedOnError,should try reconnect");
		    //ToastTracker.showToast("Chat server connection closed on error,should try reconnect");		   
		    //Intent intent = new Intent(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED);
		    //intent.putExtra("message", exception.getMessage());
		    //mService.sendBroadcast(intent);
		    //mService.stopSelf();
		}

		/**
		 * Connection failed callback.
		 * @param errorMsg smack failure message
		 */
		public void connectionFailed(String errorMsg) {
		    //Log.d(TAG, "Connection Failed");
		    //ToastTracker.showToast("xmpp connection failed");		   
		  /*  final int n = mRemoteConnListeners.beginBroadcast();

		    for (int i = 0; i < n; i++) {
			IBeemConnectionListener listener = mRemoteConnListeners.getBroadcastItem(i);
			try {
			    if (listener != null)
				listener.connectionFailed(errorMsg);
			} catch (RemoteException e) {
			    // The RemoteCallbackList will take care of removing the
			    // dead listeners.
			    //Log.w(TAG, "Error while triggering remote connection listeners", e);
			}
		    }
		    mRemoteConnListeners.finishBroadcast();*/
		   // mService.stopSelf();
		    
		}
		    @Override
			public void reconnectingIn(int paramInt) {
		    	//Log.d(TAG, "reconnectingIn"+paramInt);
		    	 //ToastTracker.showToast("xmpp reconnecing in:"+paramInt);
				
			}

			@Override
			public void reconnectionSuccessful() {
				//Log.d(TAG, "reconnection success");				
				//ToastTracker.showToast("xmpp reconnection successful");
				
			}

			@Override
			public void reconnectionFailed(Exception paramException) {
				//Log.d(TAG, "reconnectionFailed Failed");
				
			}
		}

private class ConnectToChatServerTask extends AsyncTask<XMPPConnectionListenersAdapter, Integer, Boolean>
{
	XMPPConnectionListenersAdapter adapter;
	@Override
	protected Boolean doInBackground(XMPPConnectionListenersAdapter... connection) {
		boolean result = true;	
		adapter = connection[0];
		//Log.d(TAG, "connecting on separate thread");
		try {
		    publishProgress(25);			    
		    if (!adapter.connect()) {				
			return false;
		    }		    
		    publishProgress(100);			    
		   
		} catch (RemoteException e) {			    
		    result = false;
		}
		return result;
	}	
	
	protected void onPostExecute(Boolean connected) {
		tryinConnecting.set(false);
	if(connected)
	{		
		if(mLogin != "" && mPassword != ""){
			if(tryinLogging.getAndSet(true))
				return;
			loginToServer = new LoginToChatServerTask();
			loginToServer.execute(adapter);
			//ToastTracker.showToast("connected to xmpp,logging");
			//Log.d(TAG, "connected to xmpp,logging");
		}
		else
		{
			//Log.d(TAG, "connected to xmpp,but not logging");
			//ToastTracker.showToast("connected to xmpp but not logging");
			tryinLogging.set(false);
		}
		
	}
	}

	
}

private class LoginToChatServerTask extends AsyncTask<XMPPConnectionListenersAdapter, Integer, Boolean>
{
	
	@Override
	protected Boolean doInBackground(XMPPConnectionListenersAdapter... connection) {
		boolean result = true;	
		XMPPConnectionListenersAdapter adapter = connection[0];	
		//Log.d(TAG, "logging on separate thread");
		   try{ 
		    if (!adapter.login()) {				
			publishProgress(25);
			return false;
		    }
		    //ToastTracker.showToast("logged in to xmpp");
		    publishProgress(100);
		} catch (RemoteException e) {			    
		    result = false;
		}
		return result;
	}	
	
	protected void onPostExecute(Boolean connected) {
			tryinLogging.set(false);
			if(!connected)
			{
				//Log.d(TAG, "in login post exe but is not connected");
				//TODO try relogin here for 2,3 times
				//Toast.makeText(mService, "logged failed in postexecute,may be user not yet fb logged in,its ok", Toast.LENGTH_SHORT).show();
				return;
			}
			//Log.d(TAG, "logged in to xmpp");
			//ToastTracker.showToast("logged in  to xmpp", Toast.LENGTH_SHORT);	
			if(mChatManager!=null)				
				mChatManager.notifyAllPendingQueue();
		 	Runnable sendPresence = new Runnable() {
				
				@Override
				public void run() {
					try{
						//Log.d(TAG, "sending presence packet");
						Presence presence = new Presence(Presence.Type.available);
					 	mXMPPConnection.sendPacket(presence);			
					}catch (IllegalStateException e) {
					    //Log.e(TAG, "Problem sending presence packet", e);
					}
				}
			};
			sendPresence.run();
		 	
		 	int n = mRemoteMiscListeners.beginBroadcast();

			for (int i = 0; i < n; i++) {
				ISBChatConnAndMiscListener listener = mRemoteMiscListeners.getBroadcastItem(i);
			    try {
					listener.loggedIn();
					//??can remove before finishbroadcast?
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mRemoteMiscListeners.finishBroadcast();		
			
			
		}

	
}


	

}
