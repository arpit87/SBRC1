package in.co.hopin.Activities;

import com.google.analytics.tracking.android.EasyTracker;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ThisUserConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReloginActivity extends FBLoggableFragmentActivity {
	
	FacebookConnector fbconnect;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refblogin_layout);
        
        Button faceBookLoginbutton = (Button) findViewById(R.id.refblogin_signInViaFacebook);
        faceBookLoginbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("ui_action", "button_press", "FbReLogin_button", 1L);
				Toast.makeText(ReloginActivity.this, "Logging...please wait..", Toast.LENGTH_SHORT).show();
				fbconnect = FacebookConnector.getInstance(ReloginActivity.this);
				fbconnect.reloginToFB();
			}
		});   
	}
	
	@Override
	public void onResume() {
		super.onResume();		
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
