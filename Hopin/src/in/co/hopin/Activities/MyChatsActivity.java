package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Fragments.SBChatListFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.Util.HopinTracker;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MyChatsActivity extends FragmentActivity {
	
	FragmentManager fm = this.getSupportFragmentManager();	
	
	  @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("MyChats");
	        HopinTracker.sendEvent("MyChats","ScreenOpen","mychats:open",1L);	        
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        //EasyTracker.getInstance().activityStop(this);
	    }
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.chatlist_layout);
		 showChatListLayout();
	 }
	 
	 @Override
	    public void onResume(){
	        super.onResume();
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

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
	
	@Override
	public void onPause(){
    	super.onPause();    	
        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);    	
    }

}
