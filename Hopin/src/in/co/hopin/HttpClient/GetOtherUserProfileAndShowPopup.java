package in.co.hopin.HttpClient;


import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.GetOtherUserProfileResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetOtherUserProfileAndShowPopup extends SBHttpRequest{
	private static String RESTAPI="getFBInfo";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQuery;	
	UrlEncodedFormEntity formEntity;
	HttpClient httpclient = new DefaultHttpClient();	
	GetOtherUserProfileResponse getOtherUserProfileAndShowPopup;
	JSONObject jsonobj;
	String jsonStr;	   
	
	public GetOtherUserProfileAndShowPopup(String fbid)
	{		
		super();
		queryMethod = QueryMethod.Post;
				
		//prepare getnearby request		
		httpQuery = new HttpPost(URL);
		jsonobj = GetServerAuthenticatedJSON();	
		URLStr = URL;
		try {				
			jsonobj.put(UserAttributes.FBID, fbid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+jsonobj.toString());	
		httpQuery.setEntity(postEntitygetNearbyUsers);
	
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
			
			getOtherUserProfileAndShowPopup = new GetOtherUserProfileResponse(response,jsonStr,RESTAPI);
			getOtherUserProfileAndShowPopup.setReqTimeStamp(this.reqTimeStamp);
			return getOtherUserProfileAndShowPopup;
		
	}
	
	

}



