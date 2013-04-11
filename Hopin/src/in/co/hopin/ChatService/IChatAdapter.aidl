package in.co.hopin.ChatService;

import in.co.hopin.ChatService.Message;
import in.co.hopin.ChatClient.IMessageListener;

interface IChatAdapter {

void sendMessage(in Message message);
void setOpen(in boolean value);
void addMessageListener(in IMessageListener listener);
void removeMessageListener(IMessageListener listener) ;
boolean isOpen();
String getParticipant();
List<Message> getMessages();
}