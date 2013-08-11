package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

public class SearchUserInstaFrag extends AbstractSearchInputFrag{
	
	View mInstaViewContainer = null ;
	RadioGroup radio_group_time = null ;
	String mDestination = "";
	int mRadio_button_selected = 5;  //5 for 5 min,15 for 15 min,30 for 30 min	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(mInstaViewContainer==null)
        {
        	mInstaViewContainer = inflater.inflate(R.layout.search_users_insta_frag, null); 
        	destination = (AutoCompleteTextView) mInstaViewContainer.findViewById(R.id.search_user_insta_destination);
         	radio_group_time = (RadioGroup)mInstaViewContainer.findViewById(R.id.search_user_insta_radio_group);
         	cancelFindUsers = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_cancelfindusers);
            offerRideButton = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_offerride);
            takeRideButton = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_takeride);
            destination_progressbar = (ProgressBar)mInstaViewContainer.findViewById(R.id.search_users_insta_destinationprogress);
            radio_group_time.check(R.id.search_user_insta_radiobutton_5min);
        } 
		//call super at end as it uses above inflation
		super.onCreate(savedInstanceState);	
		//placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity().getApplicationContext(), R.layout.address_suggestion_layout);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, null);
        if(mInstaViewContainer==null)
        {
        	mInstaViewContainer = inflater.inflate(R.layout.search_users_insta_frag, null); 
        	destination = (AutoCompleteTextView) mInstaViewContainer.findViewById(R.id.search_user_insta_destination);
         	radio_group_time = (RadioGroup)mInstaViewContainer.findViewById(R.id.search_user_insta_radio_group);
         	takeRideButton = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_cancelfindusers);
            offerRideButton = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_offerride);
            cancelFindUsers = (Button)mInstaViewContainer.findViewById(R.id.search_usersinsta_btn_takeride);
            destination_progressbar = (ProgressBar)mInstaViewContainer.findViewById(R.id.search_users_insta_destinationprogress);
            radio_group_time.check(R.id.search_user_insta_radiobutton_5min);
        } 
        return mInstaViewContainer;
	}

	@Override
	public String getSource() {
		if(source != null)
			return source.getText().toString();
		else return "";
	}

	@Override
	public String getDestination() {
		if(destination != null)
			return destination.getText().toString();
		else return "";
	}

	@Override
	public String getDate() {		
		return StringUtils.gettodayDateInFormat("yyyy-MM-dd");
	}

	@Override
	public String getTime() {
		int checkedRadioButton = radio_group_time.getCheckedRadioButtonId();
		switch(checkedRadioButton)
		{
		case R.id.search_user_insta_radiobutton_5min:
			mRadio_button_selected = 5;
			HopinTracker.sendEvent("SearchUsers","RadioButtonClick","searchusers:insta:click:5min",1L);
			break;
		case R.id.search_user_insta_radiobutton_15min:
			mRadio_button_selected = 15;
			HopinTracker.sendEvent("SearchUsers","RadioButtonClick","searchusers:insta:click:15min",1L);
			break;
		case R.id.search_user_insta_radiobutton_30min:
			mRadio_button_selected = 30;
			HopinTracker.sendEvent("SearchUsers","RadioButtonClick","searchusers:insta:click:30min",1L);
			break;
		}
		return StringUtils.getFutureTimeInformat(mRadio_button_selected, "HH:mm");
	}

	@Override
	public int getDailyInstaType() {
		// TODO Auto-generated method stub
		return 1;
	}

	/***
	 * we set the source geoPoint to current if user chooses "my location"
	 */
	@Override
	public SBGeoPoint getSourceGeopoint() {		
		if(!sourceSet)
			return	ThisUserNew.getInstance().getCurrentGeoPoint();
		else
			return null;
	}

	@Override
	public SBGeoPoint getDestinationGeopoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPlanInstaTabType() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getRadioButtonID() {
		// TODO Auto-generated method stub
		return radio_group_time.getCheckedRadioButtonId();
	}
	
	@Override
    public void onDestroyView() {
        super.onDestroyView();       
        ViewGroup parentViewGroup = (ViewGroup) mInstaViewContainer.getParent();
		if( null != parentViewGroup ) {
			parentViewGroup.removeView( mInstaViewContainer );
		}	
    }
}
