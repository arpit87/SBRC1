package in.co.hopin.FacebookHelpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.analytics.tracking.android.EasyTracker;

import in.co.hopin.Activities.FBLoggableFragmentActivity;
import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.*;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class FacebookConnector {
	
	private static final String TAG = "in.co.hopin.FacebookHelpers.FacebookConnector";
	
	public static String [] FB_PERMISSIONS = {"user_about_me","user_education_history","user_hometown","user_work_history","email"};
	public static String FB_APP_ID = "107927182711315";	
	//public static String FB_APP_ID = "486912421326659"; //debug one
	
	private static FacebookConnector fbconnect = null;
	public static Facebook facebook = new Facebook(FB_APP_ID);
	public static AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    private String [] permissions ;
    FBLoggableFragmentActivity underlyingActivity = null;
    
    
    private FacebookConnector()
    {
    	permissions = FB_PERMISSIONS;
    }
    
    private void setActivity(FBLoggableFragmentActivity underlying_activity)
    {
    	underlyingActivity =  underlying_activity; 
    }
    
    public static FacebookConnector getInstance(FBLoggableFragmentActivity underlying_activity)
    {    	 	
        EasyTracker.getInstance().setContext(Platform.getInstance().getContext());
    	if(fbconnect == null)
    		fbconnect = new FacebookConnector();
    	fbconnect.setActivity(underlying_activity); 
    	return fbconnect;
    }
    
    public void logoutFromFB()
    { 
    	String access_token = ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN);
	    long expires = ThisUserConfig.getInstance().getLong(ThisUserConfig.FBACCESSEXPIRES);
	 
	    if (access_token != "") {
	        facebook.setAccessToken(access_token);
	    }
	 
	    if (expires != -1 && expires != 0) {
	        facebook.setAccessExpires(expires);
	    }
		if (facebook.isSessionValid()) {
			ProgressHandler.showInfiniteProgressDialoge(underlyingActivity,"Logging out from facebook", "Wait a moment");
		    AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
		    asyncRunner.logout(underlyingActivity.getBaseContext(), new LogoutRequestListener());		    
		} 
		else
		{		
			ToastTracker.showToast("Fb session already expired");
		}
    }
    
    public static boolean isSessionValid()
    {
    	String access_token = ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN);
 	    long expires = ThisUserConfig.getInstance().getLong(ThisUserConfig.FBACCESSEXPIRES);
 	   //access_token = "BAABiKMFjGhMBAJaZBFz0G9ZCHmAOw5OF2mogxOsNFaRM3ZCZC9ZCY4waMCq39OSoy55CCBfEqWhQf4PxTG4mNzG7cPWVMzK9cvdGwsnx6WnnfGZBzbZCNcA2ICY7KZBmnRcZD";
 	   //expires = 1374324192176L;
 	  //ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES,expires);
 	    if (access_token != "") {
 	        facebook.setAccessToken(access_token);
 	    }
 	 
 	    if (expires != 0 &&  expires != -1) {
 	        facebook.setAccessExpires(expires);
 	    }
     	
     	 if (facebook.isSessionValid())      		 
     		 return true;
     	 else
     		 return false;
     	 
    }
    
    public void loginToFB()
    { 	
    	//facebook.authorize(underlying_activity, permissions, new LoginDialogListener());
    	Logger.i(TAG, "login called");
	    String access_token = ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN);
	    long expires = ThisUserConfig.getInstance().getLong(ThisUserConfig.FBACCESSEXPIRES);
	 
	    if (access_token != "") {
	        facebook.setAccessToken(access_token);
	    }
	 
	    if (expires != 0 &&  expires != -1) {
	        facebook.setAccessExpires(expires);
	    }
    	
    	 if (!facebook.isSessionValid()) {
    		 ProgressHandler.dismissDialoge();
    		 Logger.i(TAG, "login called,session in valid , trying login");
    		 facebook.authorize(underlyingActivity, permissions, new LoginDialogListener());
    	 }else
    		 ToastTracker.showToast("Already logged in");
    }  
    
    public void reloginToFB()
    { 	
    	//facebook.authorize(underlying_activity, permissions, new LoginDialogListener());
    	
	    String access_token = ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN);
	    long expires = ThisUserConfig.getInstance().getLong(ThisUserConfig.FBACCESSEXPIRES);
	 
	    if (access_token != "") {
	        facebook.setAccessToken(access_token);
	    }
	 
	    if (expires != 0 &&  expires != -1) {
	        facebook.setAccessExpires(expires);
	    }
    	
    	 if (!facebook.isSessionValid()) {
    		 ProgressHandler.dismissDialoge();
    		 facebook.authorize(underlyingActivity, permissions, new ReLoginDialogListener());
    	 }else
    		 ToastTracker.showToast("Already logged in");
    } 
    
    public void authorizeCallback(int requestCode, int resultCode,Intent data)
    {
    	facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    class ReLoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	EasyTracker.getTracker().sendEvent("callback_event", "callback_received", "FBReLogin_callback", 1L);
	    	String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
	    	ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, facebook.getAccessToken());
        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, facebook.getAccessExpires());
	    	SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID),fbid , ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
			SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);
			ThisUserConfig.getInstance().putBool(ThisUserConfig.FBRELOGINREQUIRED, false);
			Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);
	        showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        Platform.getInstance().getContext().startActivity(showSBMapViewActivity);
	        ToastTracker.showToast("Authentication succcessful");
	        underlyingActivity.finish();
	    }

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
    }
    
    class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
            EasyTracker.getTracker().sendEvent("callback_event", "callback_received", "FBLogin_callback", 1L);
	    	ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, facebook.getAccessToken());
        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, facebook.getAccessExpires());
        	Logger.i(TAG, "login callback rec");
        	ProgressHandler.showInfiniteProgressDialoge(underlyingActivity, "Authentication successsful", "Please wait..");        	
        	requestUserData();        	
        }    
	    

		public void onFacebookError(FacebookError error) {
	    	ToastTracker.showToast("Authentication with Facebook failed!");
	    	
	    }
	    public void onError(DialogError error) {
	    	ToastTracker.showToast("Authentication with Facebook failed!");
	    	
	    }
	    public void onCancel() {
	    	ToastTracker.showToast("Authentication with Facebook cancelled!");
	    	
	    }
	}

    private void sendAddFBAndChatInfoToServer(String fbid) {
    	//this should only be called from fbpostloginlistener to ensure we have fbid
    	if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"in sendAddFBAndChatInfoToServer");
    	SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(fbid);
     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);
		SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), fbid, ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
		SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);			
	}
		
	private void requestUserData() {
       
        Bundle params = new Bundle();
        params.putString("fields", "username,first_name,last_name, picture, email, gender");
        mAsyncRunner.request("me", params, new FBUserRequestListener());
    }
	
	/*
	 * Callback for fetching current user's name, picture, uid.
	 */

	public class FBUserRequestListener extends FBBaseRequestListener {	     
	    public void onComplete(final String response, final Object state) {
	        JSONObject jsonObject;
	        try {
	            jsonObject = new JSONObject(response);	  
	            if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got my fbinfo:"+jsonObject.toString());
	            String picurl,username,first_name,last_name,id,gender,email;
	            id = jsonObject.getString("id");
	            username = jsonObject.getString("username");
	            first_name  = jsonObject.getString("first_name");
	            last_name = jsonObject.getString("last_name");
	            gender = jsonObject.getString("gender");
	            email = jsonObject.getString("email");
	            picurl = "http://graph.facebook.com/" + id + "/picture?type=small";
	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBUID,id );
	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBPICURL, picurl);
	            ThisUserConfig.getInstance().putString(ThisUserConfig.FBUSERNAME, username);
	            ThisUserConfig.getInstance().putString(ThisUserConfig.GENDER, gender);
	            ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FIRSTNAME, first_name);
	            ThisUserConfig.getInstance().putString(ThisUserConfig.FB_LASTNAME, last_name);
                ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, first_name+" "+last_name);
                ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FULLNAME, first_name+" "+last_name);
                ThisUserConfig.getInstance().putString(ThisUserConfig.EMAIL, email);
                if(!StringUtils.isBlank(id))
                {
                	ThisUserConfig.getInstance().putBool(ThisUserConfig.FBLOGGEDIN, true);                	
                	String userId = ThisUserConfig.getInstance().getString(ThisUserConfig.USERID);
                	if(userId == "")
                	{
                		//this happens on fb login from tutorial page.
                		ProgressHandler.showInfiniteProgressDialoge(underlyingActivity, "Welcome "+first_name+" "+last_name+"!", "Preparing for first run..");
                		String uuid = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
                		SBHttpRequest request = new AddUserRequest(uuid,username,underlyingActivity);		
                  		SBHttpClient.getInstance().executeRequest(request);
                	}
                	else
                	{
                		sendAddFBAndChatInfoToServer(id);
                		Platform.getInstance().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                            	ProgressHandler.dismissDialoge();
                            	Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);
                    	        showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    	        Platform.getInstance().getContext().startActivity(showSBMapViewActivity);                    	                          	        
                                MapListActivityHandler.getInstance().updateUserNameInListView();
                                MapListActivityHandler.getInstance().updateUserPicInListView();
                                MapListActivityHandler.getInstance().updateThisUserMapOverlay();
                                if(!underlyingActivity.equals(MapListActivityHandler.getInstance().getUnderlyingActivity()))
                    	        	underlyingActivity.finish();
                            }
                        });
                	}
                }
	            //id getting delayed in writing to file and not getting picked in call to server so pass as argument	           
	            
	            	           
	            //Bitmap bmp = FBUtility.getBitmap(ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL));
	            //Store.getInstance().saveBitmapToFile(bmp,ThisUserConfig.FBPICFILENAME);
                
	        } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	}
	
	class LogoutRequestListener implements RequestListener {
		  public void onComplete(String response, Object state) {
			  //remove all fb info of this user	
			  
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, "");
			  ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES,-1);
			  ThisUserConfig.getInstance().putBool(ThisUserConfig.FBLOGGEDIN,false);			  
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FBPICURL, "");
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FBUSERNAME, "");
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FB_FIRSTNAME, "");
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FB_LASTNAME, "");
			  ThisUserConfig.getInstance().putString(ThisUserConfig.FBUID, "");	
			  
			  //erase chat info too
			  ThisUserConfig.getInstance().putString(ThisUserConfig.CHATUSERID,"");
			  ThisUserConfig.getInstance().putString(ThisUserConfig.CHATPASSWORD,"");			  
			  ProgressHandler.dismissDialoge();
			  
			  //refresh user pic to silhutte
			  Platform.getInstance().getHandler().post(new Runnable(){

				@Override
				public void run() {
					 //MapListActivityHandler.getInstance().updateThisUserMapOverlay();
					 //MapListActivityHandler.getInstance().updateUserPicInListView();
					underlyingActivity.finish();
					 ToastTracker.showToast("Successfully logged out");					
				}			 		
			  });
			 
		  }
		  
		  public void onIOException(IOException e, Object state) {}
		  
		  public void onFileNotFoundException(FileNotFoundException e,
		        Object state) {}
		  
		  public void onMalformedURLException(MalformedURLException e,
		        Object state) {}
		  
		  public void onFacebookError(FacebookError e, Object state) {}
		}
	
	public void openFacebookPage(String fbid,String username) {
		Intent i;
		   try {
			   underlyingActivity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
		     i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+fbid));
		   } catch (Exception e) {
			   //if fb package not present then shows in browser
		    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+username));
		   }
		   i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		 			| Intent.FLAG_ACTIVITY_NEW_TASK);
		   underlyingActivity.startActivity(i);
		}	
    
}
	


