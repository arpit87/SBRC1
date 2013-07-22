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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class NewUserDialogActivity extends FBLoggableFragmentActivity{
	
	String source = "";
	String destination = "";		
	String userid = "";
	private boolean fbloginPromptIsShowing = false;
	
	private TextView dialogHeaderName = null;
	private TextView dialogHeaderTravelInfo = null;	
	private ImageView picViewExpanded = null;
	private ImageView chatIcon = null;
	//private ImageView smsIcon = null;
	private ImageView hopinIcon = null;
	private ImageView facebookIcon = null;
	private ImageView buttonClose = null;
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;
	int daily_insta_type;
	private ScrollView mScrollView;
	private TextView userNotLoggedInView;
		
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
		String name_str,worksat_str,studiedat_str,hometown_str,gender_str = "";	
		
		SBImageLoader.getInstance().displayImageElseStub(thisNearbyUserFBInfo.getImageURL(), picViewExpanded,R.drawable.userpicicon);
		dialogHeaderName.setText(thisNearbyUserFBInfo.getFullName());
		//dialogHeaderTravelInfo.setText(thisNearbyUser.getUserLocInfo().getFormattedTravelDetails(daily_insta_type));
		
		works_at = (TextView)findViewById(R.id.expanded_work);
		studied_at = (TextView)findViewById(R.id.expanded_education);
		hometown = (TextView)findViewById(R.id.expanded_from);
		gender = (TextView)findViewById(R.id.expanded_gender);
		
		name_str = thisNearbyUserFBInfo.getFullName();
		worksat_str = thisNearbyUserFBInfo.getWorksAt();
		studiedat_str = thisNearbyUserFBInfo.getStudiedAt();
		hometown_str = thisNearbyUserFBInfo.getHometown();
		gender_str = thisNearbyUserFBInfo.getGender();
		
		if(worksat_str!="null")
			works_at.setText(getSpannedText("Works at ", worksat_str));
		else
			works_at.setVisibility(View.GONE);
		
		if(studiedat_str!="null")
			studied_at.setText(getSpannedText("Studied at " ,studiedat_str));
		else
			studied_at.setVisibility(View.GONE);
		
		if(hometown_str!="null")
			hometown.setText(getSpannedText("HomeTown " , hometown_str));
		else
			hometown.setVisibility(View.GONE);
		
		if(gender_str!="null")
			gender.setText(getSpannedText("Gender ",gender_str));
		else
			gender.setVisibility(View.GONE);
		
	}
	
	private Spannable getSpannedText(String label, String text)
	{
		StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
		Spannable label_span = new SpannableString(label + " "+text);		
		label_span.setSpan(bold, 0,label.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return label_span;
	}
	
	protected void createAndDisplayNewUserArriveDialog()
	{	
				
			picViewExpanded = (ImageView)findViewById(R.id.newuserarrive_popup_expanded_pic);		
			dialogHeaderName = (TextView)findViewById(R.id.newuserarrive_popup_header);
			dialogHeaderTravelInfo = (TextView)findViewById(R.id.newuserarrive_popup_travelinfo);
			mScrollView = (ScrollView)findViewById(R.id.newuserarrive_popup_expanded_bio_scroll);
			userNotLoggedInView = (TextView)findViewById(R.id.newuserarrive_popup_usernotloggedintext);
			chatIcon = (ImageView)findViewById(R.id.newuserarrive_popup_chat_icon_view);
			//smsIcon = (ImageView)findViewById(R.id.newuserarrive_popup_sms_icon);
			hopinIcon = (ImageView)findViewById(R.id.newuserarrive_popup_hopin_icon);
			facebookIcon = (ImageView)findViewById(R.id.newuserarrive_popup_fb_icon_view);
			buttonClose = (ImageView)findViewById(R.id.newuserarrive_popup_button_close_balloon_expandedview);
		
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					finish();
				}
				});
			
			dialogHeaderName.setText(thisNearbyUser.getUserOtherInfo().getUserName());
			dialogHeaderTravelInfo.setText(thisNearbyUser.getUserLocInfo().getFormattedTravelDetails(daily_insta_type));
			picViewExpanded.setImageDrawable(getResources().getDrawable(R.drawable.userpicicon));
			if(thisNearbyUserFBInfo.FBInfoAvailable())
			{
				userNotLoggedInView.setVisibility(View.GONE);
				mScrollView.setVisibility(View.VISIBLE);
				setFBInfoOnExpandedPopup();
				
				//set listensers only if fbinfo available
				hopinIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onHopinProfileClickWithUser(NewUserDialogActivity.this, thisNearbyUserFBInfo);
				}
				});
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {					
					CommunicationHelper.getInstance().onChatClickWithUser(NewUserDialogActivity.this,thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFullName());						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser(NewUserDialogActivity.this,thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFBUsername());						
				}
			});	
			}
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

	public boolean isFbloginPromptIsShowing() {
		return fbloginPromptIsShowing;
	}

	public void setFbloginPromptIsShowing(boolean fbloginPromptIsShowing) {
		this.fbloginPromptIsShowing = fbloginPromptIsShowing;
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
	
}
