package in.co.hopin.ChatService;

import in.co.hopin.ChatService.IChatManager;
import in.co.hopin.ChatClient.ISBChatConnAndMiscListener;

interface IXMPPAPIs {
	     
    /**
     * Connect and login synchronously on the server.
     */
    void connect();

    /**
     * Disconnect from the server
     */
    void disconnect();
    
     void loginAsync(in String login, in String password);
     
     void loginWithCallBack(in String login, in String password,in ISBChatConnAndMiscListener listener);
     
    
    /**
     * Get the chat manager.
     */
    IChatManager getChatManager();

       

}