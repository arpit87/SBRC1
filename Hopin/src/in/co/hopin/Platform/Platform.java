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
	public boolean SUPPORTS_NEWAPI = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
		
	private Platform() {
	}
	
	public static Platform getInstance()
	{
		return instance;
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
		CurrentNearbyUsers.getInstance().clearAllData();
		ThisUserNew.getInstance();
		startChatService();
	}
	
	 public void startChatService(){
	     
         Intent i = new Intent("in.co.hopin.ChatService.SBChatService");                  
         //Log.d( TAG, "Service starting" );
         context.startService(i);
        
        }
             
	
	 public void stopChatService() {		
	          Intent i = new Intent("in.co.hopin.ChatService.SBChatService");
	          context.stopService(i);         
	          
	          //Log.d( TAG, "Service stopped" );	         
	             
     }

}
