package in.co.hopin.Activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.R;

import in.co.hopin.Adapter.BlockedUsersAdapter;
import in.co.hopin.HelperClasses.BlockedUser;
import in.co.hopin.Util.HopinTracker;

import java.util.List;

public class BlockedUsersActivity extends ListActivity{
    private static final String TAG = "in.co.hopin.Activities.BlockedUsersActivity";

    private List<BlockedUser> blockedUsers;
    private BlockedUsersAdapter blockedUsersAdapter;
    TextView noBlockedUsers = null;
    ListView blockedListView = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_users_layout);
        noBlockedUsers = (TextView) findViewById(R.id.blocked_users_layout_noblocked);
        blockedListView = (ListView) findViewById(android.R.id.list);        
        this.blockedUsers = BlockedUser.getList();
        if(blockedUsers.isEmpty())
        {
        	noBlockedUsers.setVisibility(View.VISIBLE);
        	blockedListView.setVisibility(View.GONE);
        }
        else
        {
	        this.blockedUsersAdapter = new BlockedUsersAdapter(this, this.blockedUsers);
	        setListAdapter(blockedUsersAdapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        BlockedUser blockedUser = (BlockedUser) getListAdapter().getItem(position);
        buildUnblockAlertMessage(blockedUser);
    }

    private void buildUnblockAlertMessage(final BlockedUser blockedUser) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to unblock '"+ blockedUser.getName() +"'?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        BlockedUser.deleteFromList(blockedUser.getFbId());
                        blockedUsers.remove(blockedUser);
                        blockedUsersAdapter.notifyDataSetChanged();
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

    @Override
    public void onStart(){
        super.onStart();
        HopinTracker.sendView("BlockedUserListActivity");
    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }
}
