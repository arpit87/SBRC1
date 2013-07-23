package in.co.hopin.Activities;

import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.FBLoginDialogFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingsActivity extends FragmentActivity{
	
	private static final String TAG = "in.co.hopin.Activities.SettingsActivity";
	CheckBox showNewUserPopup;
	CheckBox showChatPopup;
	CheckBox womenFilter;
	View womanFilterView;
	CheckBox fbfriendsOnlyFilter;
    View blockedUsersView;
    FacebookConnector fbconnect;
    View feedbackView;
    View profileView;
    private boolean fbloginPromptIsShowing = false;

	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);		
		 womenFilter = (CheckBox)findViewById(R.id.settings_womenfilter_checkbox);
		 fbfriendsOnlyFilter = (CheckBox)findViewById(R.id.settings_fbfriendonly_checkbox);		 
         blockedUsersView = findViewById(R.id.settings_blockedusers_layout);         
         feedbackView = findViewById(R.id.settings_feedback_layout);
         womanFilterView = findViewById(R.id.settings_womenfilter_tablerow);
         profileView = findViewById(R.id.settings_profile_layout);
	 }
	 
	 @Override
	 protected void onResume(){
	     super.onResume();
		 fillSettingsActivity();
         showNewUserPopup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP, isChecked);				
			}
		});
         
         womenFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
          	
 			@Override
 			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
 				{
 					if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"woman filter clicked,checked:"+isChecked);
 					womenFilter.setChecked(false);
 					CommunicationHelper.getInstance().FBLoginpromptPopup_show(SettingsActivity.this, true);
 				}
 				else
 				{
 					if(!"female".equalsIgnoreCase(ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER)))
 					{
 						Toast.makeText(SettingsActivity.this, "Sorry this filter is only meant for women", Toast.LENGTH_SHORT).show();
 						womanFilterView.setVisibility(View.GONE);
 					}
 					else
 					{
 						ThisAppConfig.getInstance().putBool(ThisAppConfig.WOMANFILTER, isChecked);	
 					}
 				}
 			}
 		});
         
         fbfriendsOnlyFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
           	
  			@Override
  			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
  				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
  				{
  					if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"fb chk clicked,checked:"+isChecked);
  					fbfriendsOnlyFilter.setChecked(false);
  					CommunicationHelper.getInstance().FBLoginpromptPopup_show(SettingsActivity.this, true);
  				}
  				else
  				{  					
  					ThisAppConfig.getInstance().putBool(ThisAppConfig.FBFRIENDONLYFILTER, isChecked);  					
  				}
  			}
  		}); 

         blockedUsersView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent blockedUsersIntent = new Intent(SettingsActivity.this, BlockedUsersActivity.class);
                 blockedUsersIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                 startActivity(blockedUsersIntent);
             }
         });
         
         feedbackView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
            	Intent i = new Intent(Platform.getInstance().getContext(),FeedbackActivity.class);
     			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     		
     			i.putExtra("show_prompt", false);
     			Platform.getInstance().getContext().startActivity(i);						
             }
         });
         
        profileView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent hopinSelfProfile = new Intent(Platform.getInstance().getContext(),SelfProfileActivity.class);
				hopinSelfProfile.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);			
		    	startActivity(hopinSelfProfile);				
			}
		});
		 
	 }
	 
	 @Override
		public void onPause(){
	    	super.onPause();    	
	        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);    	
	    }
	 
	 private void fillSettingsActivity()
	 {
		 showNewUserPopup.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.NEWUSERPOPUP));
		 if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN) &&
		   !ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER).equalsIgnoreCase("female"))
		 {
			 if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"not woman"); 
			womanFilterView.setVisibility(View.GONE);
		 }
		else				
			womenFilter.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.WOMANFILTER));
		 fbfriendsOnlyFilter.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.FBFRIENDONLYFILTER));
		 if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"woman filter gender is:"+ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER));		 
	 }
	 
	 @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        CommunicationHelper.getInstance().authorizeCallback(this, requestCode, resultCode, data);
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
}
