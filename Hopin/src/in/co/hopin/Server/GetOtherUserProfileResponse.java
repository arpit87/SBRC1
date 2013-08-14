package in.co.hopin.Server;

import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.HopinTracker;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;


public class GetOtherUserProfileResponse extends ServerResponseBase{

	String status;
	
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;	
	
	
	private static final String TAG = "in.co.hopin.Server.GetOtherUserProfileResponse";
	public GetOtherUserProfileResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);		
	}
	
	@Override
	public void process() {
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing GetOtherUserProfileResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());
		try {
			ProgressHandler.dismissDialoge();
			body = jobj.getJSONObject("body");
			JSONObject nearbyUsersFbInfo =  body.getJSONObject(UserAttributes.FBINFO);			
			Intent hopinNewProfile = new Intent(Platform.getInstance().getContext(),OtherUserProfileActivityNew.class);
	    	hopinNewProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	hopinNewProfile.putExtra("fb_info", nearbyUsersFbInfo.toString());
	    	Platform.getInstance().getContext().startActivity(hopinNewProfile);
	    	logSuccess();
		} catch (JSONException e) {		
			logServererror();
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Some error occured");
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server get fb info for user and show popup");
			e.printStackTrace();
		}
		
	}
	
}
