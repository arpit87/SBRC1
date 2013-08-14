package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.AddThisUserSrcDstCarPoolResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AddThisUserScrDstCarPoolRequest extends SBHttpRequest {
	private final String TAG = "in.co.hopin.HttpClient.AddThisUserSrcDstCarpoolRequest";
	private static String RESTAPI="addCarpoolRequest";
	public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/"+RESTAPI+"/";

	HttpPost httpQueryAddRequest;
	JSONObject jsonobjAddRequest;
	HttpClient httpclient = new DefaultHttpClient();
	AddThisUserSrcDstCarPoolResponse addThisUserResponse;
	String jsonStr;

	public AddThisUserScrDstCarPoolRequest() {
		//we will post 2 requests here
		//1)addrequest to add source and destination
		//2) getUsersRequest to get users
		super();
		queryMethod = QueryMethod.Post;
		jsonobjAddRequest = GetServerAuthenticatedJSON();
		httpQueryAddRequest = new HttpPost(URL);
		URLStr = URL;
		try {
			populateEntityObject();
		} catch (JSONException e) {
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
		}

		StringEntity postEntityAddRequest = null;
		try {
			postEntityAddRequest = new StringEntity(jsonobjAddRequest.toString());
		} catch (UnsupportedEncodingException e) {
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
		}
		postEntityAddRequest.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "calling server:" + jsonobjAddRequest.toString());
		httpQueryAddRequest.setEntity(postEntityAddRequest);
	}

	private void populateEntityObject() throws JSONException {    	
		SBGeoPoint sourceGeoPoint =  ThisUserNew.getInstance().getSourceGeoPoint();
		SBGeoPoint destinationGeoPoint =  ThisUserNew.getInstance().getDestinationGeoPoint();
		jsonobjAddRequest.put(UserAttributes.SHAREOFFERTYPE, ThisUserNew.getInstance().get_Take_Offer_Type());
		if (sourceGeoPoint != null) {
			jsonobjAddRequest.put(UserAttributes.SRCLATITUDE, ThisUserNew.getInstance().getSourceGeoPoint().getLatitude());
			jsonobjAddRequest.put(UserAttributes.SRCLONGITUDE, ThisUserNew.getInstance().getSourceGeoPoint().getLongitude());
		} 
		if (destinationGeoPoint != null) {
			jsonobjAddRequest.put(UserAttributes.DSTLATITUDE, ThisUserNew.getInstance().getDestinationGeoPoint().getLatitude());
			jsonobjAddRequest.put(UserAttributes.DSTLONGITUDE, ThisUserNew.getInstance().getDestinationGeoPoint().getLongitude());
		}
		jsonobjAddRequest.put(UserAttributes.SRCADDRESS, ThisUserNew.getInstance().getSourceFullAddress());
		jsonobjAddRequest.put(UserAttributes.DSTADDRESS, ThisUserNew.getInstance().getDestinationFullAddress());       
		jsonobjAddRequest.put(UserAttributes.DATETIME, ThisUserNew.getInstance().getDateAndTimeOfTravel());
		if(ThisAppConfig.getInstance().getBool(ThisAppConfig.WOMANFILTER))
			jsonobjAddRequest.put(UserAttributes.WOMENFLTER, 1);
		if(ThisAppConfig.getInstance().getBool(ThisAppConfig.FBFRIENDONLYFILTER))
			jsonobjAddRequest.put(UserAttributes.FBFRIENDSFILTER, 1);
	}

	
	public ServerResponseBase execute() {
		HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute",1L);
		try {
			response = httpclient.execute(httpQueryAddRequest);
		} catch (Exception e) {
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
		}

		try {
			if(response==null)
				return null;
			jsonStr = responseHandler.handleResponse(response);
		} catch (Exception e) {
			HopinTracker.sendEvent("HttpRequest",RESTAPI,"httprequest:"+RESTAPI+":execute:responseexception",1L);
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
		}

		addThisUserResponse = new AddThisUserSrcDstCarPoolResponse(response, jsonStr,RESTAPI);
		addThisUserResponse.setReqTimeStamp(this.reqTimeStamp);
		return addThisUserResponse;
	}
}
