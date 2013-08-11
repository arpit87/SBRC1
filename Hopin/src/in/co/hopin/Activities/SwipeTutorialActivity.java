package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.Util.HopinTracker;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwipeTutorialActivity extends Activity{
	
	CheckBox dontShowTutBox = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);       
        setContentView(R.layout.swipe_tap_transparent_layout); 
        dontShowTutBox = (CheckBox)findViewById(R.id.swipe_tap_dontshowagain_checkbox);
  	  dontShowTutBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
  			
  			@Override
  			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
  				ThisAppConfig.getInstance().putBool(ThisAppConfig.SWIPETUTORIALSHOWN,true); 				
  			}
  		});
	}
	
	public void onScreenTap(View V)
	 {		
		finish();
	 }
	
	@Override
	 public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("SwipeTutorialView");
	    }
	
	

}
