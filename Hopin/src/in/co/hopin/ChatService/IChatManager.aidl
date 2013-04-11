package in.co.hopin.ChatService;

import in.co.hopin.ChatClient.IMessageListener;
import in.co.hopin.ChatClient.IChatManagerListener;
import in.co.hopin.ChatService.IChatAdapter;

interface IChatManager {

	IChatAdapter createChat(in String participant, in IMessageListener listener);
	void deleteChatNotification(IChatAdapter chat);
	void addChatCreationListener(IChatManagerListener listener);
    void removeChatCreationListener(IChatManagerListener listener);
    IChatAdapter getChat(String participant);
}