package in.co.hopin.MapHelpers;

import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.Logger;

import java.util.ArrayList;

import in.co.hopin.R;
import android.util.Log;

public class ThisUserItemizedOverlay extends BaseItemizedOverlay{

	ArrayList<ThisUserOverlayItem> userList=new ArrayList<ThisUserOverlayItem>();
	ThisUserOverlayItem selfOverlayItem;
	private static final String TAG = "in.co.hopin.MapHelpers.ThisUserItemizedOverlay";
	private SBMapView mMapView = null;
	
	
	public ThisUserItemizedOverlay(SBMapView mapView) {		
		super(boundCenter(Platform.getInstance().getContext().getResources().getDrawable(R.drawable.map_dp_frame_shadow)));
		this.mMapView = mapView;
		// TODO Auto-generated constructor stub
	}
	
	public ThisUserItemizedOverlay() {		
		super(boundCenter(Platform.getInstance().getContext().getResources().getDrawable(R.drawable.map_dp_frame_shadow)));		
	}

	@Override
	protected BaseOverlayItem createItem(int i) {
		return userList.get(i);
	}
	

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return userList.size();
	}
	
	@Override
	public void addThisUser() {
		SBGeoPoint currentGeopoint = ThisUserNew.getInstance().getCurrentGeoPoint();
		if(currentGeopoint != null)
		{
			selfOverlayItem=new ThisUserOverlayItem(currentGeopoint, ThisUserNew.getInstance().getUserID(), "",mMapView);
			userList.add(selfOverlayItem);
			populate();
		}
	}
	
	public void  updateThisUser()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"updating this user,removing overlay");		
		mMapView.removeSelfView();
		if(selfOverlayItem!=null)
		{			
			userList.remove(selfOverlayItem);			
		}
		SBGeoPoint sourceGeopoint = ThisUserNew.getInstance().getSourceGeoPoint();
		if(sourceGeopoint != null)
		{
			selfOverlayItem=new ThisUserOverlayItem(sourceGeopoint, ThisUserNew.getInstance().getUserID(), "",mMapView);
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"adding new this overlay");
			userList.add(selfOverlayItem);
			populate();
		}
	}
	
	protected boolean onTap(int i)
	{
		Logger.i(TAG,"toggling this user view");
		selfOverlayItem.ToggleView();
		return true;
		
	}
	
	
	
	

}
