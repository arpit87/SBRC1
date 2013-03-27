package in.co.hopin.LocationHelpers;

import in.co.hopin.Users.ThisUserNew;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationUpdaterFromIntent extends BroadcastReceiver{
	
	private final String TAG = "my.b1701.SB.LocationHelpers.LocationUpdaterFromIntent";  
		
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "location intent received");
		String locationKey = LocationManager.KEY_LOCATION_CHANGED;
		if(intent.hasExtra(locationKey))
		{
			context.unregisterReceiver(this);
			Location location = (Location)intent.getExtras().get(locationKey);
			Log.d(TAG,"updating loc in intent");
			ThisUserNew.getInstance().setCurrentGeoPoint(new SBGeoPoint((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6)));
		}
		else
			Log.d(TAG,"lockey not found in loc intent");
	}

	
}
