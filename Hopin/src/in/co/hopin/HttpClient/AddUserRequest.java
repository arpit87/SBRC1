package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.AddUserResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.UserAttributes;

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

import android.app.Activity;
import android.util.Log;

public class AddUserRequest extends SBHttpRequest{
	public static final String URL = ServerConstants.SERVER_ADDRESS+ServerConstants.USERSERVICE+"/addUser/";

	HttpPost httpQuery;
	JSONObject jsonobj;	
	String uuid;
	HttpClient httpclient = new DefaultHttpClient();
	AddUserResponse addUserResponse;
	String jsonStr;
	Activity tutorial_activity;
	
	public AddUserRequest(String uuid,String username,Activity tutorial_activity)
	{
		super();
		this.uuid=uuid;		
		queryMethod = QueryMethod.Get;	
		this.tutorial_activity = tutorial_activity;
		jsonobj=new JSONObject();
		httpQuery =  new HttpPost(URL);
		URLStr = URL;
		try {
			jsonobj.put(ThisAppConfig.APPUUID, uuid);
			jsonobj.put(UserAttributes.USERNAME, username);
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
			try {
				response=httpclient.execute(httpQuery);
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
					
			addUserResponse =	new AddUserResponse(response,jsonStr,tutorial_activity);
			return addUserResponse;
		
	}
	
	

}

