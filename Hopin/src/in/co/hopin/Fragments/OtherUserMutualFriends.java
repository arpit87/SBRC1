package in.co.hopin.Fragments;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.hopin.R;
import in.co.hopin.Adapter.MutualFriendAdapter;
import in.co.hopin.Users.UserFBInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class OtherUserMutualFriends extends Fragment{
	
	 UserFBInfo mNearbyUserFbInfo ;
	 ListView mListView;
	 MutualFriendAdapter mAdapter;
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState){
	        super.onCreate(savedInstanceState);
	        Bundle data = getArguments();
	        String fb_info = data.getString("fb_info");
	        JSONObject fbJson = null;
			try {
				fbJson = new JSONObject(fb_info);
			} catch (JSONException e) {
				// in case fbinfo is malformed
				try {
					fbJson = new JSONObject("{\"fb_info_available\":0}");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
	        mNearbyUserFbInfo = new UserFBInfo(fbJson);
	 }

	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup mutualFriendView = (ViewGroup) inflater.inflate(
	                R.layout.mutual_friends_layout, container, false);
	        mListView = (ListView) mutualFriendView.findViewById(R.id.mutual_friends_Listview);
	        if(mNearbyUserFbInfo.getNumberOfMutualFriends()>0)
	        {	        	
	        	mAdapter = new MutualFriendAdapter(getActivity(), mNearbyUserFbInfo.getMutualFriends());
	        	mListView.setAdapter(mAdapter);
	        }
	        else
	        {
	        	TextView noMutualFriends = (TextView)mutualFriendView.findViewById(R.id.mutual_friends_emptyList);
	        	mListView.setVisibility(View.GONE);
	        	noMutualFriends.setVisibility(View.VISIBLE);
	        }
	        return mutualFriendView;
	    }

}
