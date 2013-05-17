package in.co.hopin.MapHelpers;

import in.co.hopin.Users.NearbyUser;

import java.util.List;



import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

public abstract class BaseItemizedOverlay extends ItemizedOverlay<BaseOverlayItem>{

	public BaseItemizedOverlay(Drawable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	
	protected abstract BaseOverlayItem createItem(int arg0);

	
	public  void addList(List <?> l ){}
	
	public  void addThisUser(){}

	public  void updateThisUser(){}	
	
	public void removeAllSmallViews(){}

	public void removeAllExpandedViews() {
				
	}

	public void removeExpandedShowSmallViews() {
				
	}
	

}
