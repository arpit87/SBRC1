package in.co.hopin.ActivityHandlers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import in.co.hopin.R;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.Adapter.NearbyUsersListViewAdapter;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.Fragments.FBLoginDialogFragment;
import in.co.hopin.Fragments.SBListFragment;
import in.co.hopin.Fragments.SBMapFragment;
import in.co.hopin.HelperClasses.*;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.MapHelpers.BaseItemizedOverlay;
import in.co.hopin.MapHelpers.GourpedNearbyUsersIteamizedOverlay;
import in.co.hopin.MapHelpers.NearbyUsersItemizedOverlay;
import in.co.hopin.MapHelpers.ThisUserItemizedOverlay;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.*;
import in.co.hopin.Util.StringUtils;

import java.util.*;

public class MapListActivityHandler  extends BroadcastReceiver{
	
	SBMapView mapView;	
	private static final String TAG = "in.co.hopin.ActivityHandlers.MapActivityHandler";
	private static MapListActivityHandler instance=new MapListActivityHandler();
	private MapListViewTabActivity underlyingActivity;	
	private BaseItemizedOverlay nearbyUserGroupItemizedOverlay;
	private BaseItemizedOverlay nearbyUserItemizedOverlay;
	private MapController mapcontroller;
	private BaseItemizedOverlay thisUserOverlay;
	private boolean updateMap = false;
	private ProgressDialog progressDialog;
	AlertDialog alertDialog ;	
	private boolean mapInitialized = false;
	private SBMapFragment mapFrag;
	private SBListFragment listFrag;
	private boolean fbloginPromptIsShowing = false;
	PopupWindow fbPopupWindow = null;
	View fbloginlayout = null;
	ViewGroup popUpView = null;
	ViewGroup mListViewContainer;	
	ImageView mListImageView;
	private TextView mDestination;
	private TextView mSource;
	private TextView mUserName;
	private TextView mtime;
	
	
			
	public BaseItemizedOverlay getNearbyUserItemizedOverlay() {
		return nearbyUserItemizedOverlay;
	}

    public BaseItemizedOverlay getNearbyUserGroupItemizedOverlay() {
        return nearbyUserGroupItemizedOverlay;
    }

	public boolean isMapInitialized() {
		return mapInitialized;
	}


	public MapListViewTabActivity getUnderlyingActivity() {
		return underlyingActivity;			
	}

	public void setUnderlyingActivity(MapListViewTabActivity underlyingActivity) {
		this.underlyingActivity = underlyingActivity;
	}

	public void setListFrag(SBListFragment listFrag) {
		this.listFrag = listFrag;
	}
	
	public void setMapFrag(SBMapFragment mapFrag) {
		this.mapFrag = mapFrag;
	}

	private MapListActivityHandler(){super();}	
	
	
	public static MapListActivityHandler getInstance()
	{		
		return instance;
		
	}

	public SBMapView getMapView() {
		return mapView;
	}

	public void setMapView(SBMapView mapView) {
		this.mapView = mapView;
		 mapcontroller = mapView.getController();
		 mapView.setBuiltInZoomControls(true);
	}	
	
	
	public boolean isUpdateMap() {
		return updateMap;
	}


	public void setUpdateMap(boolean updateMapRealTime) {
		this.updateMap = updateMapRealTime;
	}
		
