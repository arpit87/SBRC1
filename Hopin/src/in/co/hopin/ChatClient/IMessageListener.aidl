package in.co.hopin.ChatClient;


import in.co.hopin.ChatService.IChatAdapter;
import in.co.hopin.ChatService.Message;

interface IMessageListener {

	void processMessage(in IChatAdapter chat,in Message msg);
}
