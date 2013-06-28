package in.co.hopin.Server;

import android.content.Intent;
import android.util.Log;
import in.co.hopin.Activities.NewUserDialogActivity;
import in.co.hopin.HelperClasses.BlockedUser;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserFBInfo;

import in.co.hopin.Util.Logger;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class GetNewUserInfoAndShowPopup extends ServerResponseBase{

	String status;
	
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;	
	int daily_insta_type;
	
	
	private static final String TAG = "in.co.hopin.Server.GetFBInfoResponseAndShowPopup";
	public GetNewUserInfoAndShowPopup(HttpResponse response,String jobjStr,int daily_insta_type) {
		super(response,jobjStr);		
		this.daily_insta_type = daily_insta_type;
	}
	
	@Override
	public void process() {
		Logger.i(TAG,"processing GetFBInfoResponse response.status:"+this.getStatus());
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			JSONArray nearbyUSers =  body.getJSONArray("NearbyUsers");
			JSONObject thisNearbyUserJObj = nearbyUSers.getJSONObject(0);

            thisNearbyUser = new NearbyUser(thisNearbyUserJObj);
            thisNearbyUserFBInfo = thisNearbyUser.getUserFBInfo();

            if (BlockedUser.isUserBlocked(thisNearbyUserFBInfo.getFbid())) {
                Logger.d(TAG, "User is blocked.. not showing popup.");
                return;
            }
			Intent i = new Intent(Platform.getInstance().getContext(),NewUserDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("nearbyuserjsonstr", thisNearbyUserJObj.toString());
			i.putExtra("daily_insta_type", daily_insta_type);
			Platform.getInstance().getContext().startActivity(i);
			//status = body.getString("Status");			
			//ThisUserConfig.getInstance().putBool(ThisUserConfig.FBINFOSENTTOSERVER, true);
			
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {		
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Some error occured");
			Logger.e(TAG, "Error returned by server get fb info for user and show popup");
			e.printStackTrace();
		}
		
	}
	
}