	public void initMyLocation() 
	{ 
		
		SBGeoPoint currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
		if(currGeo == null)
		{
			//location not found yet after initial screen!try more for 6 secs
			progressDialog = ProgressDialog.show(underlyingActivity, "Fetching location", "Trying,please wait..", true);			
			 Runnable fetchLocation = new Runnable() {
			      public void run() {
			    	  Location currLoc = SBLocationManager.getInstance().getLastXSecBestLocation(6*60);
			    	  progressDialog.dismiss();
			    	  if(currLoc != null)
			    	  {			    		  
			    		  ThisUserNew.getInstance().setCurrentGeoPoint(new SBGeoPoint((int) (currLoc.getLatitude() * 1e6), (int) (currLoc.getLongitude() * 1e6)));
			    		  ThisUserNew.getInstance().setCurrentGeoPointToSourceGeopoint();
			    		  putInitialOverlay();
			    	  }
			    	  else
			    	  {
			    		  alertDialog = new AlertDialog.Builder(underlyingActivity).create(); 
			    		  alertDialog.setTitle("Boo-hoo..");
			    		  alertDialog.setMessage("Some problem with fetching network location,please enter source location yourself in user search");
			    		  alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			    	           public void onClick(DialogInterface dialog, int id) {
			    	                dialog.cancel();
			    	           }
			    	       });
			    		  alertDialog.show();
			    	  }
			      } 
			 };			        
			Platform.getInstance().getHandler().postDelayed(fetchLocation, 4000);// post after 6 secs
		}
		else
		{				
			ThisUserNew.getInstance().setCurrentGeoPointToSourceGeopoint();
			putInitialOverlay();		
		}
	}
	
	public void myLocationButtonClick()
	{
		if(!mapInitialized)
		{
			initMyLocation();	
			return;
		}
		
		//if we have source which might be different from current then zoom in to source
		SBGeoPoint sourceGeoPoint = ThisUserNew.getInstance().getSourceGeoPoint();
		if(sourceGeoPoint!=null)
		{
			centreMapTo(sourceGeoPoint);
			return;
		}
		//else to current
		int startInterval = 300;
		Location thisCurrLoc = SBLocationManager.getInstance().getLastXSecBestLocation(startInterval);
		if(thisCurrLoc == null)
		{
			progressDialog = ProgressDialog.show(underlyingActivity, "Fetching location", "Please wait..", true);
			for(int attempt = 1 ; attempt <= 4; attempt++ )
			{
				//thisCurrLoc = SBLocationManager.getInstance().getCurrentBestLocation(location)
				thisCurrLoc = SBLocationManager.getInstance().getLastXSecBestLocation(startInterval*attempt);
				if(thisCurrLoc != null)
				{
					break;
				}
			}
			progressDialog.dismiss();
		}
		if(thisCurrLoc!=null)
		{
			SBGeoPoint currGeo = new SBGeoPoint((int)(thisCurrLoc.getLatitude()*1e6),(int)(thisCurrLoc.getLongitude()*1e6));
			ThisUserNew.getInstance().setCurrentGeoPoint(currGeo);
			ThisUserNew.getInstance().setSourceGeoPoint(currGeo);
			MapListActivityHandler.getInstance().setUpdateMap(true);
			updateThisUserMapOverlay();
		}
	}
		
public void centreMapTo(SBGeoPoint centrePoint)
{
	if(centrePoint !=null)
		mapcontroller.animateTo(centrePoint);
}

