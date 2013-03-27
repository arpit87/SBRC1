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

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Server.ChatServiceCreateUserResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.UserAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ChatServiceCreateUser extends SBHttpRequest{
		
		private final String TAG = "my.b1701.SB.HttpClient.ChatServiceCreateUser";
        public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.CHATSERVICE + "/createUser/";
		HttpPost httpQueryAddRequest;	
		JSONObject jsonobjAddRequest = new JSONObject();
		HttpClient httpclient = new DefaultHttpClient();
		ChatServiceCreateUserResponse chatServiceCreateUserResponse;
		String jsonStr;
		
		public ChatServiceCreateUser(String fbid)
		{
			//we will post 2 requests here
			//1)addrequest to add source and destination
			//2) getUsersRequest to get users
			super();
			queryMethod = QueryMethod.Post;
			httpQueryAddRequest =  new HttpPost(URL);
			try {
				//sometime fb id not written to file yet bfr this call happens so passing in as argument!!
				jsonobjAddRequest.put(UserAttributes.CHATUSERID, ThisUserConfig.getInstance().getString(ThisUserConfig.USERID));
				jsonobjAddRequest.put(UserAttributes.CHATUSERNAME, fbid);
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			StringEntity postEntityAddRequest = null;
			try {
				postEntityAddRequest = new StringEntity(jsonobjAddRequest.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			postEntityAddRequest.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			//Log.d(TAG, "calling server:" + jsonobjAddRequest.toString());	
			httpQueryAddRequest.setEntity(postEntityAddRequest);
			
					
		}
		
		public ServerResponseBase execute() {
				try {
					response=httpclient.execute(httpQueryAddRequest);
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
							
				chatServiceCreateUserResponse = new ChatServiceCreateUserResponse(response,jsonStr);			
				return chatServiceCreateUserResponse;
			
		}
		
		

	}

