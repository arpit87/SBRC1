package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.DeleteReqResponse;
import in.co.hopin.Server.FeedbackResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FeedbackRequest extends SBHttpRequest{

	private static String RESTAPI="saveFeedBack";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

    HttpClient httpclient = new DefaultHttpClient();
	HttpPost httpQuery;
	String jsonStr;
	JSONObject jsonobj;	
	
	public FeedbackRequest(String feedback)
	{
		super();
		queryMethod = QueryMethod.Get;		
        httpQuery =  new HttpPost(URL);               
        jsonobj=new JSONObject();		
        URLStr = URL;
		try {
			jsonobj.put(UserAttributes.USERID, ThisUserNew.getInstance().getUserID());
			jsonobj.put(UserAttributes.USERNAME, ThisUserConfig.getInstance().getString(ThisUserConfig.FBUSERNAME));
			jsonobj.put(UserAttributes.EMAIL, ThisUserConfig.getInstance().getString(ThisUserConfig.EMAIL));
			jsonobj.put("feedback", feedback);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	
		
		StringEntity postEntityUser = null;
		try {
			postEntityUser = new StringEntity(jsonobj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntityUser.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+jsonobj.toString());	
		httpQuery.setEntity(postEntityUser);
        
	}
	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);	
			try {
				response=httpclient.execute(httpQuery);
			} catch (Exception e) {
				HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute:executeexception",1L);
			}
			
			try {
				if(response==null)
					return null;
				jsonStr = responseHandler.handleResponse(response);
			} catch (Exception e) {
				HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute:responseexception",1L);
			}   			
			
		return new FeedbackResponse(response,jsonStr,RESTAPI);
	}

}
