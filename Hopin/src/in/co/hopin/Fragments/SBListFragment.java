package in.co.hopin.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.NearbyUsersListViewAdapter;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;

import java.util.List;

public class SBListFragment extends ListFragment {
	
	private static final String TAG = "my.b1701.SB.Fragments.SBListFragment";
	private ViewGroup mListViewContainer;
	private List<NearbyUser> nearbyUserlist = null;
	
	public void reset(){
        nearbyUserlist = null;
        if (getListAdapter() != null) {
            ((NearbyUsersListViewAdapter) getListAdapter()).clear();
        }
    }
    
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
		//update listview
        Log.i(TAG,"on create list view");
        nearbyUserlist = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if(nearbyUserlist!=null)
        {
			NearbyUsersListViewAdapter adapter = new NearbyUsersListViewAdapter(getActivity(), nearbyUserlist);
			setListAdapter(adapter);
			Log.i(TAG,"nearby users:"+nearbyUserlist.toString());
        }
        MapListActivityHandler.getInstance().setListFrag(this);
	}

    @Override
    public void onResume(){
        super.onResume();
        MapListActivityHandler.getInstance().updateUserNameInListView();
        MapListActivityHandler.getInstance().updateUserPicInListView();
        MapListActivityHandler.getInstance().updateSrcDstTimeInListView();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		Log.i(TAG,"oncreateview listview");
		mListViewContainer=  MapListActivityHandler.getInstance().getThisListContainerWithListView();
		return mListViewContainer;
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		NearbyUser userAtthisPosition = CurrentNearbyUsers.getInstance().getNearbyUserAtPosition(position);
		if(userAtthisPosition != null)
			CommunicationHelper.getInstance().onChatClickWithUser(userAtthisPosition.getUserFBInfo().getFbid(),userAtthisPosition.getUserFBInfo().getFullName());
		else
			ToastTracker.showToast("Unable to chat,user not in current list");
        ToastTracker.showToast("Chat with user at: " + position);
    }
	
	@Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"ondestroyview listview");
        ViewGroup parentViewGroup = (ViewGroup) mListViewContainer.getParent();
		if( null != parentViewGroup ) {
			parentViewGroup.removeView( mListViewContainer );
		}
		//mListViewContainer.removeAllViews();
    }  
	

}
