package in.co.hopin.Server;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.GetMatchingCarPoolUsersRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Users.UserAttributes;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class AddThisUserSrcDstCarPoolResponse extends ServerResponseBase{

    private static final String TAG = "my.b1701.SB.Server.AddUserResponse";
    

	String user_id;
		
	public AddThisUserSrcDstCarPoolResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);

				
	}
	
	@Override
	public void process() {
		//this process is not called if u make syncd consecutive requests,that time only last process called
		Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			ToastTracker.showToast("added this user src,dst for car pool,fetching match");
            body.put(UserAttributes.DAILYINSTATYPE, 0);
            ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_CARPOOL, body.toString());
            MapListActivityHandler.getInstance().setSourceAndDestination(body);
			SBHttpRequest getNearbyUsersRequest = new GetMatchingCarPoolUsersRequest();
	        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
			
		} catch (JSONException e) {
			Log.e(TAG, "Error returned by server on user add scr dst");
			ToastTracker.showToast("Network error,try again");
			ProgressHandler.dismissDialoge();
			e.printStackTrace();
		}
		
	}
	

	
}

