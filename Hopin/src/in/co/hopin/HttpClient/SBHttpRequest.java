package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class SBHttpRequest {
	
	public enum QueryMethod {
		Get,
		Post,
		Put,
		Delete
	}
	
	QueryMethod queryMethod = null;
	String URLStr = "";
	HttpResponse response = null;
	long reqTimeStamp = System.currentTimeMillis();
	// Create a response handler
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	
	public abstract ServerResponseBase execute();	
	
	public String GetQueryURL()
	{
		return URLStr;
	}
	//do not add this to initial add user request
	public JSONObject GetServerAuthenticatedJSON()
	{
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(ThisAppConfig.APPUUID, ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID));
			jObj.put(ThisUserConfig.USERID, ThisUserConfig.getInstance().getString(ThisUserConfig.USERID));
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return jObj;
	}
}
