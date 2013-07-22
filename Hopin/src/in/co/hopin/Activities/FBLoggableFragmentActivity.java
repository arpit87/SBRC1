package in.co.hopin.Activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;


public abstract class FBLoggableFragmentActivity extends SherlockFragmentActivity{
	public abstract boolean isFbloginPromptIsShowing();
	public abstract void setFbloginPromptIsShowing(boolean fbloginPromptIsShowing);
}
