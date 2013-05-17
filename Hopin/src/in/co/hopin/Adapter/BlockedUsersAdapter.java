package in.co.hopin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.hopin.R;

import in.co.hopin.HelperClasses.BlockedUser;
import in.co.hopin.HelperClasses.SBImageLoader;

import java.util.List;

public class BlockedUsersAdapter extends BaseAdapter {

    private Activity activity;
    private List<BlockedUser> blockedUsers;
    private LayoutInflater inflater;

    public BlockedUsersAdapter(Activity activity, List<BlockedUser> blockedUsers){
        this.activity = activity;
        this.blockedUsers = blockedUsers;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return blockedUsers.size();
    }

    @Override
    public BlockedUser getItem(int i) {
        return blockedUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BlockedUser blockedUser =  blockedUsers.get(position);
        View blockedUserView = convertView != null ? convertView : inflater.inflate(R.layout.blocked_user_row, null);
        ImageView blockedUserImage = (ImageView) blockedUserView.findViewById(R.id.blocked_user_image);
        TextView blockedUserName = (TextView) blockedUserView.findViewById(R.id.blocked_user_name);

        String imageUrl = "http://graph.facebook.com/" + blockedUser.getFbId() + "/picture?type=small";
        SBImageLoader.getInstance().displayImageElseStub(imageUrl, blockedUserImage, R.drawable.userpicicon);
        blockedUserName.setText(blockedUser.getName());

        return blockedUserView;
    }
}
