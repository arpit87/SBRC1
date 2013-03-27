package in.co.hopin.Server;

import in.co.hopin.Activities.NewUserDialogActivity;
import in.co.hopin.Activities.OtherUserProfileActivity;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Users.UserFBInfo;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;


public class GetOtherUserProfileResponse extends ServerResponseBase{

	String status;
	
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;	
	
	
	private static final String TAG = "in.co.hopin.Server.GetOtherUserProfileResponse";
	public GetOtherUserProfileResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);		
	}
	
	@Override
	public void process() {
		//Log.i(TAG,"processing GetOtherUserProfileResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		//Log.i(TAG,"got json "+jobj.toString());
		try {
			ProgressHandler.dismissDialoge();
			body = jobj.getJSONObject("body");
			JSONObject nearbyUsersFbInfo =  body.getJSONObject(UserAttributes.FBINFO);			
			Intent i = new Intent(Platform.getInstance().getContext(),OtherUserProfileActivity.class);			
			i.putExtra("fb_info", nearbyUsersFbInfo.toString());
			Platform.getInstance().getContext().startActivity(i);
			//status = body.getString("Status");			
			//ThisUserConfig.getInstance().putBool(ThisUserConfig.FBINFOSENTTOSERVER, true);
			
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {			
			ToastTracker.showToast("Some error occured");
			//Log.e(TAG, "Error returned by server get fb info for user and show popup");
			e.printStackTrace();
		}
		
	}
	
}
