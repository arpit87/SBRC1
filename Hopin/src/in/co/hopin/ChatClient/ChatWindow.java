package in.co.hopin.ChatClient;

import in.co.hopin.R;
import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.ChatService.IChatAdapter;
import in.co.hopin.ChatService.IChatManager;
import in.co.hopin.ChatService.IXMPPAPIs;
import in.co.hopin.ChatService.Message;
import in.co.hopin.ChatService.SBChatService;
import in.co.hopin.HelperClasses.ActiveChat;
import in.co.hopin.HelperClasses.BlockedUser;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.GetOtherUserProfileAndShowPopup;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Users.CurrentNearbyUsers;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class ChatWindow extends SherlockFragmentActivity{
	
	private static String TAG = "in.co.hopin.ChatClient.ChatWindow";
	public static String PARTICIPANT = "participant";
	public static String TRAVELINFO = "travel_info";
	public static String DAILYINSTATYPE = "daily_insta";
	public static String PARTICIPANT_NAME = "name";	
	private IXMPPAPIs xmppApis = null;
	  
    private ListView mMessagesListView;
    private EditText mInputField;
    private Button mSendButton; 
    
    private Menu mMenu;
    private IChatAdapter chatAdapter;
    private IChatManager mChatManager;   
    private IMessageListener mMessageListener = new SBOnChatMessageListener();
   
    private final ChatServiceConnection mChatServiceConnection = new ChatServiceConnection();
    private String mParticipantFBID = "";
    private String mParticipantName = "";       
    private String mParticipantImageURL = ""; 
    Handler mHandler = new Handler();
    private SBChatListViewAdapter mMessagesListAdapter = new SBChatListViewAdapter(this);
    private boolean mBinded = false;
    private String mThiUserChatUserName = "";
    private String mThisUserChatPassword = "";
    private String mThisUserChatFullName =  "";
	private ProgressDialog progressDialog;		
    private NotificationManager notificationManager;
    private boolean mFBLoggedIn = false;
    ActionBar ab;

    private boolean fbloginPromptIsShowing = false;
    
		    
	    @Override
		public void onCreate(Bundle savedInstanceState) {	    	
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.chatwindow);
		ab= getSupportActionBar();
        
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_black));
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);       
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        	  
	    mMessagesListView = (ListView) findViewById(R.id.chat_messages);
	    mMessagesListView.setAdapter(mMessagesListAdapter);
	    mInputField = (EditText) findViewById(R.id.chat_input);		
		mInputField.requestFocus();
		mSendButton = (Button) findViewById(R.id.chat_send_message);		
		mSendButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	HopinTracker.sendEvent("ChatWindow","ButtonClick","chatwindow:click:send",1L);
		    	if(BlockedUser.isUserBlocked(mParticipantFBID))
		    		buildUnblockAlertMessageToUnblock(mParticipantFBID);
		    	else
		    		sendMessage();
		    }
		});	
		
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);   
        
}
	    
	    


