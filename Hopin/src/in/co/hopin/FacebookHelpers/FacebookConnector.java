package in.co.hopin.FacebookHelpers;

import in.co.hopin.Activities.Tutorial;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.Store;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.ChatServiceCreateUser;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SaveFBInfoRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookConnector {
	
	private static final String TAG = "in.co.hopin.FacebookHelpers.FacebookConnector";
	
	public static String [] FB_PERMISSIONS = {"user_about_me","user_education_history","user_hometown","user_work_history","email"};
	public static String FB_APP_ID = "107927182711315";
	
	
	public static Facebook facebook = new Facebook(FB_APP_ID);
	public static AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    private String [] permissions ;
    Activity underlying_activity = null;
    

    
    public FacebookConnector(Activity underlying_activity)
    {
    	this.underlying_activity =  underlying_activity;
    	this.permissions = FB_PERMISSIONS;
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
			ProgressHandler.showInfiniteProgressDialoge(underlying_activity,"Logging out from facebook", "Wait a moment");
		    AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
		    asyncRunner.logout(underlying_activity.getBaseContext(), new LogoutRequestListener());		    
		} 
		else
			ToastTracker.showToast("Fb session already expired");
    }
    
    public void loginToFB()
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
    		 facebook.authorize(underlying_activity, permissions, new LoginDialogListener());  
    		 
    	 }
    }  
    
    public void authorizeCallback(int requestCode, int resultCode,Intent data)
    {
    	facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {	    	
	    	ThisUserConfig.getInstance().putString(ThisUserConfig.FBACCESSTOKEN, facebook.getAccessToken());
        	ThisUserConfig.getInstance().putLong(ThisUserConfig.FBACCESSEXPIRES, facebook.getAccessExpires());
        	ProgressHandler.showInfiniteProgressDialoge(underlying_activity, "Authentication successsful", "Please wait..");
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
    	//Log.i(TAG,"in sendAddFBAndChatInfoToServer");
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
	            //Log.i(TAG,"got my fbinfo:"+jsonObject.toString());
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
                		ProgressHandler.showInfiniteProgressDialoge(underlying_activity, "Welcome "+first_name+" "+last_name+"!", "Preparing for first run..");
                		String uuid = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
                		SBHttpRequest request = new AddUserRequest(uuid,username,underlying_activity);		
                  		SBHttpClient.getInstance().executeRequest(request);
                	}
                	else
                	{
                		sendAddFBAndChatInfoToServer(id);
                		Platform.getInstance().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                MapListActivityHandler.getInstance().updateUserNameInListView();
                                MapListActivityHandler.getInstance().updateUserPicInListView();
                                MapListActivityHandler.getInstance().updateThisUserMapOverlay();
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
					 MapListActivityHandler.getInstance().updateThisUserMapOverlay();
					 MapListActivityHandler.getInstance().updateUserPicInListView();
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
			   underlying_activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
		     i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+fbid));
		   } catch (Exception e) {
			   //if fb package not present then shows in browser
		    i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+username));
		   }
		   i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		 			| Intent.FLAG_ACTIVITY_NEW_TASK);
		   underlying_activity.startActivity(i);
		}
    
}
	


