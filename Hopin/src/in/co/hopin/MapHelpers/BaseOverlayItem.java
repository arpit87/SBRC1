package in.co.hopin.MapHelpers;

import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public abstract class BaseOverlayItem extends OverlayItem{

	
	public BaseOverlayItem(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	
	
}
