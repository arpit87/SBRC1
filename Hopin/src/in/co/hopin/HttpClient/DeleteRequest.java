package in.co.hopin.HttpClient;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.DeleteReqResponse;
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

public class DeleteRequest extends SBHttpRequest{

	private static String RESTAPI="deleteRequest";
	public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/"+RESTAPI+"/";

    HttpClient httpclient = new DefaultHttpClient();
	HttpPost httpQuery;
	String jsonStr;
	JSONObject jsonobj;	
	int daily_insta_type = 0;
	public DeleteRequest(int daily_insta_type)
	{
		super();
		queryMethod = QueryMethod.Get;		
        httpQuery =  new HttpPost(URL);
        this.daily_insta_type = daily_insta_type;        
        jsonobj = GetServerAuthenticatedJSON();	
        URLStr = URL;
		
		try {
			jsonobj.put(UserAttributes.USERID, ThisUserNew.getInstance().getUserID());
			jsonobj.put(UserAttributes.DELETEDAILYINSTATYPE, daily_insta_type);
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
			
			DeleteReqResponse delUserResponse = new DeleteReqResponse(response,jsonStr,daily_insta_type,RESTAPI);
			delUserResponse.setReqTimeStamp(this.reqTimeStamp);
			return delUserResponse;
	}

}
