package in.co.hopin.Server;

import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Util.HopinTracker;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SaveFBInfoResponse extends ServerResponseBase{

	String status;
	
	private static final String TAG = "in.co.hopin.Server.SaveFBInfoResponse";
	public SaveFBInfoResponse(HttpResponse response,String jobjStr,String api) {
		super(response,jobjStr,api);
	}
	
	@Override
	public void process() {
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"processing SaveFBInfoResponse response.status:"+this.getStatus());		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");			
			//status = body.getString("Status");			
			ThisUserConfig.getInstance().putBool(ThisUserConfig.FBINFOSENTTOSERVER, true);
			ProgressHandler.dismissDialoge();
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {		
			HopinTracker.sendEvent("ServerResponse",getRESTAPI(),"ServerResponse:"+getRESTAPI()+":servererror",1L);
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error returned by server on user add");		
		}
		
	}
	
}
