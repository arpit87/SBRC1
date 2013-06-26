package in.co.hopin.Fragments;

import in.co.hopin.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelfFriends extends Fragment{
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);	      
	 }
	 
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup friendsView = (ViewGroup) inflater.inflate(
	                R.layout.self_profile_friends, container, false);
			return friendsView;
	 }

}
