package in.co.hopin.Fragments;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.hopin.R;
import in.co.hopin.Activities.FBLoggableFragmentActivity;
import in.co.hopin.Activities.SelfProfileActivity;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.Logger;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SelfAboutMeFrag extends Fragment{
	
	 private static final String TAG = "in.co.hopin.Fragments.SelfAboutMeFrag";
	 TextView nameTextView;
	 TextView worksAtTextView;
	 TextView homeTownTextView;
	 TextView educationTextView;
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);	      
	 }
	 	 
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 ViewGroup aboutMeView;
		 if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			{
			 //user not logged in. return no data layout
			 aboutMeView = (ViewGroup) inflater.inflate(
		                R.layout.nodata_layout, container, false);			 
			 TextView nodataTextView = (TextView) aboutMeView.findViewById(R.id.nodata_layout_textview);
			 nodataTextView.setText("No Data, please login");     			
			//Logger.i(TAG,"self profile click but not fb logged in");			
			//CommunicationHelper.getInstance().FBLoginpromptPopup_show((FBLoggableFragmentActivity)getActivity(), true) ;
				
			 return aboutMeView;
			}
		 
		 	//user is logged in 
	        aboutMeView = (ViewGroup) inflater.inflate(
	                R.layout.self_profile_aboutme, container, false);
	        String name = ThisUserNew.getInstance().getUserFBInfo().getFullName();
	        String worksat = ThisUserNew.getInstance().getUserFBInfo().getWorksAt();
	        String hometown = ThisUserNew.getInstance().getUserFBInfo().getHometown();
	        String education = ThisUserNew.getInstance().getUserFBInfo().getStudiedAt();
	        
	        //nameTextView = (TextView) aboutMeView.findViewById(R.id.self_aboutme_name);
	        worksAtTextView = (TextView) aboutMeView.findViewById(R.id.self_aboutme_worksat);
	        homeTownTextView = (TextView) aboutMeView.findViewById(R.id.self_aboutme_hometown);
	        educationTextView = (TextView) aboutMeView.findViewById(R.id.self_aboutme_education);
	        
	        //nameTextView.setText(name);
	        worksAtTextView.setText(worksat);
	        homeTownTextView.setText(hometown);
	        educationTextView.setText(education);
	        return aboutMeView;
	    }

}