public void centreMapToPlusLilUp(SBGeoPoint centrePoint)
{
	GeoPoint lilUpcentrePoint = new GeoPoint(centrePoint.getLatitudeE6()+90000/mapView.getZoomLevel(), centrePoint.getLongitudeE6());
	if(lilUpcentrePoint !=null)
		mapcontroller.animateTo(lilUpcentrePoint);
}
	
	
	
	private void putInitialOverlay()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"initializing this user location");
	    mapcontroller.setZoom(14);
	    if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"setting myoverlay");        
	    thisUserOverlay = new ThisUserItemizedOverlay(mapView); 
	    //SBGeoPoint currGeo = ThisUser.getInstance().getCurrentGeoPoint();
	    //if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"location is:"+currGeo.getLatitudeE6()+","+currGeo.getLongitudeE6());		
	    thisUserOverlay.addThisUser();	    
	    mapView.getOverlays().add(thisUserOverlay);
	    mapView.postInvalidate();	       
	    mapcontroller.animateTo(ThisUserNew.getInstance().getCurrentGeoPoint());
	    //onResume of mapactivity doesnt update user till its once initialized
	    mapInitialized = true;
	  
	}


	
	private void updateNearbyUsersonMap() {
		
		//caution while updating nearbyusers
		//this user may be interacting with a view so we are going to show progressbar			
					
		List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
		
		//update map view
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating earby user");
		if(nearbyUserItemizedOverlay!=null || nearbyUserGroupItemizedOverlay != null)
		{
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"removing prev nearby users overlay");			
			if (nearbyUserItemizedOverlay != null) {
                mapView.getOverlays().remove(nearbyUserItemizedOverlay);
            }
            if (nearbyUserGroupItemizedOverlay != null) {
                mapView.getOverlays().remove(nearbyUserGroupItemizedOverlay);
            }
			mapView.removeAllNearbyUserView();
		}
      
		if(nearbyUsers == null || nearbyUsers.size()==0)
			return;
	
        Context context = Platform.getInstance().getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        float  ppi = displayMetrics.densityDpi * density;
        float factor = displayMetrics.densityDpi / 160;
        double gridWidth = ppi * 0.2; //pixels in 5 mm

        Map<String,List<NearbyUser>> gridToUsersMap = new HashMap<String, List<NearbyUser>>();
        for (NearbyUser nearbyUser : nearbyUsers) {
            SBGeoPoint geoPoint = nearbyUser.getUserLocInfo().getGeoPoint();
            Point point = mapView.getProjection().toPixels(geoPoint, null);
            float x = point.x * factor;
            float y = point.y * factor;

            int column = (int)(x/gridWidth);
            int row = (int) (y/gridWidth);
            String key = column + ":" + row;
            List<NearbyUser> users = gridToUsersMap.get(key);
            if (users == null) {
                users = new ArrayList<NearbyUser>();
                gridToUsersMap.put(key, users);
            }
            users.add(nearbyUser);
        }
		
        List<NearbyUserGroup> groups = new LinkedList<NearbyUserGroup>();
        List<NearbyUser> individualUsers = new LinkedList<NearbyUser>();
        for (Map.Entry<String, List<NearbyUser>> entry : gridToUsersMap.entrySet()){
            if (entry.getValue().size() == 1){
                individualUsers.add(entry.getValue().get(0));
            } else {
                groups.add(new NearbyUserGroup(entry.getValue()));
            }
        }
		
        if(!individualUsers.isEmpty())
        {
	        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"adding individualUsers useroverlay,no indi:"+individualUsers.size());	
	        nearbyUserItemizedOverlay = new NearbyUsersItemizedOverlay(mapView);
	        nearbyUserItemizedOverlay.addList(individualUsers);
	        mapView.getOverlays().add(nearbyUserItemizedOverlay);
        }
        
        if(!groups.isEmpty())
        {
        	if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"adding groups useroverlay,no.groups:"+groups.size());
			nearbyUserGroupItemizedOverlay = new GourpedNearbyUsersIteamizedOverlay(mapView);
			nearbyUserGroupItemizedOverlay.addList(groups);
			 mapView.getOverlays().add(nearbyUserGroupItemizedOverlay);
        }
      
		
		//show fb login popup at bottom if not yet logged in
		boolean isfbloggedin = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);
		if(!isfbloggedin)
		{
			fbloginpromptpopup_show(true);
		}	
		
		ProgressHandler.dismissDialoge();
		
	}
	
	public void updateOverlayOnZoomChange()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating nearby users on zoom change");
		updateNearbyUsersonMap();		
		mapView.postInvalidate();
	}
	
	public void updateNearbyUsersOnUsersChange()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating nearby users on user change");
		updateNearbyUsersonMap();
		updateNearbyUserOnList();
		centerMap();
		mapView.postInvalidate();
	}

    private void updateNearbyUserOnList() {
    	List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if (nearbyUsers == null){
            nearbyUsers = Collections.emptyList();
        }

        NearbyUsersListViewAdapter adapter = new NearbyUsersListViewAdapter(underlyingActivity, nearbyUsers);

        if(listFrag != null)
        {
            listFrag.setListAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void fbloginpromptpopup_show(boolean show)
	{
		
		if(show )
		{
			if(!fbloginPromptIsShowing)
			{
				if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"showing fblogin prompt");	
				popUpView = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.fbloginpromptpopup, null); 
				fbPopupWindow = new PopupWindow(popUpView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false); //Creation of popup
				fbPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);   
				fbPopupWindow.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);    // Displaying popup
		        fbloginPromptIsShowing = true;		
		        fbPopupWindow.setTouchable(true);
		        fbPopupWindow.setFocusable(false);
		        //fbPopupWindow.setOutsideTouchable(true);
		        fbloginlayout = popUpView.findViewById(R.id.fbloginpromptloginlayout);
		        fbloginlayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
                        MapListActivityHandler.getInstance().closeExpandedViews();
						FBLoginDialogFragment fblogin_dialog = FBLoginDialogFragment.newInstance(underlyingActivity.getFbConnector());
						fblogin_dialog.show(underlyingActivity.getSupportFragmentManager(), "fblogin_dialog");
						fbPopupWindow.dismiss();
						fbloginPromptIsShowing = false;
						if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"fblogin prompt clicked");
					}
				});
		        ImageView buttonClosefbprompt = (ImageView) popUpView.findViewById(R.id.fbloginpromptclose);		        
		        buttonClosefbprompt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						fbPopupWindow.dismiss();
						fbloginPromptIsShowing = false;
					}
				});
			}
			else
			{
				//will flicker prompt here if already showing
				TextView fblogintext = (TextView) popUpView.findViewById(R.id.fbloginprompttext);
				Animation anim = new AlphaAnimation(0.0f, 1.0f);
		        anim.setDuration(50); //You can manage the time of the blink with this parameter
		        anim.setStartOffset(20);
		        anim.setRepeatMode(Animation.REVERSE);
		        anim.setRepeatCount(6);
		        fblogintext.startAnimation(anim);				
			}
			//popUpView.setBackgroundResource(R.drawable.transparent_black);
		}
		if(!show)
		{
			if(fbloginPromptIsShowing && fbPopupWindow!=null)
				fbPopupWindow.dismiss();
			fbloginPromptIsShowing = false;
		}
	}
	
	public void updateThisUserMapOverlay()
	{		
		//be careful here..do we have location yet?
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"update this user called");	
		if(thisUserOverlay == null)	
		{			
			thisUserOverlay = new ThisUserItemizedOverlay(mapView);
			thisUserOverlay.updateThisUser();	    
		    mapView.getOverlays().add(thisUserOverlay);		   
		}
		else
		{
		    thisUserOverlay.updateThisUser();
		    if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"this user map overlay updated");
		}
	    mapView.postInvalidate();	    
	    //dont centre here else on every automatic update it centres
	    //mapcontroller.animateTo(ThisUser.getInstance().getSourceGeoPoint());		
	}	
	
		
	private void centerMap() {

		int mylat = ThisUserNew.getInstance().getSourceGeoPoint().getLatitudeE6();
		int mylon = ThisUserNew.getInstance().getSourceGeoPoint().getLongitudeE6();
        int minLat = mylat;
        int maxLat = mylat;
        int minLon = mylon;
        int maxLon = mylon;
        
        List<NearbyUser> nearbyUsers = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if (nearbyUsers != null) {
            for (NearbyUser n : nearbyUsers) {
                    SBGeoPoint geoPoint = n.getUserLocInfo().getGeoPoint();
                    int lat = (int) (geoPoint.getLatitudeE6());
                    int lon = (int) (geoPoint.getLongitudeE6());

                    maxLat = Math.max(lat, maxLat);
                    minLat = Math.min(lat, minLat);
                    maxLon = Math.max(lon, maxLon);
                    minLon = Math.min(lon, minLon);
            }
        }

        mapcontroller.zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));        
        mapcontroller.animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        mapView.setOldZoomLevel(mapView.getZoomLevel());
}
	
