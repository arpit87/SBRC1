package in.co.hopin.Fragments;

import in.co.hopin.Activities.MapListViewTabActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.maps.MapView;

public class SBMapFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {				
	private static final String TAG = "in.co.hopin.Fragments.SBMapFragment";
	private ViewGroup mMapViewContainer;	
	private MapView mMapView;
	private ImageButton selfLocationButton;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreate,mapview");
        MapListActivityHandler.getInstance().setMapFrag(this);
	}
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview,mapview");
		mMapViewContainer = ((MapListViewTabActivity)getActivity()).getThisMapContainerWithMapView();
		if(mMapView == null)
			mMapView = (MapView) mMapViewContainer.findViewById(R.id.map_view);
		//mMapViewContainer = inflater.inflate(R.layout.map,null,false);
		return mMapViewContainer;		
	}	

	
	@Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"ondestroyview,mapview");
        ViewGroup parentViewGroup = (ViewGroup) mMapViewContainer.getParent();
		if( null != parentViewGroup ) {
			parentViewGroup.removeView( mMapViewContainer );
		}
		mMapViewContainer.removeView(mMapView);
		mMapViewContainer.removeAllViews();
    }  
	
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	
}

