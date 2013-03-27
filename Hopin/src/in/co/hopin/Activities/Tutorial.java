package in.co.hopin.Activities;

import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.AddUserRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class Tutorial extends Activity{
	ImageView map1View;
	ImageView map2View;
	TextView tapFrameTextView;
	FacebookConnector fbconnect;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        final String uuid = b.getString("uuid");
        
        final EditText userNameView = (EditText) findViewById(R.id.tutorial_name_edittext);
        final EditText phoneView = (EditText) findViewById(R.id.tutorial_mobile_edittext);
        tapFrameTextView = (TextView) findViewById(R.id.tutorial_maptaptextview);
        map1View = (ImageView) findViewById(R.id.tutorial_smallpicmapview);
        map2View = (ImageView) findViewById(R.id.tutorial_expandedpicmapview);
        
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
       
        Button startButton = (Button) findViewById(R.id.tutorial_startbutton);
		// if button is clicked, close the custom dialog
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                String userNameText = userNameView.getText().toString();
                if (StringUtils.isBlank(userNameText)) {
                	Toast.makeText(Tutorial.this,"Please enter name",Toast.LENGTH_SHORT);
                    return;
                }
                
               String mobile = phoneView.getText().toString();
               ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, userNameText);
               ThisUserConfig.getInstance().putString(ThisUserConfig.MOBILE, mobile);
               SBHttpRequest request = new AddUserRequest(uuid,userNameText,Tutorial.this);		
       		   SBHttpClient.getInstance().executeRequest(request);
       		   ProgressHandler.showInfiniteProgressDialoge(Tutorial.this, "Welcome "+userNameText+"!", "Preparing for first run");       		  
				
			}
		}); 
        
        Button faceBookLoginbutton = (Button) findViewById(R.id.tutorial_signInViaFacebook);
        faceBookLoginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProgressHandler.showInfiniteProgressDialoge(Tutorial.this, "Trying logging", "Please wait..");
				fbconnect = new FacebookConnector(Tutorial.this);
				fbconnect.loginToFB();
			}
		});
        
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

}
