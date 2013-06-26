package in.co.hopin.Adapter;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Users.Friend;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MutualFriendAdapter extends BaseAdapter{

	List mMutualFriends;
	Activity underLyingActivity;
	private static LayoutInflater inflater=null;
	public MutualFriendAdapter(Activity activity,List<Friend> friends)
	{
		underLyingActivity = activity;
		mMutualFriends = friends;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMutualFriends.size();
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
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend thisMutualFriend = (Friend) mMutualFriends.get(position);
		View thisUserView=convertView;
        if( position%2 == 0)        
        	thisUserView = inflater.inflate(R.layout.mutual_friend_row_left, null);
        else        		
        	thisUserView = inflater.inflate(R.layout.mutual_friend_row_right, null);
        
        ImageView userImageView = (ImageView)thisUserView.findViewById(R.id.mutual_friend_image);       
        TextView userName = (TextView)thisUserView.findViewById(R.id.mutual_friend_name);               
        SBImageLoader.getInstance().displayImageElseStub(thisMutualFriend.getImageURL(), userImageView, R.drawable.userpicicon);
        userName.setText(thisMutualFriend.getName());       
		return thisUserView;
	}

}
