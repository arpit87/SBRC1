package in.co.hopin.Activities;

import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class NewUserDialogActivity extends Activity{
	
	String source = "";
	String destination = "";		
	String userid = "";
	
	private TextView dialogHeaderName = null;
	private TextView dialogHeaderTravelInfo = null;	
	private ImageView picViewExpanded = null;
	private ImageView chatIcon = null;
	private ImageView smsIcon = null;
	private ImageView facebookIcon = null;
	private ImageView buttonClose = null;
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;
	int daily_insta_type;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newuserarrived_popup);
        		
	}
	
	@Override
    public void onResume(){
    	super.onResume();

    	String jsonstr = getIntent().getStringExtra("nearbyuserjsonstr");
    	daily_insta_type = getIntent().getIntExtra("daily_insta_type", 1);
    	try {
    		thisNearbyUser = new NearbyUser(new JSONObject(jsonstr));
    		thisNearbyUserFBInfo = thisNearbyUser.getUserFBInfo();
    		createAndDisplayNewUserArriveDialog();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    	
	}

	private void setFBInfoOnExpandedPopup()
	{		
		
		TextView works_at = null;
		TextView studied_at = null;
		TextView hometown = null;
		TextView gender = null;
			
				
		dialogHeaderName.setText(thisNearbyUserFBInfo.getFullName());
		dialogHeaderTravelInfo.setText(thisNearbyUser.getUserLocInfo().getFormattedTravelDetails(daily_insta_type));
				
		works_at = (TextView)findViewById(R.id.newuserarrive_popup_expanded_work);
		studied_at = (TextView)findViewById(R.id.newuserarrive_popup_expanded_education);
		hometown = (TextView)findViewById(R.id.newuserarrive_popup_expanded_from);
		gender = (TextView)findViewById(R.id.newuserarrive_popup_expanded_gender);
		
		works_at.setText("Works at "+thisNearbyUserFBInfo.getWorksAt());
		studied_at.setText("Studied at " +thisNearbyUserFBInfo.getStudiedAt());
		hometown.setText("HomeTown " + thisNearbyUserFBInfo.getHometown());
		gender.setText("Gender "+thisNearbyUserFBInfo.getGender());
		
	}
	
	protected void createAndDisplayNewUserArriveDialog()
	{	
				
			picViewExpanded = (ImageView)findViewById(R.id.newuserarrive_popup_expanded_pic);		
			dialogHeaderName = (TextView)findViewById(R.id.newuserarrive_popup_header);
			dialogHeaderTravelInfo = (TextView)findViewById(R.id.newuserarrive_popup_travelinfo);
			chatIcon = (ImageView)findViewById(R.id.newuserarrive_popup_chat_icon_view);
			smsIcon = (ImageView)findViewById(R.id.newuserarrive_popup_sms_icon);
			facebookIcon = (ImageView)findViewById(R.id.newuserarrive_popup_fb_icon_view);
			buttonClose = (ImageView)findViewById(R.id.newuserarrive_popup_button_close_balloon_expandedview);
		
					
			/*if(!thisNearbyUserFBInfo.isPhoneAvailable())
			{				
				smsIcon.setImageResource(R.drawable.sms_icon_disabled);
				smsIcon.invalidate();                
			}*/
			
			
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					finish();
				}
				});
			
			smsIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onSmsClickWithUser(userid,thisNearbyUserFBInfo.isPhoneAvailable());
				}
				});
						
			
			SBImageLoader.getInstance().displayImage(thisNearbyUserFBInfo.getImageURL(), picViewExpanded);
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {					
					CommunicationHelper.getInstance().onChatClickWithUser(thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFullName());						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser(NewUserDialogActivity.this,thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFBUsername());						
				}
			});	
			
			setFBInfoOnExpandedPopup();
		
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
