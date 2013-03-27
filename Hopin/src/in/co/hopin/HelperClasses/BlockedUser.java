package in.co.hopin.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import in.co.hopin.Platform.Platform;
import in.co.hopin.provider.BlockedUsersProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockedUser {

    private static final String TAG = "my.b1701.SB.HelperClasses.BlockedUser";
    private static Uri mUriFetch = Uri.parse("content://" + BlockedUsersProvider.AUTHORITY + "/db_fetch_only");
    private static Uri mUri = Uri.parse("content://" + BlockedUsersProvider.AUTHORITY + "/blockedUsers");
    private static String[] columns = new String[] {"fbId", "name"};

    private final String fbId;
    private final String name;

    public BlockedUser(String fbId, String name) {
        this.fbId = fbId;
        this.name = name;
    }

    public String getFbId() {
        return fbId;
    }

    public String getName() {
        return name;
    }

    public static List<BlockedUser> getList(){
        Log.i(TAG, "Fetching blocked Users");
        List<BlockedUser> blockedUsers;
        
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            Log.i(TAG, "Empty result");
            blockedUsers = Collections.emptyList();
        } else {
            blockedUsers = new ArrayList<BlockedUser>();
            if (cursor.moveToFirst()) {
                do {
                    BlockedUser blockedUser = new BlockedUser(cursor.getString(0), cursor.getString(1));
                    blockedUsers.add(blockedUser);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null){
            cursor.close();
        }

        return blockedUsers;
    }

    public static boolean isUserBlocked(String fbId){
        Log.i(TAG, "Checking if '" + fbId + "' is blocked");
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, "fbId = ?", new String[]{fbId}, null);

        boolean isUserBlocked = true;
        if (cursor == null || cursor.getCount() == 0){
        	Log.i(TAG, "fbid:" + fbId + "' is not blocked ");
            isUserBlocked = false;
        }

        if (cursor != null) {
            cursor.close();
        }

        Log.i(TAG, "fbid:" + fbId + "' is blocked :"+isUserBlocked);
        return isUserBlocked;
    }

    public static void addtoList(final String fbId, final String name){
        if (isUserBlocked(fbId)) {
            Log.i(TAG, "User already blocked");
            return;
        }

        Log.i(TAG, "Adding '" +fbId + "' to blocked users list");
        new Thread("blockUser") {
            @Override
            public void run() {
                saveHistoryBlocking(fbId, name);
            }
        }.start();

    }

    private static void saveHistoryBlocking(String fbId, String name) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();

        try {
            ContentValues values = new ContentValues();
            values.put(columns[0], fbId);
            values.put(columns[1], name);
            cr.insert(mUri, values);
        } catch (RuntimeException e) {
            Log.e(TAG, "BlockUserQueryError", e);
        }
    }

    public static void clearList(){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, null, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "ClearAllQueryError", e);
        }
    }

    public static void deleteFromList(String fbId){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, "fbId = ?", new String[]{fbId});
        } catch (RuntimeException e){
            Log.e(TAG, "Error in deleting user : " + fbId, e);
        }
    }
}