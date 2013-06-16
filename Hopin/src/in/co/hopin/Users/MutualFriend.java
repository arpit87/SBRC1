package in.co.hopin.Users;

import org.json.JSONException;
import org.json.JSONObject;

public class MutualFriend {
	
	private String fb_id = "";
	private String name = "";
	JSONObject allInfo = null;
	
	public MutualFriend(JSONObject jsonObject) {
        allInfo = jsonObject;
        try {
        	fb_id = allInfo.getString(UserAttributes.MUTUALFRIENDFBID);
        } catch (JSONException e) {
        	return;
        }
        
        try {
        	name = allInfo.getString(UserAttributes.MUTUALFRIENDNAME);
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
	
	
	

}
