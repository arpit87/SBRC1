package in.co.hopin.MapHelpers;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.Users.NearbyUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.co.hopin.R;
import android.content.Context;


public class NearbyUsersItemizedOverlay extends BaseItemizedOverlay{

	ArrayList<NearbyUserOverlayItem> userList=new ArrayList<NearbyUserOverlayItem>();
	private SBMapView mMapView = null;
	private static Context context = MapListActivityHandler.getInstance().getUnderlyingActivity();
	
	public NearbyUsersItemizedOverlay(SBMapView mapView) {
		super(boundCenter(context.getResources().getDrawable(R.drawable.map_dp_frame_shadow)));
		this.mMapView = mapView;
	}
	
	public NearbyUsersItemizedOverlay() {
		super(boundCenter(context.getResources().getDrawable(R.drawable.map_dp_frame_shadow)));
		}
	
	@Override
	public void addList(List<?> allUsers) {		
		Iterator<NearbyUser> it = (Iterator<NearbyUser>) allUsers.iterator();
		while(it.hasNext() )
		{
			NearbyUser u = it.next();
			NearbyUserOverlayItem overlayItem=new NearbyUserOverlayItem(u,mMapView);
			userList.add(overlayItem);
			populate();
		}	    
		
	}

	@Override
	protected NearbyUserOverlayItem createItem(int i) {
		return userList.get(i);
	}

	@Override
	public int size() {
		return userList.size();
	}

	public void removeAllSmallViews()
	{
		if(size()==0)
			return;
		Iterator<NearbyUserOverlayItem> it = (Iterator<NearbyUserOverlayItem>) userList.iterator();
		while(it.hasNext() )
		{
			it.next().removeSmallView();			
		}
	}
	
	public void removeAllExpandedViews()
	{
		if(size()==0)
			return;
		Iterator<NearbyUserOverlayItem> it = (Iterator<NearbyUserOverlayItem>) userList.iterator();
		while(it.hasNext() )
		{
			it.next().removeExpandedView();			
		}
	}
	
	public void removeExpandedShowSmallViews()
	{
		if(size()==0)
			return;
		Iterator<NearbyUserOverlayItem> it = (Iterator<NearbyUserOverlayItem>) userList.iterator();
		while(it.hasNext() )
		{
			it.next().showSmallIfExpanded();			
		}
	}
	
	
	
	protected boolean onTap(int i)
	{
		//on tap check if user logged in to fb
		userList.get(i).toggleSmallView();	
		MapListActivityHandler.getInstance().centreMapTo(userList.get(i).getGeoPoint());
		return true;
		
	}
	

	
	
}
