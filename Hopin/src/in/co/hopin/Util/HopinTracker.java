package in.co.hopin.Util;

import in.co.hopin.HelperClasses.Event;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.google.analytics.tracking.android.EasyTracker;

public class HopinTracker {
	
	//common info
	public static String USERID = "uid";
	public static String APPUUID = "app_id";
	public static String APPVERSIONCODE = "app_vcode";
	public static String APPVERSIONNAME = "app_vname";
	public static String DEVICEID = "did";
	public static String IPADDRESS = "ip"; 
	public static String WIFIIPADDRESS = "wifiip"; 
	public static String MANUFACTURER = "manufacturer";
	public static String MODEL = "model";
	public static String FBID = "fbid";
	public static String ANDROIDVERSION = "sdk_ver";
	
	
	//inst info
	public static String MOBILECOUNTRYCODE = "mcc";  //MobileCountryCode
	public static String MOBILENETWORKCODE="mnc";
	public static String CURRLATITUDE="curr_lat";
	public static String CURRLONGITUDE="curr_longi";
	public static String TIMESTAMP="ts";		
	public static String NETWORKOPERATOR = "net_op";
	public static String NETWORKTYPE = "net_type";
	public static String NETWORKSUBTYPE = "net_subtype";
	public static String LOGINSTATE = "login";
	
	//arguments
	public static String GRPSIZE = "grp_size";
	public static String OTHERUSERID = "other_uid";
	public static String OTHERUSERFBID = "other_fbid";
	public static String APIRESPONSETIME = "res_time";
	public static String NUMMATCHES = "num_match";
	
	
	public static void sendEvent(String category, String action, String label, Long value, Map<String,Object> args)
	{
		EasyTracker.getTracker().sendEvent(category, action, label, value);
		args.put("event", label);
		String jsonString = createInstantaneousInfoJSON(args).toString();
		Event.addEvent(jsonString);
	}
	
	public static void sendEvent(String category, String action, String label, Long value)
	{
		EasyTracker.getTracker().sendEvent(category, action, label, value);
	}
	
	public static void sendView(String viewString)
	{
		EasyTracker.getTracker().sendView(viewString);
	}
	
	public static JSONObject createCommonInfoJSON()
	{
		JSONObject json = new JSONObject();
		//common info val
		  String USERIDVAL = ThisUserConfig.getInstance().getString(ThisUserConfig.USERID);
		  String APPUUIDVAL = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
		  int APPVERSIONCODEVAL = Platform.getInstance().getThisAppVersion();
		  String APPVERSIONNAMEVAL = Platform.getInstance().getThisAppVersionName();
		  String DEVICEIDVAL = ThisAppConfig.getInstance().getString(ThisAppConfig.DEVICEID);	
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  int sdk = Build.VERSION.SDK_INT;
		try {
			json.put(USERID, USERIDVAL);			
			json.put(APPUUID, APPUUIDVAL);
			json.put(APPVERSIONCODE, APPVERSIONCODEVAL);
			json.put(APPVERSIONNAME, APPVERSIONNAMEVAL);
			json.put(DEVICEID, DEVICEIDVAL);
			json.put(MANUFACTURER, manufacturer);
			json.put(MODEL, model);
			json.put(ANDROIDVERSION, sdk);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	private static JSONObject createInstantaneousInfoJSON(Map<String,Object> args)
	{
		JSONObject json = new JSONObject();
		String IPADDRESSESVAL = SBConnectivity.getipAddress();		 
		TelephonyManager telephonyManager = (TelephonyManager)Platform.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
		long unixTime = System.currentTimeMillis();		
		String networkOperator = telephonyManager.getNetworkOperator();
	    String mcc = networkOperator.substring(0, 3);
	    String mnc = networkOperator.substring(3);
	    double lat = 0.0;
	    double longi = 0.0;
	    SBGeoPoint currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
	    boolean isloggedin = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
	    String login_state = "none";
	    String fbid = "";
	    String operatorName = telephonyManager.getNetworkOperatorName();
	    String networkType = SBConnectivity.getNetworkType();
	    String networkSubType = SBConnectivity.getNetworkSubType();
	    if(isloggedin)
	    {
	    	login_state = "fb";
	    	fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
	    }
	    	
	    if(currGeo !=null)
	    {
	    	lat = currGeo.getLatitude();
	    	longi = currGeo.getLongitude();
	    }
	   
	    try {
			json.put(MOBILECOUNTRYCODE, mcc);			
			json.put(MOBILENETWORKCODE, mnc);
			json.put(CURRLATITUDE, lat);
			json.put(CURRLONGITUDE, longi);
			json.put(TIMESTAMP, unixTime);
			json.put(IPADDRESS, IPADDRESSESVAL);	
			json.put(LOGINSTATE, login_state);
			json.put(FBID, fbid);
			json.put(NETWORKOPERATOR, operatorName);
			json.put(NETWORKTYPE, networkType);
			json.put(NETWORKSUBTYPE, networkSubType);			
			Iterator entries = args.entrySet().iterator();
			while (entries.hasNext())
			{
				Map.Entry entry = (Map.Entry) entries.next();			    
			    json.put((String)entry.getKey(), entry.getValue());
			}
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
