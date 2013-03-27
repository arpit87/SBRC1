package in.co.hopin.Server;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;

public class ChatServiceCreateUserResponse extends ServerResponseBase{

		String user_id;
			
		private static final String TAG = "my.b1701.SB.Server.ChatServiceCreateUserResponse";
		public ChatServiceCreateUserResponse(HttpResponse response,String jobjStr) {
			super(response,jobjStr);			
					
		}
		
		@Override
		public void process() {
			//this process is not called if u make syncd consecutive requests,that time only last process called
			Log.i(TAG,"processing ChatServiceCreateUserResponse response.status:"+this.getStatus());	
			
			//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
			Log.i(TAG,"got json "+jobj.toString());
			try {
				Log.i(TAG, "json:"+jobj.toString());
				body = jobj.getJSONObject("body");
				String username = body.getString("username");
				String password = body.getString("password");
				ToastTracker.showToast("chtusr:"+username);
				Log.i(TAG, "chat user created usrname:"+username + " pass:"+password);
				
				ThisUserConfig.getInstance().putString(ThisUserConfig.CHATUSERID, username);
				ThisUserConfig.getInstance().putString(ThisUserConfig.CHATPASSWORD, password);
				
				Intent loginToChatServer = new Intent();
				loginToChatServer.setAction("SBLoginToChatServer");
				loginToChatServer.putExtra("username", username);
				loginToChatServer.putExtra("password", password);
				Platform.getInstance().getContext().sendBroadcast(loginToChatServer);				
				ToastTracker.showToast("chat login intent sent for chat");
			} catch (JSONException e) {
				Log.e(TAG, "Error returned by server on chat user add");
				ToastTracker.showToast("Unable to add this chat user ");
				e.printStackTrace();
			}
			
		}
		
	}