@Override
public void onResume() {
	super.onResume();
	//set participant before binding	
	mParticipantFBID = getIntent().getStringExtra(PARTICIPANT);	
    if(StringUtils.isBlank(mParticipantFBID))
	  return;
    notificationManager.cancel(mParticipantFBID.hashCode());
	mParticipantName = getIntent().getStringExtra(PARTICIPANT_NAME);
	ab.setTitle(mParticipantName);
	//mContactNameTextView.setText(mParticipantName);
	mParticipantImageURL = "http://graph.facebook.com/" + mParticipantFBID + "/picture?type=small";
	mMessagesListAdapter.clearList();
	mMessagesListAdapter.setParticipantFBURL(mParticipantImageURL);	
	//mContactNameTextView.setText(mReceiver);
	//getParticipantInfoFromFBID(mParticipantFBID);
	mFBLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER);
	mThiUserChatUserName = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
	mThisUserChatPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
	mThisUserChatFullName = ThisUserConfig.getInstance().getString(ThisUserConfig.FB_FULLNAME);
	if (!mBinded) 
		bindToService();
	else
		try {			
				changeCurrentChat();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
}

    @Override
    protected void onPause() {
	super.onPause();
	
	    if (chatAdapter != null) {
	    	try {
				chatAdapter.setOpen(false);
				Logger.i(TAG, "closed chat of:"+chatAdapter.getParticipant());
				List<Message> chatMessages = chatAdapter.getMessages();
                if (chatMessages != null && !chatMessages.isEmpty()) {
				    Message lastmMessage = chatMessages.get(chatMessages.size()-1);
				    ActiveChat.addChat(mParticipantFBID, mParticipantName, lastmMessage.getBody());
                }
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	  
	    }    
	   
    }
    
    @Override
    public void onDestroy() {
    	
    	super.onDestroy();
    	if (mBinded) {
    		releaseService();
    	    mBinded = false;
    	}
    	xmppApis = null;	
    	chatAdapter = null;
    	mChatManager = null;    	
    }   
    
    
    @Override
	public void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);	
    }
    
    @Override
    public void onStart(){
        super.onStart();
        HopinTracker.sendView("ChatWindow");
        HopinTracker.sendEvent("ChatWindow","ScreenOpen","chatwindow:open",1L);
        //EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        if(BlockedUser.isUserBlocked(mParticipantFBID))
		{
        	mMenu.getItem(mMenu.size()-1).setTitle("UnBlock");
		}
        else
        {
        	mMenu.getItem(mMenu.size()-1).setTitle("Block");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	HopinTracker.sendEvent("ChatWindow","MenuOpen","chatwindowmenu:open",1L);
        switch (menuItem.getItemId())
        {
        case R.id.chat_menu_block:        	
        	if(BlockedUser.isUserBlocked(mParticipantFBID))
			{
        		HopinTracker.sendEvent("ChatWindow","MenuClick","chatwindowmenu:click:unblock",1L);
				BlockedUser.deleteFromList(mParticipantFBID);
				Toast.makeText(ChatWindow.this,mParticipantName + " unblocked", Toast.LENGTH_SHORT).show();
				menuItem.setTitle("Block");
			}
			else
			{
				HopinTracker.sendEvent("ChatWindow","MenuClick","chatwindowmenu:click:block",1L);
				BlockedUser.addtoList(mParticipantFBID, mParticipantName);
		        Toast.makeText(ChatWindow.this,mParticipantName + " blocked", Toast.LENGTH_SHORT).show();
		        menuItem.setTitle("UnBlock");
			}
        	break;  
        case R.id.chat_menu_hopin_profile:
        	HopinTracker.sendEvent("ChatWindow","MenuClick","chatwindowmenu:click:hopinprofile",1L);
        	NearbyUser n = CurrentNearbyUsers.getInstance().getNearbyUserWithFBID(mParticipantFBID);        	
        	if(n!=null)
        	{
	        	Intent hopinNewProfile = new Intent(getApplicationContext(),OtherUserProfileActivityNew.class);
		    	hopinNewProfile.putExtra("fb_info", n.getUserFBInfo().getJsonObj().toString());
		    	hopinNewProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    	startActivity(hopinNewProfile);
        	}
        	else
        	{	        	
	        	ProgressHandler.showInfiniteProgressDialoge(ChatWindow.this, "Fetching user profile", "Please wait..");
		    	GetOtherUserProfileAndShowPopup req = new GetOtherUserProfileAndShowPopup(mParticipantFBID);
				SBHttpClient.getInstance().executeRequest(req);	
        	}
			break;
        case R.id.chat_menu_fb_profile:	
        	HopinTracker.sendEvent("ChatWindow","MenuClick","chatwindowmenu:click:fbprofile",1L);
        	CommunicationHelper.getInstance().onFBIconClickWithUser(this, mParticipantFBID, mParticipantName);
        	break;
        default:
        	break;     
        } 
        return super.onOptionsItemSelected(menuItem);
    }   
    
    private void bindToService() {
            if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "binding chat to service" );        
        	
           Intent i = new Intent(getApplicationContext(),SBChatService.class);
          
           getApplicationContext().bindService(i, mChatServiceConnection, BIND_AUTO_CREATE);	  
           mBinded = true;
        
   }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
	    
	    private void releaseService() {
    		if(mChatServiceConnection != null) {
    			getApplicationContext().unbindService(mChatServiceConnection);    			   			
    			if (Platform.getInstance().isLoggingEnabled()) Log.d( TAG, "chat Service released from chatwindow" );
    		} else {
    			//ToastTracker.showToast("Cannot unbind - service not bound", Toast.LENGTH_SHORT);
    		}
	    }
	    
		    
	    public String getParticipantFBID() {
			return mParticipantFBID;
		}

	    
	    
		private void sendMessage() {
		final String inputContent = mInputField.getText().toString();	
		SBChatMessage lastMessage = null;
		if(!mFBLoggedIn)
		{
			//check the status again..this is imp as by now it could have logged in
			mFBLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER);
			mThiUserChatUserName = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
			mThisUserChatPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);			
			
			Logger.i(TAG,"chat sent click but not fb logged in");			
			CommunicationHelper.getInstance().FBLoginDialog_show(ChatWindow.this); 					
		}
		else if(!"".equals(inputContent))
		{
			Message newMessage = new Message(mParticipantFBID);
			newMessage.setBody(inputContent);
			newMessage.setFrom(mThiUserChatUserName+"@"+ServerConstants.CHATSERVERIP);			
			newMessage.setTo(mParticipantFBID+"@"+ServerConstants.CHATSERVERIP);
			newMessage.setSubject(mThisUserChatFullName);			
			newMessage.setUniqueMsgIdentifier(System.currentTimeMillis());	
			newMessage.setTimeStamp(StringUtils.gettodayDateInFormat("hh:mm"));
			newMessage.setStatus(SBChatMessage.SENDING);							
			mMessagesListAdapter.addMessage(new SBChatMessage(mThiUserChatUserName, mParticipantFBID,inputContent, false, StringUtils.gettodayDateInFormat("hh:mm"),
					                                          SBChatMessage.SENDING,newMessage.getUniqueMsgIdentifier()));
			mMessagesListAdapter.notifyDataSetChanged();
			
		  //send msg to xmpp
			 try {
				if (chatAdapter == null) {										
					chatAdapter = mChatManager.createChat(mParticipantFBID, mMessageListener);					
				}
				if(chatAdapter != null)
				{
					chatAdapter.setOpen(true);
					Logger.i(TAG, "open chat is of:"+chatAdapter.getParticipant());
					chatAdapter.sendMessage(newMessage);
				}
				
			    } catch (RemoteException e) {
			    	sendingFailed(lastMessage);
				if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
			    }
		   
		}			   
		    mInputField.setText(null);
		}
	    
	    private void sendingFailed(SBChatMessage lastMessage)
	    {
	    	lastMessage = (SBChatMessage) mMessagesListAdapter.getItem(mMessagesListAdapter.getCount() - 1);
	    	lastMessage.setStatus(SBChatMessage.SENDING_FAILED); 
	    	mMessagesListAdapter.setMessage(mMessagesListAdapter.getCount() - 1, lastMessage);
	    	mMessagesListAdapter.notifyDataSetChanged();
	    }
	  	
	    //in already open chatWindow this function switches chats
	    private void changeCurrentChat() throws RemoteException {
	    	
	    	chatAdapter = mChatManager.getChat(mParticipantFBID);
	    	if (chatAdapter != null) {
	    		chatAdapter.setOpen(true);
	    		Logger.i(TAG, "open chat is of:"+chatAdapter.getParticipant());
	    		chatAdapter.addMessageListener(mMessageListener);
	    		fetchPastMsgsIfAny();
	    	}
	    	//getParticipantInfoFromFBID(participant);	    	
	    	
	        }
	    
	    /**
	     * Get all messages from the current chat and refresh the activity with them.
	     * @throws RemoteException If a Binder remote-invocation error occurred.
	     */
	private void fetchPastMsgsIfAny() throws RemoteException {
		mMessagesListAdapter.clearList();		
		if (chatAdapter != null) {
			List<Message> chatMessages = chatAdapter.getMessages();
		Logger.i(TAG, "tryin to fetch past msgs");
		if (chatMessages.size() > 0) {
			List<SBChatMessage> msgList = convertMessagesList(chatMessages);
			mMessagesListAdapter.addAllToList(msgList);
			Logger.d(TAG,"list adapter size in fetch past"	+ mMessagesListAdapter.getCount());
			mMessagesListAdapter.notifyDataSetChanged();
			mMessagesListView.setSelection(mMessagesListView.getCount() - 1);
		}
		}
	}
	    

	    /**
	     * Convert a list of Message coming from the service to a list of MessageText that can be displayed in UI.
	     * @param chatMessages the list of Message
	     * @return a list of message that can be displayed.
	     */
	    private List<SBChatMessage> convertMessagesList(List<Message> chatMessages) {
		List<SBChatMessage> result = new ArrayList<SBChatMessage>(chatMessages.size());		
		SBChatMessage lastMessage = null;		
		for (Message m : chatMessages) {
		    		    
		    if (m.getType() == Message.MSG_TYPE_CHAT) {	
			
			if (m.getBody() != null) {
			    if (lastMessage == null ) {
					lastMessage = new SBChatMessage(m.getInitiator(), m.getReceiver(), m.getBody(), false, m.getTimestamp(),m.getStatus(),m.getUniqueMsgIdentifier());
					if(m.getStatus() == SBChatMessage.DELIVERED || m.getStatus()==SBChatMessage.RECEIVED)
		    			lastMessage.setStatus(SBChatMessage.OLD);
					result.add(lastMessage);
			    } 
			    else if(m.getInitiator().equals(lastMessage.getInitiator()) && 
			    		lastMessage.getStatus() == SBChatMessage.OLD &&
			    		(m.getStatus() == SBChatMessage.DELIVERED || m.getStatus()==SBChatMessage.RECEIVED))
		    	{			    	
	    			lastMessage.setMessage(lastMessage.getMessage().concat("\n" + m.getBody()));	    			
		    		lastMessage.setTimestamp(m.getTimestamp());
		    	}
		    	else
		    	{			    		
		    		lastMessage = new SBChatMessage(m.getInitiator(), m.getReceiver(), m.getBody(), false, m.getTimestamp(),m.getStatus(),m.getUniqueMsgIdentifier());
		    		if(m.getStatus() == SBChatMessage.DELIVERED || m.getStatus()==SBChatMessage.RECEIVED)
		    			lastMessage.setStatus(SBChatMessage.OLD);
		    		result.add(lastMessage);
		    	}
			    }			
		    }
		    
		}
		return result;
	    }
	    
	    public void initializeChatWindow() {
	    	
	           	
	    	if(mChatManager == null)
			{
				try {
					mChatManager = xmppApis.getChatManager();
    			if (mChatManager != null) {
    				Logger.d(TAG, "Chat manager got");
    				chatAdapter = mChatManager.createChat(mParticipantFBID, mMessageListener);
    				if(chatAdapter!=null)
    				{
						chatAdapter.setOpen(true);
						Logger.i(TAG, "open chat is of:"+chatAdapter.getParticipant());
						fetchPastMsgsIfAny();
    				}
    			   // mChatManager.addChatCreationListener(mChatManagerListener);
    			    //changeCurrentChat(thisUserID);
    			}
    			else
    			{	if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Chat manager not got,will try login");
    				CommunicationHelper.getInstance().FBLoginpromptPopup_show(ChatWindow.this, true);  	
    				//loginWithProgress();
    			}
    		    } catch (RemoteException e) {
    			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
    		    }   
			}		
	    	
	          
	    	
	    }
	    
	    
	    private final class ChatServiceConnection implements ServiceConnection{
	    	
	    	@Override
	    	public void onServiceConnected(ComponentName className, IBinder boundService) {
	    		//ToastTracker.showToast("onServiceConnected called", Toast.LENGTH_SHORT);
	    		if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG,"onServiceConnected called");
	    		xmppApis = IXMPPAPIs.Stub.asInterface((IBinder)boundService);	    		
	    		try {
					xmppApis.loginAsync(mThiUserChatUserName, mThisUserChatPassword);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		initializeChatWindow();    	
	    		if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG,"service connected");
	    	}

	    	@Override
	    	public void onServiceDisconnected(ComponentName arg0) {
	    		//ToastTracker.showToast("onService disconnected", Toast.LENGTH_SHORT);
	    		xmppApis = null;   		
	    	    
	    		if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG,"service disconnected");
	    	}

	    } 
	    
		private void buildUnblockAlertMessageToUnblock(final String fbid) {
	        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("Do you really want to unblock "+ mParticipantName + "?")
	                .setCancelable(false)
	                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                    public void onClick(final DialogInterface dialog, final int id) {
	                        BlockedUser.deleteFromList(fbid);
	                        sendMessage();
	                    }
	                })
	                .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                    public void onClick(final DialogInterface dialog, final int id) {
	                        dialog.cancel();
	                    }
	                });
	        final AlertDialog alert = builder.create();
	        alert.show();
	    }
	    
 