public void clearAllData()
{
    if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "clearing all data");
    thisUserOverlay = null;
    nearbyUserItemizedOverlay = null;
    nearbyUserGroupItemizedOverlay = null;
    mListViewContainer = null;
	if(mapView!=null)
	{
		mapView.setOldZoomLevel(-1);
		mapView.removeAllViews();
		mapView.getOverlays().clear();
	}
	if(listFrag!=null)
	{		
		listFrag.reset();
	}
	if(mSource!=null) {
        mSource.setText(R.string.source_listview);
    }
	if(mDestination!=null) {
		mDestination.setText(R.string.destination_listview);
    }
	if(mtime!=null)
		mtime.setText("");
}


@Override
public void onReceive(Context context, Intent intent) {
	String intentAction = intent.getAction();
	if(intentAction.equals(BroadCastConstants.NEARBY_USER_UPDATED))
	{
		//ToastTracker.showToast("update intent received");
		updateNearbyUsersOnUsersChange();
	}	
}

public ViewGroup getThisListContainerWithListView() {
    if (mListViewContainer == null) {
        mListViewContainer = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.nearbyuserlistview, null, false);
        mListImageView = (ImageView) mListViewContainer.findViewById(R.id.selfthumbnail);
        mUserName = (TextView) mListViewContainer.findViewById(R.id.my_name_listview);
        mDestination = (TextView) mListViewContainer.findViewById(R.id.my_destination_listview);
        mSource =  (TextView) mListViewContainer.findViewById(R.id.my_source_listview);        
       
        mtime = (TextView) mListViewContainer.findViewById(R.id.my_time_listview); 
        //mMapViewContainer.removeView(mMapView);
    }    

	return mListViewContainer;
}

