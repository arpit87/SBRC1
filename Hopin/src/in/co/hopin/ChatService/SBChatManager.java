package in.co.hopin.ChatService;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import in.co.hopin.ChatClient.IChatManagerListener;
import in.co.hopin.ChatClient.IMessageListener;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SBChatManager extends IChatManager.Stub {
	
	private ChatManager mChatManager;
	private XMPPConnection mXMPPConnection;
	private Roster mRoster;	
	private static final String TAG = "in.co.hopin.ChatService.SBChatManager";
    private final Map<String, ChatAdapter> mAllChats = new HashMap<String, ChatAdapter>();
    private final SBChatManagerAndInitialMsgListener mChatAndInitialMsgListener = new SBChatManagerAndInitialMsgListener();
    private final RemoteCallbackList<IChatManagerListener> mRemoteChatCreationListeners = new RemoteCallbackList<IChatManagerListener>();	
    private SBChatService mService = null;
      
    

	public SBChatManager(XMPPConnection xmppConnection, SBChatService service) {
		this.mXMPPConnection = xmppConnection;
		this.mChatManager = xmppConnection.getChatManager();
		this.mService = service;
		this.mRoster = xmppConnection.getRoster();				
		//this.mChatManager.addChatListener(mChatAndInitialMsgListener);
        addChatListener();

	}

    private void resetChatManager() {
        mChatManager = mXMPPConnection.getChatManager();
    }
    
    private void addChatListener() {
        mChatManager.addChatListener(mChatAndInitialMsgListener);
    }

    public synchronized void resetOnConnection() {
        resetChatManager();
        addChatListener();
        for (Map.Entry<String, ChatAdapter> entry : mAllChats.entrySet()) {
            String key = entry.getKey() + "@" + ServerConstants.CHATSERVERIP;
            Chat chat = mChatManager.createChat(key, null);
            entry.getValue().resetChatOnConnection(chat);
        }
    }

	/**
     * Get an existing ChatAdapter or create it if necessary.
     * @param chat The real instance of smack chat
     * @return a chat adapter register in the manager
     */
    private ChatAdapter getChatAdapter(Chat chat) {
	String key = StringUtils.parseName(chat.getParticipant());
	if (mAllChats.containsKey(key)) {
	    return mAllChats.get(key);
	}
	ChatAdapter newChatAdapter = new ChatAdapter(chat,this);	
	mAllChats.put(key, newChatAdapter);
	return newChatAdapter;
    }
    
    @Override
    public void deleteChatNotification(IChatAdapter chat) {
	mService.deleteNotification(1);
    }
    
    @Override
    public void addChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.register(listener);
    }
	
 @Override
    public void removeChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.unregister(listener);
    }

	@Override
	public synchronized IChatAdapter createChat(String participant, IMessageListener listener) throws RemoteException {
			String key = participant+"@"+ServerConstants.CHATSERVERIP;
			ChatAdapter chatAdapter;
			if (mAllChats.containsKey(participant)) {
				chatAdapter = mAllChats.get(participant);
				chatAdapter.addMessageListener(listener);
			    return chatAdapter;
			}
			Chat c = mChatManager.createChat(key, null);
			// maybe a little probleme of thread synchronization
			// if so use an HashTable instead of a HashMap for mChats
			chatAdapter = getChatAdapter(c);
			chatAdapter.addMessageListener(listener);
			return chatAdapter;
		    }
	
	@Override
    public synchronized ChatAdapter getChat(String participant) {
		String key = participant;
		if (mAllChats.containsKey(key)) {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"Chat returned for:"+key);
		    return mAllChats.get(key);
		}
		else
		{
			Chat c = mChatManager.createChat(key, null);
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"Chat created for:"+key);
			// maybe a little probleme of thread synchronization
			// if so use an HashTable instead of a HashMap for mChats
			return getChatAdapter(c);
		}
			
	
    }   
	
	public void notifyAllPendingQueue()
	{
		if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"notifying all pending queue");
		Collection<ChatAdapter> c = mAllChats.values();		
		Iterator it = c.iterator();
		while(it.hasNext())
		{			
			ChatAdapter ca = (ChatAdapter) it.next();
			ca.notifyMsgQueue();
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"notified a queue");
		}
			
		
	}
	
	public int numChats()
	{
		return mAllChats.size();
	}
	
	public void notifyChat(int id,String participant_fbid,String participant_name,String chat_message) { 	   			
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "Sending notification") ;
	    	mService.sendNotification(id,participant_fbid,participant_name,chat_message);
	   
	}
	
		
	private class SBChatManagerAndInitialMsgListener implements ChatManagerListener {

		/****
		 * this is initial remote msg listener registered to a newly remote created chat.It is called back only 
		 * till this window not opened by user and it keeps showing incoming msgs as notifications.
		 * as soon the user taps on notification and this chat opens we change msg listener to one with 
		 * client so that further call backs are handled by SBonChatMsgListener
		 */
		/*@Override
		public void processMessage(IChatAdapter chat,
				in.co.hopin.ChatService.Message msg) throws RemoteException {
			try {
				String body = msg.getBody();
				if (!chat.isOpen() && body != null) {
				    if (chat instanceof ChatAdapter) {
					mAllChats.put(chat.getParticipant(), (ChatAdapter) chat);
				    }
				    //will put it as notification
				    notifyChat(chat, body);
				}
			    } catch (RemoteException e) {
				if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, e.getMessage());
			    }
			
		}*/
		

		@Override
		public void chatCreated(Chat chat, boolean locally) {
			/// no call backs required on remote chat creation as we showing notification
			//till chat window opened by user. As soon he opens we will register msglistener
			//which will then take care of further msgs
			 ChatAdapter newchatAdapter;
			 String key = StringUtils.parseName(chat.getParticipant());
			 if(!in.co.hopin.Util.StringUtils.isBlank(key)) {
				if (mAllChats.containsKey(key)) {
					newchatAdapter= mAllChats.get(key);
					if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"returning old adapter for:"+key);
				}
				else
				{
					if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"chat adapter not fond so creating new for:"+key);
					newchatAdapter = new ChatAdapter(chat,SBChatManager.this);	
					mAllChats.put(key,newchatAdapter);
				}
             }
             else {
                    //newchatAdapter.addMessageListener(mChatAndInitialMsgListener);
			        if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "Insane smack " + chat.toString() + " created locally " + locally + " with blank key?: " + key);
             }
		
		}	
		}
		
	
		
	
}
