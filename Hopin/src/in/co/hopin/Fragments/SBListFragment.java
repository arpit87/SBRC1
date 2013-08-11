package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.NearbyUsersListViewAdapter;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Util.HopinTracker;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class SBListFragment extends ListFragment {
	
	private static final String TAG = "in.co.hopin.Fragments.SBListFragment";
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
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"on create list view");
        nearbyUserlist = CurrentNearbyUsers.getInstance().getAllNearbyUsers();
        if(nearbyUserlist!=null)
        {
			NearbyUsersListViewAdapter adapter = new NearbyUsersListViewAdapter(getActivity(), nearbyUserlist);
			setListAdapter(adapter);
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"nearby users:"+nearbyUserlist.toString());
        }
        MapListActivityHandler.getInstance().setListFrag(this);       
	}
	
	@Override
	public void onActivityCreated(Bundle icicle) {    
		super.onActivityCreated(icicle);
	    registerForContextMenu(getListView());
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
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview listview");
		mListViewContainer=  MapListActivityHandler.getInstance().getThisListContainerWithListView();		
		return mListViewContainer;
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		HopinTracker.sendEvent("ListView","ListClick","listmatchingusers:click:listitem",1L);
		NearbyUser userAtthisPosition = CurrentNearbyUsers.getInstance().getNearbyUserAtPosition(position);
		if(userAtthisPosition != null)
			CommunicationHelper.getInstance().onChatClickWithUser(getActivity(),userAtthisPosition.getUserFBInfo().getFbid(),userAtthisPosition.getUserFBInfo().getFullName());
		else
			ToastTracker.showToast("Unable to chat,user not in current list");
        //ToastTracker.showToast("Chat with user at: " + position);
    }
	
	
	@Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"ondestroyview listview");
        ViewGroup parentViewGroup = (ViewGroup) mListViewContainer.getParent();
		if( null != parentViewGroup ) {
			parentViewGroup.removeView( mListViewContainer );
		}
		//mListViewContainer.removeAllViews();
    }  
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    HopinTracker.sendEvent("ListView","LongClick","listmatchingusers:longclick:listitem",1L);
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.listview_longclick_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {	 
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();		
		NearbyUser userAtthisPosition = CurrentNearbyUsers.getInstance().getNearbyUserAtPosition(info.position);
	    switch (item.getItemId()) {	  
	    case R.id.listview_fb_profile:
	    	HopinTracker.sendEvent("ListView","MenuClick","listmatchingusers:longclickmenu:click:fbprofile",1L);
	    	CommunicationHelper.getInstance().onFBIconClickWithUser(getActivity(), userAtthisPosition.getUserFBInfo().getFbid(), userAtthisPosition.getUserFBInfo().getFBUsername());
	    	break;
	    case R.id.listview_hopin_profile:
	    	HopinTracker.sendEvent("ListView","MenuClick","listmatchingusersmenu:longclickmenu:click:hopinprofile",1L);
	    	CommunicationHelper.getInstance().onHopinProfileClickWithUser(getActivity(), userAtthisPosition.getUserFBInfo());
	    	break;
	    }
	    return false;
	}
	

}
