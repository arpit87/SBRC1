package in.co.hopin.Activities;

import in.co.hopin.Fragments.SBChatListFragment;
import in.co.hopin.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.analytics.tracking.android.EasyTracker;

public class MyChatsActivity extends FragmentActivity{
	
	FragmentManager fm = this.getSupportFragmentManager();	
   
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
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.chatlist_layout);
		 showChatListLayout();
	 }
	 
	 public void showChatListLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            fragTrans.replace(R.id.chatlist_layout_content, new SBChatListFragment());	            
	            fragTrans.commit();	           
	        }
	    }

}
