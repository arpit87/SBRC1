package in.co.hopin.Adapter;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Users.UserLocInfo;
import in.co.hopin.Users.UserOtherInfo;
import in.co.hopin.Util.StringUtils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyUsersListViewAdapter extends BaseAdapter{

	List mNearbyUsers;
	Activity underLyingActivity;
	private static LayoutInflater inflater=null;
	public NearbyUsersListViewAdapter(Activity activity,List<NearbyUser> nearbyUsers)
	{
		underLyingActivity = activity;
		mNearbyUsers = nearbyUsers;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNearbyUsers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void clear()
	{		
		mNearbyUsers.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NearbyUser thisUser = (NearbyUser) mNearbyUsers.get(position);
		View thisUserView=convertView;
        if( position == mNearbyUsers.size()-1)
        	thisUserView = inflater.inflate(R.layout.nearbyuser_list_lastrow, null);
        else        		
        	thisUserView = inflater.inflate(R.layout.nearbyuser_list_row, null);
        
        ImageView userImageView = (ImageView)thisUserView.findViewById(R.id.nearbyuser_list_row_image);
        
        ImageView chatDotOffline = (ImageView)thisUserView.findViewById(R.id.nearbyuser_list_row_chatstatus_offline);
        
        TextView userName = (TextView)thisUserView.findViewById(R.id.nearbyuser_list_row_nearbyusername);
        TextView mutualFriends = (TextView)thisUserView.findViewById(R.id.nearbyuser_list_row_mutualfriends);
        TextView userSource = (TextView)thisUserView.findViewById(R.id.nearbyuser_list_row_source);
        TextView userDestination = (TextView)thisUserView.findViewById(R.id.nearbyuser_list_row_destination);
        TextView userTime = (TextView)thisUserView.findViewById(R.id.nearbyuser_list_row_time);        
        SBImageLoader.getInstance().displayImageElseStub(thisUser.getUserFBInfo().getImageURL(), userImageView, R.drawable.userpicicon);
        final UserFBInfo thisUserFBInfo = thisUser.getUserFBInfo();
        UserLocInfo thisUserLocInfo = thisUser.getUserLocInfo();
        UserOtherInfo thisUserOtherInfo = thisUser.getUserOtherInfo();
        String name = thisUserFBInfo.getFullName();
        if(StringUtils.isBlank(name))
        	name = thisUserOtherInfo.getUserName();
        String source = thisUserLocInfo.getUserSrcAddress();
        String destination = thisUserLocInfo.getUserDstAddress();
        String formattedTravelInfo = thisUserLocInfo.getFormattedTimeDetails(ThisUserNew.getInstance().get_Daily_Instant_Type());
        userName.setText(name);
        String mutual_friends = Integer.toString(thisUserFBInfo.getNumberOfMutualFriends());        
        mutualFriends.setText(StringUtils.getSpannedText(mutual_friends,"mutual friends"));
        userSource.setText(StringUtils.getSpannedTextUptoChar(",",source,2));
        //userDestination.setText(StringUtils.getSpannedText("To:", destination));
       // userTime.setText(StringUtils.getSpannedText("At:",formattedTravelInfo));
        userDestination.setText(StringUtils.getSpannedTextUptoChar(",",destination,2));
        userTime.setText(StringUtils.getSpannedTextUptoChar(",",formattedTravelInfo,1));
        if(thisUserOtherInfo.isOnline())        	
        {
        	chatDotOffline.setVisibility(View.INVISIBLE);
        }
		return thisUserView;
	}	
	

}
