package in.co.hopin.Server;

import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.Activities.SelfProfileActivity;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

public class SelfProfileResponse extends ServerResponseBase{

	String status;
	
	private static final String TAG = "in.co.hopin.Server.SelfProfileResponse";
	public SelfProfileResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
	}
	
	@Override
	public void process() {
		Logger.i(TAG,"processing SelfProfileResponse response.status:"+this.getStatus());		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Logger.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");			
			//status = body.getString("Status");			
			JSONObject selfFbInfo =  body.getJSONObject(UserAttributes.FBINFO);	
			ThisUserNew.getInstance().setUserFBInfo(new UserFBInfo(selfFbInfo));
			Intent hopinSelfProfile = new Intent(Platform.getInstance().getContext(),SelfProfileActivity.class);
			hopinSelfProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);			
	    	Platform.getInstance().getContext().startActivity(hopinSelfProfile);
	    	logSuccess();
			ProgressHandler.dismissDialoge();
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {		
			logServererror();
			ProgressHandler.dismissDialoge();
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server on user add");
			e.printStackTrace();
		}
		
	}
	
}