//this is callback method executed on client when ChatService receives a message	
private class SBOnChatMessageListener extends IMessageListener.Stub {
	//this method appends to current chat, we open new chat only on notification tap or user taps on list
	//i.e. we open new chat window only on intent
	@Override
	public void processMessage(final IChatAdapter chatAdapter, final Message msg)
			throws RemoteException {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Logger.i(TAG, "Msg of type :" + msg.getType() + " received.");

                if (msg.getType() == Message.MSG_TYPE_ACKFOR_DELIVERED) {
                    //here we should receive acks of only open chats
                    //non open chats ack update msgs in list of theie respective chatAdapter and user when next opens them
                    //he fetches all the msgs which have been updated in adapter.
                    mMessagesListAdapter.updateMessageStatusWithUniqueID(msg.getUniqueMsgIdentifier(), SBChatMessage.DELIVERED);
                } else if (msg.getType() == Message.MSG_TYPE_ACKFOR_BLOCKED) {
                    //here we should receive acks of only open chats
                    //non open chats ack update msgs in list of theie respective chatAdapter and user when next opens them
                    //he fetches all the msgs which have been updated in adapter.
                    mMessagesListAdapter.updateMessageStatusWithUniqueID(msg.getUniqueMsgIdentifier(), SBChatMessage.BLOCKED);
                } else if (msg.getType() == Message.MSG_TYPE_ACKFOR_SENT) {
                    //here we should receive acks of only open chats
                    //non open chats ack update msgs in list of theie respective chatAdapter and user when next opens them
                    //he fetches all the msgs which have been updated in adapter.
                    mMessagesListAdapter.updateMessageStatusWithUniqueID(msg.getUniqueMsgIdentifier(), SBChatMessage.SENT);
                }  else if (msg.getType() == Message.MSG_TYPE_CHAT) {
                    //here we can get one type of chat msg                    
                    //1) incoming msg from other user
                     if (msg.getBody() != null) {
                            //incomiing added in chatadapter
                            //ActiveChat.addChat(mParticipantFBID, mThisUserChatFullName, msg.getBody());
                            SBChatMessage lastMessage = null;

                            if (mMessagesListAdapter.getCount() != 0)
                                lastMessage = (SBChatMessage) mMessagesListAdapter.getItem(mMessagesListAdapter.getCount() - 1);

                            if (lastMessage != null && !lastMessage.getInitiator().equals(mThiUserChatUserName)) {
                                lastMessage.setMessage(lastMessage.getMessage().concat("\n" + msg.getBody()));
                                lastMessage.setTimestamp(msg.getTimestamp());
                                mMessagesListAdapter.setMessage(mMessagesListAdapter.getCount() - 1, lastMessage);

                            } else if (msg.getBody() != null) {
                                mMessagesListAdapter.addMessage(new SBChatMessage(msg.getInitiator(), msg.getReceiver(), msg.getBody(), false, msg.getTimestamp(), msg.getStatus(), msg.getUniqueMsgIdentifier()));
                            }

                        }
                    
                }

                Logger.i(TAG, "Notifying adapter.");
                mMessagesListAdapter.notifyDataSetChanged();
            }
        });


    }
}

private class SBChatServiceConnAndMiscListener extends ISBChatConnAndMiscListener.Stub{

	@Override
	public void loggedIn() throws RemoteException {
		if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Chat window login call back");		
		if(mChatManager == null)
			mChatManager = xmppApis.getChatManager();
		
		if(mChatManager == null)
		{			
			if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Chat window login call back,logged in but still didnt find chat manager");
		}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Chat window login call back,logged in and found chat manager");
        }
	}

	@Override
	public void connectionClosed() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosedOnError() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconnectingIn(int seconds) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconnectionFailed() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconnectionSuccessful() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionFailed(String errorMsg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
}

public boolean isFbloginPromptIsShowing() {
	return fbloginPromptIsShowing;
}




public void setFbloginPromptIsShowing(boolean fbloginPromptIsShowing) {
	this.fbloginPromptIsShowing = fbloginPromptIsShowing;
}



	    
}
