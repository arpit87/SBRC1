package in.co.hopin.MapHelpers;

import in.co.hopin.Activities.SelfProfileActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.Store;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class ThisUserOverlayItem extends BaseOverlayItem{

	private static String TAG = "in.co.hopin.MapHelpers.ThisUserOverlayItem";
	
	protected SBMapView mMapView = null;
	protected static LayoutInflater mInflater;
	View viewOnMarker = null; 
	ImageView picView = null;
	SBGeoPoint mGeoPoint = null;
	String mImageURL= null;	
	boolean isVisible = false;
	String fbPicURL = null;
	
	public ThisUserOverlayItem(SBGeoPoint geoPoint, String imageURL, String arg2,SBMapView mapView) {
		super(geoPoint, imageURL, arg2);	
		this.mGeoPoint = geoPoint;		
		this.mMapView = mapView;
		this.mImageURL = imageURL;
		createAndDisplayView();
	}
	
	//enable a view to be drawn over each marker
		
	private void createAndDisplayView()
	{
		if(mMapView == null)
			return;
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mGeoPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		if(viewOnMarker==null)
		{
			mInflater = (LayoutInflater) Platform.getInstance().getContext().getSystemService(Platform.getInstance().getContext().LAYOUT_INFLATER_SERVICE);
			viewOnMarker = mInflater.inflate(R.layout.map_frame_layout_red, null);
			picView = (ImageView)viewOnMarker.findViewById(R.id.userpic);	
			fbPicURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL);
			if(fbPicURL != "")
			{
				SBImageLoader.getInstance().displayImageElseStub(fbPicURL, picView, R.drawable.userpicicon);
			}
			else
			{
				picView.setImageDrawable( Platform.getInstance().getContext().getResources().getDrawable(R.drawable.userpicicon));
			}
			mMapView.addSelfView(viewOnMarker,params);
			viewOnMarker.setVisibility(View.VISIBLE);
			viewOnMarker.setOnTouchListener(new ThisUserOnTouchListener());
			isVisible = true;
		}
		else
		{			
			viewOnMarker.setLayoutParams(params);	
			viewOnMarker.setVisibility(View.VISIBLE);
			isVisible = true;
		
		}
		
	}
	
	public void removeView()
	{
		if(viewOnMarker!=null)
		{
			viewOnMarker.setVisibility(View.GONE);
			isVisible = false;
		}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"trying to remove null thisUserMapView");
        }
	}	
	
	public void ToggleView()
	{
		if(isVisible)
			removeView();
		else
			showView();
	}
	
	public void showView()
	{
		if(viewOnMarker!=null)
		{
			viewOnMarker.setVisibility(View.VISIBLE);
			isVisible = true;
		}
		else
		{
			createAndDisplayView();
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"trying to show null thisUserMapView");
		}
	}
	
	public class ThisUserOnTouchListener implements OnTouchListener
	{		
		@Override
		public boolean onTouch(View v, MotionEvent event) {			
			MapListActivityHandler.getInstance().centreMapTo(mGeoPoint);
			Intent hopinSelfProfile = new Intent(MapListActivityHandler.getInstance().getUnderlyingActivity(),SelfProfileActivity.class);
			hopinSelfProfile.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);			
			MapListActivityHandler.getInstance().getUnderlyingActivity().startActivity(hopinSelfProfile);
			return true;
		}
		
	}
	

}
