package in.co.hopin.LocationHelpers;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class NetworkListener implements LocationListener{
	
	long minTime=0L;
	float minDistnce=0F;	
	private static final String TAG = "my.b1701.SB.LocationHelpers.NetworkListener";
	
	private Location thisWindowBestLocation = null;	
	private Location lastWindowBestLocation = null;

	public static final int OUT_OF_SERVICE = 0;	
	public static final int TEMPORARILY_UNAVAILABLE = 1;	
	public static final int AVAILABLE = 2;
	
	public void start()
	{
		//Log.i(TAG,"strted listening to network");
		//ToastTracker.showToast("strted listning to network");
		//thisWindowBestLocation = null;
		
		SBLocationManager.getInstance().locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, this,Platform.getInstance().getContext().getMainLooper());
	}
	
	public void start(long minTime,float minDistance)
	{
		//Log.i(TAG,"strted listening to network wid mintim"+minTime+",mindist:"+minDistance);
		this.minTime=minTime;
		this.minDistnce=minDistance;
		thisWindowBestLocation = lastWindowBestLocation;
		SBLocationManager.getInstance().locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistnce, this,Platform.getInstance().getContext().getMainLooper());
	}
	
	public void stop()
	{
		if(thisWindowBestLocation != null)
		{
			//Log.i(TAG,"stopped listening to network,best loc thiswindowbestacc:"+thisWindowBestLocation.getAccuracy());
			lastWindowBestLocation = thisWindowBestLocation;
		}		
		SBLocationManager.getInstance().locManager.removeUpdates(this);
	}	

	public void onLocationChanged(Location location) {
		//we see that location bursts come in windows n then idle for given time
		//so we find most accurate location of a window and set it to thisWindowBest
		//Log.i(TAG,"network location changed");
		//Log.i(TAG,"new network location acc:"+ location.getAccuracy());
		//ToastTracker.showToast("loc changed:acc:"+location.getAccuracy());
		
		//locChangeCount = (locChangeCount+1)%LOC_CHANGE_PER_WINDOW;
		
		//we are considering fixed window size of LOC_CHANGE_PER_WINDOW. So we choose most accurate location
		//in that window
		//if(locChangeCount == 0)
		//	thisWindowBestLocation = null;
		
		if(thisWindowBestLocation == null ||
		   location.getAccuracy() < thisWindowBestLocation.getAccuracy() || 
		   location.getTime()-thisWindowBestLocation.getTime()>180000 )
		{		
			//window starting			
			//this listener runs only when we are on map view
			thisWindowBestLocation = location;
			SBGeoPoint newCurrentPoint = new SBGeoPoint((int)(thisWindowBestLocation.getLatitude()*1e6),(int)(thisWindowBestLocation.getLongitude()*1e6)); 
			ThisUserNew.getInstance().setCurrentGeoPoint(newCurrentPoint);
			if(MapListActivityHandler.getInstance().isUpdateMap() && MapListActivityHandler.getInstance().isMapInitialized())
			{				
				ThisUserNew.getInstance().setSourceGeoPoint(newCurrentPoint);
				MapListActivityHandler.getInstance().updateThisUserMapOverlay();				
			}
			
		}	
		
	}	
	

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		/*Log.i(TAG,"network status changed to:"+status);
		//Toast toast = Toast.makeText(Platform.getInstance().getContext(), "Network status change to:"+status, Toast.LENGTH_SHORT);       
		//toast.show();
		if(thisWindowBestLocation!=null)
		LocationUpdater.getInstance().UpdateToBestCurrentLocation(new SBLocation(thisWindowBestLocation));
		if(status == AVAILABLE) //window starting
			thisWindowBestLocation = null;
		//if(status == TEMPORARILY_UNAVAILABLE) //window ending*/			
		
	}

}
