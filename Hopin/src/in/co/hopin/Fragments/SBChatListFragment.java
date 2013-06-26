package in.co.hopin.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import in.co.hopin.R;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.ChatListAdapter;
import in.co.hopin.Adapter.NearbyUsersListViewAdapter;
import in.co.hopin.HelperClasses.ActiveChat;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;

import java.util.List;

public class SBChatListFragment extends ListFragment {
	
	private static final String TAG = "in.co.hopin.Fragments.SBChatListFragment";
	private ViewGroup mListViewContainer;
	private List<ActiveChat> chatUserlist = null;
	ChatListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
		//update listview
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"on create list view");
        chatUserlist = ActiveChat.getActiveChats();  
        if(!chatUserlist.isEmpty())
        {
			mAdapter = new ChatListAdapter(getActivity(), chatUserlist);
			setListAdapter(mAdapter);
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"chatlist users:"+chatUserlist.toString());
        }
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"oncreateview chatlistview");		
		mListViewContainer = (ViewGroup) inflater.inflate(R.layout.chatfragment_listview, null);
		TextView mEmptyListTextView = (TextView)mListViewContainer.findViewById(R.id.chatlist_fragment_emptyList);
		if(chatUserlist.isEmpty())
		{
			mEmptyListTextView.setVisibility(View.VISIBLE);
		} 
		return mListViewContainer;
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		ActiveChat clickedUser = chatUserlist.get(position);
		String fbid = clickedUser.getUserId();
		String name = clickedUser.getName();
		CommunicationHelper.getInstance().onChatClickWithUser(fbid,name);
		
    }
	
}	
