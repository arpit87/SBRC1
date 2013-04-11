package in.co.hopin.ChatClient;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class SBChatListViewAdapter extends BaseAdapter {

	private static String TAG = "in.co.hopin.ChatClient.SBChatListViewAdapter";
	List<SBChatMessage> mListMessages = new ArrayList<SBChatMessage>();
	HashMap<Long,SBChatMessage> mHashMapSentNotDeliveredMsgs = new HashMap<Long,SBChatMessage>();
	String participantFBURL = "";
	String selfFBId = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
	String selfFirstName = ThisUserConfig.getInstance().getString(ThisUserConfig.FB_FIRSTNAME);
	String selfImageURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL);
	int chatMsgStatus = SBChatMessage.UNKNOWN ;
    private Activity activity;

    public SBChatListViewAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
	 * Returns the number of messages contained in the messages list.
	 * @return The number of messages contained in the messages list.
	 */
	@Override
	public int getCount() {
	    return mListMessages.size();	    
	}
	
	public void setMessage(int i,SBChatMessage msg) {
	     mListMessages.set(i, msg);
	}
	
	public void setParticipantFBURL(String fburl) {
		participantFBURL = fburl;
	}
	
	
	public void addMessage(SBChatMessage msg) {
	     mListMessages.add(msg);
	     //save all msgs that user is sending in a hash map to update their status
	     if(msg.getStatus() == SBChatMessage.SENDING)
	    	 mHashMapSentNotDeliveredMsgs.put(msg.getUniqueIdentifier(), msg);
	}
	
	public void updateMessageStatusWithUniqueID(long unique_ID,int status) {
		
	     SBChatMessage msg = mHashMapSentNotDeliveredMsgs.get(unique_ID);
	     if(msg!=null)
	     {
	    	 msg.setStatus(status);
	    	 if(status == SBChatMessage.DELIVERED)
	    		 mHashMapSentNotDeliveredMsgs.remove(msg);
	     }		
	}
	
	public void clearList()
	{
		mListMessages.clear();
	}
	
	public void addAllToList(List<SBChatMessage> listMessages)
	{
		mListMessages.addAll(listMessages);		
	}

	/**
	 * Return an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The item from the messages list at the requested position.
	 */
	@Override
	public Object getItem(int position) {
	    return mListMessages.get(position);
	}
	
	

	/**
	 * Return the id of an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The id of an item from the messages list at the requested position.
	 */
	@Override
	public long getItemId(int position) {
	    return position;
	}

	/**
	 * Return the view of an item from the messages list.
	 * @param position The position of the requested item.
	 * @param convertView The old view to reuse if possible.
	 * @param parent The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified position.
	 */

	public View getView(int position, View convertView, ViewGroup parent) {
	    View chatRowView;
	    TextView msgText ;
	    TextView msgStatus ;
	    ImageView imgView ;
	    String imageURL = "";
	    String statusText = "";
	    String time = "";
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);		
	    SBChatMessage msg = mListMessages.get(position);	
	    if(msg.getInitiator().equalsIgnoreCase(selfFBId))
	    {
	    	chatRowView = inflater.inflate(R.layout.chat_msg_row_my, null);	    	
	    	imageURL = selfImageURL;	   
	    	 Log.d(TAG,"self msg"+msg.getMessage());
	    }
	    else
	    {
	    	 chatRowView = inflater.inflate(R.layout.chat_msg_row_other, null);	    	 	
	 	     imageURL = participantFBURL;		    	
	 	     Log.d(TAG,"othr person msg"+msg.getMessage());
	    }  
	    	msgText = (TextView) chatRowView.findViewById(R.id.chatmessagetext);
	    	msgStatus = (TextView) chatRowView.findViewById(R.id.chatmessagestatusandtime);
	    	imgView = (ImageView) chatRowView.findViewById(R.id.chat_msg_pic);
	    	SBImageLoader.getInstance().displayImageElseStub(imageURL, imgView, R.drawable.userpicicon);
		    msgText.setText(msg.getMessage());
		    
		    if (msg.getTimestamp() != null) {
				time = msg.getTimeStamp();				
			    }
		    
		    chatMsgStatus = msg.getStatus();		    
		    switch(chatMsgStatus)
		    {
		        case SBChatMessage.SENDING_FAILED:
		    	statusText = "Sending failed";
		    	msgStatus.setTextColor(Color.RED);
		    	break;
		    	case SBChatMessage.SENDING: //sending
		    	statusText = "Sending..";
		    	break;
		    	case SBChatMessage.SENT:
		    	statusText = "Sent";		    	
		    	break;
		    	case SBChatMessage.DELIVERED:
		    	statusText = "Delivered";		    	
			    break;	
		    	case SBChatMessage.BLOCKED:
		    	statusText = "Blocked";
			    break;	
		    	case SBChatMessage.RECEIVED:
			    statusText = "@"+time;
				break;
		    	case SBChatMessage.OLD:
				statusText = "@"+time;
				break;
				default:
				statusText = "";
		    }
		    //registerForContextMenu(msgText);
		    msgStatus.setText(statusText);
		    
	    if (msg.isError()) {
		String err = "#some error occured!";
		msgText.setText(err);
		msgText.setTextColor(Color.RED);
		msgStatus.setError("");
	    }
	    return chatRowView;
	}
    }

