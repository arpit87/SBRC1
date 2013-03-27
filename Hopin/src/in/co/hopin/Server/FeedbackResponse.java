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

public class FeedbackResponse extends ServerResponseBase{
	
	private static final String TAG = "my.b1701.SB.Server.FeedbackResponse";
	
	public FeedbackResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);
		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		Log.i(TAG,"processing FeedbackResponse");
		Log.i(TAG,"server response:"+jobj.toString());
		try {			
			String body = jobj.getString("body");
			ToastTracker.showToast("Feedback saved successfully");			
		} catch (JSONException e) {
			ToastTracker.showToast("Some error occured in saving feedback");
			e.printStackTrace();
		}
	}
}
