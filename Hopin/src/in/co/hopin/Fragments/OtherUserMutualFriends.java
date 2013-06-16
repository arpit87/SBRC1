package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Adapter.MutualFriendAdapter;
import in.co.hopin.Users.UserFBInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class OtherUserMutualFriends extends Fragment{
	
	 UserFBInfo mNearbyUserFbInfo ;
	 ListView mListView;
	 MutualFriendAdapter mAdapter;
	 
	 public OtherUserMutualFriends(UserFBInfo userFBInfo) {
		 mNearbyUserFbInfo = userFBInfo;		 
	}

	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup mutualFriendView = (ViewGroup) inflater.inflate(
	                R.layout.mutual_friends_layout, container, false);
	        mListView = (ListView) mutualFriendView.findViewById(R.id.mutual_friends_Listview);
	        mAdapter = new MutualFriendAdapter(getActivity(), mNearbyUserFbInfo.getMutualFriends());
	        mListView.setAdapter(mAdapter);
	        return mutualFriendView;
	    }

}