public void updateUserPicInListView() {
    if (mListImageView != null) {
        String fbPicURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL);
        if (fbPicURL != "") {
            SBImageLoader.getInstance().displayImageElseStub(fbPicURL, mListImageView, R.drawable.userpicicon);
        } else {
            mListImageView.setImageDrawable(Platform.getInstance().getContext().getResources().getDrawable(R.drawable.userpicicon));
        }
    }
}

public void updateUserNameInListView() {
    if (mUserName != null) {
        String userName = ThisUserConfig.getInstance().getString(ThisUserConfig.USERNAME);
        if (userName=="") {
        	//ToastTracker.showToast("haaw..username null!!");
            return;
        }
        mUserName.setText(userName);
    }
}

public void updateSrcDstTimeInListView() {

    if (mListViewContainer == null) {
        mListViewContainer = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.nearbyuserlistview, null, false);
        mListImageView = (ImageView) mListViewContainer.findViewById(R.id.selfthumbnail);
        mUserName = (TextView) mListViewContainer.findViewById(R.id.my_name_listview);
        mDestination = (TextView) mListViewContainer.findViewById(R.id.my_destination_listview);
        mSource = (TextView) mListViewContainer.findViewById(R.id.my_source_listview);
        mtime = (TextView) mListViewContainer.findViewById(R.id.my_time_listview);
    }

    String destination = ThisUserNew.getInstance().getDestinationFullAddress();
    if (!StringUtils.isBlank(destination)) {
        mDestination.setText(destination);
        String source = ThisUserNew.getInstance().getSourceFullAddress();
        if (StringUtils.isBlank(source))
            source = "My Location";
        mSource.setText(source);

        String date_time = ThisUserNew.getInstance().getDateAndTimeOfTravel();
        if (!StringUtils.isBlank(date_time)) {
            mtime.setText("Time: " + StringUtils.formatDate("yyyy-MM-dd HH:mm", "h:mm a, EEE, MMM d", date_time));
        }
    }
}

    public void closeExpandedViews(){
        BaseItemizedOverlay nearbyUserOverlay = MapListActivityHandler.getInstance().getNearbyUserItemizedOverlay();
        if(nearbyUserOverlay!=null)
            nearbyUserOverlay.removeExpandedShowSmallViews();
        BaseItemizedOverlay nearbyUserGroupOverlay = MapListActivityHandler.getInstance().getNearbyUserGroupItemizedOverlay();
		if(nearbyUserGroupOverlay!=null)
			nearbyUserGroupOverlay.removeExpandedShowSmallViews();
    }

    public void setSourceAndDestination(JSONObject jsonObject) throws JSONException {
        double srcLat = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLATITUDE));
        double srcLong = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLONGITUDE));
        double destLat = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLATITUDE));
        double destLong = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLONGITUDE));
        String srcAddress = jsonObject.getString(UserAttributes.SRCADDRESS);
        String srcLocality = jsonObject.getString(UserAttributes.SRCLOCALITY);
        String dstAddress = jsonObject.getString(UserAttributes.DSTADDRESS);
        String dstLocality = jsonObject.getString(UserAttributes.DSTLOCALITY);
        String dateTime = jsonObject.getString(UserAttributes.DATETIME);
        int dailyInstaType = jsonObject.getInt(UserAttributes.DAILYINSTATYPE);
        int shareOfferType = Integer.parseInt(jsonObject.getString(UserAttributes.SHAREOFFERTYPE));

        ThisUserNew.getInstance().setSourceGeoPoint(new SBGeoPoint((int)(srcLat*1e6),(int)(srcLong*1e6)));
        ThisUserNew.getInstance().setDestinationGeoPoint(new SBGeoPoint((int) (destLat * 1e6), (int) (destLong * 1e6)));
        ThisUserNew.getInstance().setSourceFullAddress(srcAddress);
        ThisUserNew.getInstance().setSourceLocality(srcLocality);
        ThisUserNew.getInstance().setDestinationFullAddress(dstAddress);
        ThisUserNew.getInstance().setDestiantionLocality(dstLocality);
        ThisUserNew.getInstance().setDateOfTravel(StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", dateTime));
        ThisUserNew.getInstance().setTimeOfTravel(StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "HH:mm", dateTime));
        ThisUserNew.getInstance().set_Daily_Instant_Type(dailyInstaType);
        ThisUserNew.getInstance().set_Take_Offer_Type(shareOfferType);

        MapListActivityHandler.getInstance().updateThisUserMapOverlay();
        MapListActivityHandler.getInstance().centreMapTo(ThisUserNew.getInstance().getSourceGeoPoint());
    }

}
