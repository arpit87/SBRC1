package in.co.hopin.Platform;

import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.ThisUserNew;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class Platform {
	
	private final String TAG = "in.co.hopin.Platform.Platform";
	private static Platform instance = new Platform();
	private Context context;	
	private Handler handler;
	private boolean ENABLE_LOGGING = false;
	public boolean SUPPORTS_NEWAPI = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
		
	private Platform() {
	}
	
	public static Platform getInstance()
	{
		return instance;
	}
	
	public boolean isLoggingEnabled() {
		return ENABLE_LOGGING;
	}	

	public Context getContext(){
		return context;
	}	
	
	public Handler getHandler(){
		return handler;
	}
	
	public void initialize(Context context) {
		this.context= context;			
		SBHttpClient.getInstance();
		handler = new Handler();
		ENABLE_LOGGING = false;
		CurrentNearbyUsers.getInstance().clearAllData();
		ThisUserNew.getInstance();
		startChatService();
	}
	
	 public void startChatService(){
	     
         Intent i = new Intent("in.co.hopin.ChatService.SBChatService");                  
         if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "Service starting" );
         context.startService(i);
        
        }
             
	
	 public void stopChatService() {		
	          Intent i = new Intent("in.co.hopin.ChatService.SBChatService");
	          context.stopService(i);         
	          
	          if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "Service stopped" );	         
	             
     }

}
