package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.ThisUserConfig;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SelfFriends extends Fragment{
	private static final String TAG = "in.co.hopin.Fragments.SelfFriends";
	
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);	      
	 }
	 
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 ViewGroup friendsView;
		 if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			{
			 //user not logged in. return no data layout
			 friendsView = (ViewGroup) inflater.inflate(
		                R.layout.nodata_layout, container, false);
			 TextView nodataTextView = (TextView) friendsView.findViewById(R.id.nodata_layout_textview);
			 nodataTextView.setText("No Data, please login");
			 //Logger.i(TAG,"self profile click but not fb logged in");			
			 //CommunicationHelper.getInstance().FBLoginpromptPopup_show((FBLoggableFragmentActivity)getActivity(), true) ;
			 return friendsView;
			}
	        friendsView = (ViewGroup) inflater.inflate(
	                R.layout.self_profile_friends, container, false);
			return friendsView;
	 }

}
