package in.co.hopin.Server;

import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SaveFBInfoResponse extends ServerResponseBase{

	String status;
	
	private static final String TAG = "my.b1701.SB.Server.SaveFBInfoResponse";
	public SaveFBInfoResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);
	}
	
	@Override
	public void process() {
		Log.i(TAG,"processing SaveFBInfoResponse response.status:"+this.getStatus());		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");			
			//status = body.getString("Status");			
			ThisUserConfig.getInstance().putBool(ThisUserConfig.FBINFOSENTTOSERVER, true);
			ProgressHandler.dismissDialoge();
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {			
			Log.e(TAG, "Error returned by server on user add");
			e.printStackTrace();
		}
		
	}
	
}