package in.co.hopin.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.R;

import in.co.hopin.Adapter.HistoryAdapter;
import in.co.hopin.HelperClasses.*;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.provider.HistoryContentProvider;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartStrangerBuddyActivity extends Activity {
	
	private ProgressBar mProgress;
	private static final String TAG = "in.co.hopin.Activities.StartStrangerBuddyActivity";
	Runnable startMapActivity;
	Intent showSBMapViewActivity;
	Timer timer;
	AtomicBoolean mapActivityStarted = new AtomicBoolean(false);	
	private Context platformContext;

    private static Uri mHistoryUri = Uri.parse("content://" + HistoryContentProvider.AUTHORITY + "/db_fetch_only");
    private static String[] columns = new String[]{
            "sourceAddress",
            "destinationAddress",
            "timeOfTravel",
            "dateOfTravel",
            "dailyInstantType",
            "planInstantType",
            "takeOffer",
            "reqDate",
            "radioButtonId",
            "date"
    };
	
	
	
    /** Called when the activity is first created. */
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private void firstRun() {
		//get user_id from the server				
		String uuid = ThisAppInstallation.id(this.getBaseContext());
		ThisAppConfig.getInstance().putString(ThisAppConfig.APPUUID,uuid);
		ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP,true);
		ThisAppConfig.getInstance().putInt(ThisAppConfig.APPOPENCOUNT,0);
		//with uuid means first time start
		final Intent show_tutorial = new Intent(this,Tutorial.class);
		show_tutorial.putExtra("uuid", uuid);
		Runnable r = new Runnable() {
	          public void run() {	        		  
	        	  startActivity(show_tutorial);
	        	  finish();
	          }};
		Platform.getInstance().getHandler().postDelayed(r, 2000);
	}

    private boolean isLocationProviderEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void buildAlertMessageForLocationProvider() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location access is required to run the application, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        //Log.e(TAG, "clicked yes..");
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void buildAlertMessageForNoNetwork() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Network connection not detected. Please check network connecton and reopen Hopin")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                })           
                ;
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void onResume()
    {   	
    	super.onResume();
        //Log.e(TAG, "onresume");
        if (!isLocationProviderEnabled()){
            buildAlertMessageForLocationProvider();           
        }
        else  if(!SBConnectivity.isConnected())
        {
        	buildAlertMessageForNoNetwork();	           
        }
        else        	
        {
	
	        //Log.i(TAG,"started network listening ");
	        SBLocationManager.getInstance().StartListeningtoNetwork();
            loadHistoryFromDB();
	        platformContext = Platform.getInstance().getContext();
	       
	
	        //map activity can get started from 3 places, timer task if location found instantly
	        //else this new runnable posted after 3 seconds
	        //else on first run
	        showSBMapViewActivity = new Intent(platformContext, MapListViewTabActivity.class);
	        showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	
	        startMapActivity = new Runnable() {
	            public void run() {	            	
	                platformContext.startActivity(showSBMapViewActivity);
	                finish();
	            }};
	
	
	        if(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID) == "")
	        {
	            firstRun();
	        }
	        else
	        {
	            ThisUserNew.getInstance().setUserID(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID));
	            timer = new Timer();
	            timer.scheduleAtFixedRate(new GetNetworkLocationFixTask(), 500, 500);
	        }
        }
    }
    
    public void onPause()
    {
    	super.onPause();
    }

    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    public void onStop()
    {   	
    	super.onStop();
        EasyTracker.getInstance().activityStop(this);    	
    }
    
    private class GetNetworkLocationFixTask extends TimerTask
    { 
    	private int counter = 0;
         public void run() 
         {
        	 counter++;
        	 //Log.i(TAG, "timer task counter:"+counter);
        	 SBGeoPoint currGeo;
        	 
        	 //check if it got location by singleUpdateintent which works for froyo+
        	 currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
        	 
        	 if(currGeo == null)
        	 {
        		 Location lastBestLoc = SBLocationManager.getInstance().getLastXSecBestLocation(10*60);  
        		 if(lastBestLoc!=null)
        			 currGeo = new SBGeoPoint((int)(lastBestLoc.getLatitude()*1e6), (int)(lastBestLoc.getLongitude()*1e6));
        	 }
        	 
        	 if(currGeo != null || counter>5)
        	 {       
        		 timer.cancel();
       		  	 timer.purge();
        		 //ToastTracker.showToast("starting activity in counter:"+counter);  
        		 if(currGeo != null)
        			 ThisUserNew.getInstance().setCurrentGeoPoint(currGeo);        		 
        		 if(!mapActivityStarted.getAndSet(true))
	        	  {
        			 Platform.getInstance().getHandler().post(startMapActivity);
	        	  }
        	 }
          }
     }

    private void loadHistoryFromDB() {
        LinkedList<HistoryAdapter.HistoryItem> historyItemList = null;
        //Log.e(TAG, "Fetching searches");
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(mHistoryUri, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            //Log.e(TAG, "Empty result");
        } else {
            LinkedList<HistoryAdapter.HistoryItem> historyItems = new LinkedList<HistoryAdapter.HistoryItem>();
            if (cursor.moveToFirst()) {
                do {
                    HistoryAdapter.HistoryItem historyItem = new HistoryAdapter.HistoryItem(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4),cursor.getInt(5),
                            cursor.getInt(6), cursor.getString(7),cursor.getInt(8));
                    historyItems.add(historyItem);
                } while (cursor.moveToNext());

            }
            if(historyItems.size()>0)
                historyItemList = historyItems;
        }

        if (cursor!= null) {
            cursor.close();
        }

        if (historyItemList == null) {
            historyItemList = new LinkedList<HistoryAdapter.HistoryItem>();
        }

        ThisUserNew.getInstance().setHistoryItemList(historyItemList);
    }

}
