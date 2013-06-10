package in.co.hopin.HttpClient;

import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.GetMatchingCarPoolUsersResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GetMatchingCarPoolUsersRequest  extends SBHttpRequest{

    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/getCarpoolMatches/";
	
	HttpPost httpQueryGetNearbyUsers;	
	JSONObject jsonobjGetNearbyUsers;
	HttpClient httpclient = new DefaultHttpClient();
	GetMatchingCarPoolUsersResponse getNearbyUsersResponse;
	String jsonStr;
	public GetMatchingCarPoolUsersRequest()
	{		
		super();
		queryMethod = QueryMethod.Post;
				
		//prepare getnearby request		
		httpQueryGetNearbyUsers = new HttpPost(URL);
		jsonobjGetNearbyUsers = GetServerAuthenticatedJSON();
		try {
			jsonobjGetNearbyUsers.put(UserAttributes.USERID, ThisUserNew.getInstance().getUserID());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringEntity postEntitygetNearbyUsers = null;
		try {
			postEntitygetNearbyUsers = new StringEntity(jsonobjGetNearbyUsers.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntitygetNearbyUsers.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug", "calling server:"+jsonobjGetNearbyUsers.toString());	
		httpQueryGetNearbyUsers.setEntity(postEntitygetNearbyUsers);
		
	}
	
	public ServerResponseBase execute() {
			try {
				response=httpclient.execute(httpQueryGetNearbyUsers);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if(response==null)
					return null;
					jsonStr = responseHandler.handleResponse(response);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		
			
			getNearbyUsersResponse =	new GetMatchingCarPoolUsersResponse(response,jsonStr);
			return getNearbyUsersResponse;
		
	}
	
	

}
