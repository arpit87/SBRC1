package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserFBInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OtheruserAboutMeFragment extends Fragment{
	
	 UserFBInfo mNearbyUserFbInfo ;
	 TextView worksAtTextView;
	 TextView homeTownTextView;
	 TextView educationTextView;
	 
	 public OtheruserAboutMeFragment(UserFBInfo nearbyUserFbInfo)
	 {
		 mNearbyUserFbInfo = nearbyUserFbInfo;
	 }
	 
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup aboutMeView = (ViewGroup) inflater.inflate(
	                R.layout.otheruser_aboutme, container, false);
	        worksAtTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_worksat);
	        homeTownTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_hometown);
	        educationTextView = (TextView) aboutMeView.findViewById(R.id.otheruser_aboutme_education);
	        
	        worksAtTextView.setText(mNearbyUserFbInfo.getWorksAt());
	        homeTownTextView.setText(mNearbyUserFbInfo.getHometown());
	        educationTextView.setTag(mNearbyUserFbInfo.getStudiedAt());
	        return aboutMeView;
	    }

}
