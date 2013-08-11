package in.co.hopin.Util;

import in.co.hopin.HelperClasses.Event;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.analytics.tracking.android.EasyTracker;

public class HopinTracker {
	
	//common info
	private static String USERID = "user_id";
	private static String APPUUID = "app_id";
	private static String APPVERSIONCODE = "app_versioncode";
	private static String APPVERSIONNAME = "app_versionname";
	private static String DEVICEID = "device_id";
	private static String IPADDRESS = "ipaddresses"; 
	
	//inst info
	private static String MOBILECOUNTRYCODE = "mobile_countery_code";  //MobileCountryCode
	private static String MOBILENETWORKCODE="mobile_network_code";
	private static String CURRLATITUDE="current_latitude";
	private static String CURRLONGITUDE="current_longitude";
	private static String TIMESTAMP="ts";
	private static String ISWIFI="is_wifi";
		
	//common info val
	private static String USERIDVAL = ThisUserConfig.getInstance().getString(ThisUserConfig.USERID);
	private static String APPUUIDVAL = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
	private static int APPVERSIONCODEVAL = Platform.getInstance().getThisAppVersion();
	private static String APPVERSIONNAMEVAL = Platform.getInstance().getThisAppVersionName();
	private static String DEVICEIDVAL = ThisAppConfig.getInstance().getString(ThisAppConfig.DEVICEID);
	private static String IPADDRESSESVAL = SBConnectivity.getipAddress();
	private static JSONObject commonInfoJSON = createCommonInfoJSON();
	static TelephonyManager telephonyManager = (TelephonyManager)Platform.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
	
	public static void sendEvent(String category, String action, String label, Long value, String Arg1)
	{
		EasyTracker.getTracker().sendEvent(category, action, label, value);
		String event = label + Arg1;
		JSONObject[] objs = new JSONObject[] {createCommonInfoJSON() , createInstantaneousInfoJSON(event)};		
		Event.addEvent(mergeJSONObjects(objs));
	}
	
	public static void sendEvent(String category, String action, String label, Long value)
	{
		EasyTracker.getTracker().sendEvent(category, action, label, value);
	}
	
	public static void sendView(String viewString)
	{
		EasyTracker.getTracker().sendView(viewString);
	}
	
	private static JSONObject createCommonInfoJSON()
	{
		JSONObject json = new JSONObject();
		try {
			json.put(USERID, USERIDVAL);			
			json.put(APPUUID, APPUUIDVAL);
			json.put(APPVERSIONCODE, APPVERSIONCODEVAL);
			json.put(APPVERSIONNAME, APPVERSIONNAMEVAL);
			json.put(DEVICEID, DEVICEIDVAL);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	private static JSONObject createInstantaneousInfoJSON(String event)
	{
		JSONObject json = new JSONObject();
		long unixTime = System.currentTimeMillis();		
		String networkOperator = telephonyManager.getNetworkOperator();
	    String mcc = networkOperator.substring(0, 3);
	    String mnc = networkOperator.substring(3);
	    double lat = 0.0;
	    double longi = 0.0;
	    SBGeoPoint currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
	    if(currGeo !=null)
	    {
	    	lat = currGeo.getLatitude();
	    	longi = currGeo.getLongitude();
	    }
	    boolean isWifi = SBConnectivity.isWifi();
	    try {
			json.put(MOBILECOUNTRYCODE, mcc);			
			json.put(MOBILENETWORKCODE, mnc);
			json.put(CURRLATITUDE, lat);
			json.put(CURRLONGITUDE, longi);
			json.put(TIMESTAMP, unixTime);
			json.put(IPADDRESS, IPADDRESSESVAL);
			json.put(ISWIFI, isWifi);
			json.put("event", event);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	private  static String mergeJSONObjects(JSONObject[] objs)
	{
		JSONObject merged = new JSONObject();		
		for (JSONObject obj : objs) {
		    Iterator it = obj.keys();
		    while (it.hasNext()) {
		        String key = (String)it.next();
		        try {
					merged.put(key, obj.get(key));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return merged.toString();
	}

}
