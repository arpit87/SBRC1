package in.co.hopin.Server;

import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import android.content.Intent;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class DeleteReqResponse extends ServerResponseBase{
	
	private static final String TAG = "in.co.hopin.Server.DeleteUserResponse";
	int daily_insta_type;

	public DeleteReqResponse(HttpResponse response,String jobjStr,int daily_insta_type) {
		super(response,jobjStr);
		this.daily_insta_type = daily_insta_type;
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		//Log.i(TAG,"processing PostUserReqDataResponse");
		//Log.i(TAG,"server response:"+jobj.toString());
		try {
			//body = jobj.getJSONObject("body");
			String body = jobj.getString("body");
			ToastTracker.showToast("Request deleted successfully");
			Intent notifyUpdateintent = new Intent();
			if(daily_insta_type == 1) {
                ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_INSTA, "");
				notifyUpdateintent.setAction(BroadCastConstants.INSTAREQ_DELETED);
            }
			else {
                ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_CARPOOL, "");
				notifyUpdateintent.setAction(BroadCastConstants.CARPOOLREQ_DELETED);
            }
			//this broadcast is for my active req page to update itself to no active req
			Platform.getInstance().getContext().sendBroadcast(notifyUpdateintent);
		} catch (JSONException e) {
			ToastTracker.showToast("Some error occured in delete request");
			e.printStackTrace();
		}
	}
}
