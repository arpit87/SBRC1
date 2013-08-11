package in.co.hopin.MapHelpers;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.Users.NearbyUserGroup;
import in.co.hopin.Util.HopinTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;


public class GourpedNearbyUsersIteamizedOverlay extends BaseItemizedOverlay{

	ArrayList<GroupedNearbyUsersOverlayItem> groupList=new ArrayList<GroupedNearbyUsersOverlayItem>();
	private SBMapView mMapView = null;
	private static Context context = MapListActivityHandler.getInstance().getUnderlyingActivity();
	
	public GourpedNearbyUsersIteamizedOverlay(SBMapView mapView) {
		super(boundCenter(context.getResources().getDrawable(R.drawable.map_dp_frame_shadow)));
		this.mMapView = mapView;
	}
	
	public GourpedNearbyUsersIteamizedOverlay() {
		super(boundCenter(context.getResources().getDrawable(R.drawable.map_dp_frame_shadow)));
		}
	
	@Override
	public void addList(List<?> groups) {		
		Iterator<NearbyUserGroup> iterator_groups = (Iterator<NearbyUserGroup>) groups.iterator();
		while(iterator_groups.hasNext() )
		{
			NearbyUserGroup thisGroup = iterator_groups.next();
			GroupedNearbyUsersOverlayItem overlayItem=new GroupedNearbyUsersOverlayItem(thisGroup,mMapView);
			groupList.add(overlayItem);
			populate();
		}	    
		
	}

	@Override
	protected GroupedNearbyUsersOverlayItem createItem(int i) {
		return groupList.get(i);
	}

	@Override
	public int size() {
		return groupList.size();
	}

	public void removeAllSmallViews()
	{
		if(size()==0)
			return;
		Iterator<GroupedNearbyUsersOverlayItem> it = (Iterator<GroupedNearbyUsersOverlayItem>) groupList.iterator();
		while(it.hasNext() )
		{
			it.next().removeSmallView();			
		}
	}
	
	public void removeAllExpandedViews()
	{
		if(size()==0)
			return;
		Iterator<GroupedNearbyUsersOverlayItem> it = (Iterator<GroupedNearbyUsersOverlayItem>) groupList.iterator();
		while(it.hasNext() )
		{
			it.next().removeExpandedView();			
		}
	}
	
	public void removeExpandedShowSmallViews()
	{
		if(size()==0)
			return;
		Iterator<GroupedNearbyUsersOverlayItem> it = (Iterator<GroupedNearbyUsersOverlayItem>) groupList.iterator();
		while(it.hasNext() )
		{
			it.next().showSmallIfExpanded();			
		}
	}
	
	
	
	protected boolean onTap(int i)
	{	
		groupList.get(i).toggleSmallView();	
		MapListActivityHandler.getInstance().centreMapTo(groupList.get(i).getGeopoint());
		return true;
		
	}
	

	
	
}
