package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.UserFBInfo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class OtherUserProfileActivity extends Activity{
	
	private String fbinfoJsonStr = "";	
	private UserFBInfo userFBInfo = null;
	private JSONObject fbInfoJSON;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.other_user_profile);
        
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		fbinfoJsonStr = getIntent().getStringExtra("fb_info");
		if (Platform.getInstance().isLoggingEnabled()) Log.d("debug","got json str:"+fbinfoJsonStr);
		try {
			fbInfoJSON = new JSONObject(fbinfoJsonStr);
			userFBInfo = new UserFBInfo(fbInfoJSON);
			setFBInfoOnExpandedPopup();
		} catch (JSONException e) {
			Toast.makeText(this, "Sorry problem  fetching profile", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
	}
	
	private void setFBInfoOnExpandedPopup()
	{		
		
		TextView works_at = null;
		TextView studied_at = null;
		TextView hometown = null;
		TextView gender = null;
		TextView dialogHeaderName = null;
		ImageView pic = null;
		
		pic = (ImageView)findViewById(R.id.other_user_profile_popup_expanded_pic);
		dialogHeaderName = (TextView)findViewById(R.id.other_user_profile_popup_header);		
		works_at = (TextView)findViewById(R.id.other_user_profile_worksat);
		studied_at = (TextView)findViewById(R.id.other_user_profile_studiedat);
		hometown = (TextView)findViewById(R.id.other_user_profile_hometown);
		gender = (TextView)findViewById(R.id.other_user_profile_gender);		
		
		SBImageLoader.getInstance().displayImageElseStub(userFBInfo.getImageURL(), pic, R.drawable.nearbyusericon);
		dialogHeaderName.setText(userFBInfo.getFullName());	
		works_at.setText(userFBInfo.getWorksAt());
		studied_at.setText(userFBInfo.getStudiedAt());
		hometown.setText(userFBInfo.getHometown());
		gender.setText(userFBInfo.getGender());
		
	}

    @Override
    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

}
