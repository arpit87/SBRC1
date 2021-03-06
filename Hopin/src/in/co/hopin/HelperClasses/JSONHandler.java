package in.co.hopin.HelperClasses;

import in.co.hopin.Users.NearbyUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONHandler {
	
	private JSONObject jObj;	
	private static JSONHandler instance=null;
	
	private JSONHandler(){}
	
	public static JSONHandler getInstance()
	{
		if(instance == null)
			instance = new JSONHandler();
		return instance;
		
	}
	
	public String getFBPicURLFromJSON(JSONObject jObj)
	{
		String URL = null;
		try {
			JSONObject picture = jObj.getJSONObject("picture");
			JSONObject data = picture.getJSONObject("data");
			URL = data.getString("url");
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return URL;
	}
	
	/*public JSONObject GetJSONObjectFromHttp(HttpResponse response)
	{
				
		if(response.getStatusLine().getStatusCode()!=200)
			jObj = null;
		StringBuilder builder = new StringBuilder();	   
	    String json = "";
	    HttpEntity entity;
		InputStream inputStream = null;
		try {
			entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String line;
			try{
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}}finally{
				reader.close();
				inputStream.close();
				}			
            json = builder.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);            
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
		return jObj;
		
	}*/
	
	public List<NearbyUser> GetNearbyUsersInfoFromJSONObject(JSONObject jObj)
	{
		
		//for 0 users we are returning null and not zero size list
		ArrayList<NearbyUser> nearbyUsers = null;
		try {			
						
			JSONArray users = jObj.getJSONArray("NearbyUsers");
						
			if(users.length() > 0)
				nearbyUsers = new ArrayList<NearbyUser>();
			
			for(int i=0;i<users.length();i++)
			{
				JSONObject thisOtherUser=users.getJSONObject(i);
				//Log.d("json",thisOtherUser.toString());
				NearbyUser u = new NearbyUser(thisOtherUser);
				nearbyUsers.add((u));				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nearbyUsers;
		
	}

}
