package in.co.hopin.Users;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {
	
	private String fb_id = "";
	private String name = "";
	JSONObject allInfo = null;
	private String installed_hopin = "";	
	
	public Friend(JSONObject jsonObject) {
        allInfo = jsonObject;
        try {
        	fb_id = allInfo.getString(UserAttributes.FRIENDFBID);
        } catch (JSONException e) {
        	return;
        }
        
        try {
        	name = allInfo.getString(UserAttributes.FRIENDNAME);
        } catch (JSONException e) {
        	return;
        }
        
        try {
        	installed_hopin = allInfo.getString(UserAttributes.INSTALLEDHOPIN);
        } catch (JSONException e) {
        	return;
        }
	}
	
	public String getFb_id() {
		return fb_id;
	}
	public String getName() {
		return name;
	}
	
	public String getImageURL() {
		String picurl = "http://graph.facebook.com/" + fb_id + "/picture?type=small";
		return picurl;
	}
	
	public boolean hasInstalledHopin()
	{
		if(installed_hopin.equals("1"))
			return true;
		else 
			return false;		
	}
	

}
