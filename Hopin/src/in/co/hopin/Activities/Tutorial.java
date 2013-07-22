package in.co.hopin.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.R;
import in.co.hopin.Util.StringUtils;

public class Tutorial extends FBLoggableFragmentActivity{
	ImageView map1View;
	ImageView map2View;
	TextView tapFrameTextView;
	FacebookConnector fbconnect;
	ScrollView mScrollView; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        final String uuid = b.getString("uuid");
        
        final EditText userNameView = (EditText) findViewById(R.id.tutorial_name_edittext);
        //final EditText phoneView = (EditText) findViewById(R.id.tutorial_mobile_edittext);
        tapFrameTextView = (TextView) findViewById(R.id.tutorial_maptaptextview);
        map1View = (ImageView) findViewById(R.id.tutorial_smallpicmapview);
        map2View = (ImageView) findViewById(R.id.tutorial_expandedpicmapview);
        mScrollView = (ScrollView)findViewById(R.id.tutorial_scroll);
        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tapFrameTextView.startAnimation(anim);
        
        map1View.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				anim.cancel();
				tapFrameTextView.setVisibility(View.INVISIBLE);
				map1View.setVisibility(View.GONE);
				map2View.setVisibility(View.VISIBLE);				
			}
		});
       
        userNameView.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// scroll whole view down
				mScrollView.postDelayed(new Runnable() {
				        @Override
				        public void run() {
				        	mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				        }
				    },1000L);
				return false;
			}
		});
       
        
        Button startButton = (Button) findViewById(R.id.tutorial_startbutton);
		// if button is clicked, close the custom dialog
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                String userNameText = userNameView.getText().toString();
                if (StringUtils.isBlank(userNameText)) {
                	Toast.makeText(Tutorial.this,"Please enter name",Toast.LENGTH_SHORT).show();
                    return;
                }
                
              // String mobile = phoneView.getText().toString();
               ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, userNameText);
               //ThisUserConfig.getInstance().putString(ThisUserConfig.MOBILE, mobile);
               SBHttpRequest request = new AddUserRequest(uuid,userNameText,Tutorial.this);		
       		   SBHttpClient.getInstance().executeRequest(request);
       		   ProgressHandler.showInfiniteProgressDialoge(Tutorial.this, "Welcome "+userNameText+"!", "Preparing for first run");       		  
				
			}
		}); 
        
        Button faceBookLoginbutton = (Button) findViewById(R.id.tutorial_signInViaFacebook);
        faceBookLoginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("ui_action", "button_press", "FbLogin_button", 1L);
				Toast.makeText(Tutorial.this, "Logging...please wait..", Toast.LENGTH_SHORT).show();
				fbconnect = FacebookConnector.getInstance(Tutorial.this);
				fbconnect.loginToFB();
			}
		});   
        
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			finish();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbconnect.authorizeCallback(requestCode, resultCode, data);
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

	@Override
	public boolean isFbloginPromptIsShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFbloginPromptIsShowing(boolean fbloginPromptIsShowing) {
		// TODO Auto-generated method stub
		
	}

}
