package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Fragments.HistoryInstaShareFragment;
import in.co.hopin.Fragments.HistoryPlanFragment;
import in.co.hopin.Fragments.SearchUserInstaFrag;
import in.co.hopin.Fragments.SearchUserPlanFrag;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;

public class SearchInputActivityNew extends FragmentActivity{
    public static final String TAG = "in.co.hopin.Activites.SearchInputActivity";      

	FragmentManager fm = this.getSupportFragmentManager();
	ToggleButton BtnGotoPastSearch;
	Button BtnInstaSearchView;
	Button BtnPlanSearchView;	
   
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
		 setContentView(R.layout.search_users_framelayout);
		 BtnInstaSearchView = (Button)findViewById(R.id.search_user_tab_insta);
		 BtnPlanSearchView = (Button)findViewById(R.id.search_user_tab_plan);
		 BtnGotoPastSearch = (ToggleButton)findViewById(R.id.search_user_tab_gotohistory);
		
		 BtnInstaSearchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    if(BtnGotoPastSearch.isChecked())
			    	showInstaHistoryLayout();
			    else
			    	showInstaSearchLayout();
				v.setSelected(true);
				BtnPlanSearchView.setSelected(false);
								
			}
		});
		 
		 BtnPlanSearchView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(BtnGotoPastSearch.isChecked())
				    	showPlanHistoryLayout();
				    else
				    	showPlanSearchLayout();
					v.setSelected(true);
					BtnInstaSearchView.setSelected(false);
									
				}
			});
		 
		 BtnGotoPastSearch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked)
				{
					if(BtnPlanSearchView.isSelected())
						showPlanHistoryLayout();
					else if(BtnInstaSearchView.isSelected())
						showInstaHistoryLayout();
					Toast.makeText(SearchInputActivityNew.this, "Tap history row to search..", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(BtnPlanSearchView.isSelected())
						showPlanSearchLayout();
					else if(BtnInstaSearchView.isSelected())
						showInstaSearchLayout();
				}
				
			}
		});
			 
		 BtnInstaSearchView.setSelected(true);
	     showInstaSearchLayout();
		
	 }
	 
	 public void showInstaSearchLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            fragTrans.replace(R.id.search_user_instaplan_content, new SearchUserInstaFrag());	            
	            fragTrans.commit();	           
	        }
	    }
	    
	    public void showPlanSearchLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new SearchUserPlanFrag());
	            ft.commit();
	           
	        }
	    }
	    
	    public void showInstaHistoryLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new HistoryInstaShareFragment());
	            ft.commit();
	           
	        }
	    } 
	    
	    public void showPlanHistoryLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new HistoryPlanFragment());
	            ft.commit();
	           
	        }
	    }
	    
	   
   

}
