package in.co.hopin.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import in.co.hopin.ChatService.Message;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.provider.ChatHistoryProvider;

import java.util.*;

public class ChatHistory {
    private static final String TAG = "in.co.hopin.HelperClasses.ChatHistory";
    private static Uri mUriFetch = Uri.parse("content://" + ChatHistoryProvider.AUTHORITY + "/db_fetch_only");
    private static Uri mUri = Uri.parse("content://" + ChatHistoryProvider.AUTHORITY + "/chathistory");
    private static String[] columns = new String[] {"fbIdTo",
                                                    "fbIdFrom",
                                                    "body",                                                 
                                                    "groupId",
                                                    "timestamp",
                                                    "status",
                                                    "uniqueId",
                                                    "date"
                                                   };

    public static List<Message> getChatHistory(String userId){
        String fbid = getFBId(userId);
        //Log.i(TAG, "Fetching chat history for " + fbid);
        List<Message> messages;

        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, "fbIdTo = ? or fbIdFrom = ?", new String[]{fbid, fbid}, columns[7]);

        if (cursor == null || cursor.getCount() == 0) {
            //Log.i(TAG, "Empty result");
            messages = Collections.emptyList();
        } else {
            messages = new LinkedList<Message>();
            if (cursor.moveToFirst()) {
                do {
                    messages.add(buildMessage(cursor));
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null){
            cursor.close();
        }

        return messages;
    }

    public static Map<String, List<Message>> getCompleteChatHistory(String thisUserFbId){
        //Log.i(TAG, "Fetching complete chat history");
        Map<String, List<Message>> chatHistory = new HashMap<String, List<Message>>();
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, null, null, columns[5]);

        if (!(cursor == null || cursor.getCount() == 0)){
            Message message = buildMessage(cursor);
            String userId = (message.getFrom().equals(thisUserFbId) ? message.getTo() :
                    message.getTo().equals(thisUserFbId) ? message.getFrom() : null);
            if (userId != null) {
                List<Message> messages = chatHistory.get(userId);
                if (messages == null){
                    messages = new ArrayList<Message>();
                    chatHistory.put(userId, messages);
                }
                messages.add(message);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return chatHistory;
    }

    public static void addtoChatHistory(final Message message){
        //Log.i(TAG, "Saving chathistory for user " + message.getFrom());
        final long time = System.currentTimeMillis();
        new Thread("addchathistory") {
            @Override
            public void run() {
                saveChatHistoryBlocking(message, time);
            }
        }.start();

    }

    private static void saveChatHistoryBlocking(Message message, long time) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();

        try {
            ContentValues values = new ContentValues();
            values.put(columns[0], getFBId(message.getTo()));
            values.put(columns[1], getFBId(message.getFrom()));
            values.put(columns[2], message.getBody());           
            values.put(columns[3], -1);
            values.put(columns[4], message.getTimestamp());
            values.put(columns[5], message.getStatus());
            values.put(columns[6], message.getUniqueMsgIdentifier());
            values.put(columns[7], time);
            cr.insert(mUri, values);
        } catch (RuntimeException e) {
            //Log.e(TAG, "BlockUserQueryError", e);
        }
    }

    public static void clearList(){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, null, null);
        } catch (RuntimeException e) {
            //Log.e(TAG, "ClearAllQueryError", e);
        }
    }

    public static void updateStatus(long messageUniqueId, int status){
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columns[5],status);
        cr.update(mUri, contentValues, "uniqueId = ?", new String[]{Long.toString(messageUniqueId)});
    }

    private static Message buildMessage(Cursor cursor){
        String to = cursor.getString(0) + "@" + ServerConstants.CHATSERVERIP;
        String from = cursor.getString(1) + "@" + ServerConstants.CHATSERVERIP;
        String body = cursor.getString(2);       
        String time = cursor.getString(4);
        int status = cursor.getInt(5);
        return new Message(to, from, body, time, Message.MSG_TYPE_CHAT, status);
    }

    private static String getFBId(String userid){
        return userid.split("@")[0];
    }
}
