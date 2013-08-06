package in.co.hopin.Util;

import com.google.analytics.tracking.android.EasyTracker;

public class HopinTracker {
	
	public static void sendEvent(String category, String action, String label, Long value)
	{
		EasyTracker.getTracker().sendEvent(category, action, label, value);
	}
	
	public static void sendView(String viewString)
	{
		EasyTracker.getTracker().sendView(viewString);
	}
	

}
