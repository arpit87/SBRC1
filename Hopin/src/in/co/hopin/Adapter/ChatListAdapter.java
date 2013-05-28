package in.co.hopin.Adapter;

import in.co.hopin.HelperClasses.ActiveChat;
import in.co.hopin.HelperClasses.SBImageLoader;

import java.util.List;

import in.co.hopin.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatListAdapter  extends BaseAdapter{
	
	private LayoutInflater inflater= null;
	List<ActiveChat> mchatUsers;

	public ChatListAdapter(Activity activity,List<ActiveChat> chatUserlist)
	{
		inflater= (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mchatUsers = chatUserlist;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mchatUsers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mchatUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View thisUserRow=convertView;
        if(thisUserRow==null)
		   thisUserRow = inflater.inflate(R.layout.chatlist_row, null);
        ImageView userImageView = (ImageView)thisUserRow.findViewById(R.id.chatlist_image);                
        TextView userName = (TextView)thisUserRow.findViewById(R.id.chatlist_name);
        TextView lastChatMsgView = (TextView)thisUserRow.findViewById(R.id.chatlist_lastchat);
        
        String fbid = mchatUsers.get(position).getUserId();
        String name = mchatUsers.get(position).getName();
        String lastChat = mchatUsers.get(position).getLastMessage();
        String imageurl = "http://graph.facebook.com/" + fbid + "/picture?type=small"; 

        SBImageLoader.getInstance().displayImageElseStub(imageurl, userImageView, R.drawable.userpicicon);
        userName.setText(name);
        lastChatMsgView.setText(lastChat);
        
		return thisUserRow;
	}

}
