package in.co.hopin.Activities;

import java.util.ArrayList;
import java.util.List;

import in.co.hopin.R;
import in.co.hopin.Activities.SelfProfileActivity.MyPageAdapter;
import in.co.hopin.Fragments.HistoryInstaShareFragment;
import in.co.hopin.Fragments.HistoryPlanFragment;
import in.co.hopin.Fragments.SearchUserInstaFrag;
import in.co.hopin.Fragments.SearchUserPlanFrag;
import in.co.hopin.Fragments.SelfAboutMeFrag;
import in.co.hopin.Fragments.SelfFriends;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ThisUserConfig;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;

public class SearchInputActivityNew extends FragmentActivity{
    public static final String TAG = "in.co.hopin.Activites.SearchInputActivity";      

	FragmentManager fm = this.getSupportFragmentManager();
	ToggleButton BtnGotoPastSearch;
	Button BtnInstaSearchView;
	Button BtnPlanSearchView;	
	private MyPageAdapter mPagerSearchAdapter;
	private MyPageAdapter mPagerHistoryAdapter;
	private ViewPager mPager;
	private static final int NUM_PAGES = 2;
	
	
   
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
		 mPager = (ViewPager) findViewById(R.id.search_users_viewpager);		 
		 mPagerSearchAdapter = new MyPageAdapter(getSupportFragmentManager());  
		 mPagerSearchAdapter.setFragments(getSearchFragments());
		 mPagerHistoryAdapter = new MyPageAdapter(getSupportFragmentManager());  
		 mPagerHistoryAdapter.setFragments(getHistoryFragments());
	     mPager.setAdapter(mPagerSearchAdapter);	     
		 BtnInstaSearchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    mPager.setCurrentItem(0);
			}
		});
		 
		 BtnPlanSearchView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPager.setCurrentItem(1);
				}
			});
		 
		 BtnGotoPastSearch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {	
				mPager.removeAllViews();
				if(isChecked)
				{					
					mPager.setAdapter(mPagerHistoryAdapter);					
					if(BtnPlanSearchView.isSelected())
						mPager.setCurrentItem(1);
					else if(BtnInstaSearchView.isSelected())
						mPager.setCurrentItem(0);					
				}
				else
				{		
					mPager.setAdapter(mPagerSearchAdapter);					
					if(BtnPlanSearchView.isSelected())
						mPager.setCurrentItem(1);
					else if(BtnInstaSearchView.isSelected())
						mPager.setCurrentItem(0);
				}
				
			}
		});
		 
		  mPager.setOnPageChangeListener(new OnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {	            	
	            		switch(position)
		        		{
		        			case 0: //about button selected
		        				BtnInstaSearchView.setSelected(true);
		    					BtnPlanSearchView.setSelected(false);
		        			break;
		        			case 1:
		        				BtnInstaSearchView.setSelected(false);
		    					BtnPlanSearchView.setSelected(true);
		        			break;
		        			default:
		        				BtnInstaSearchView.setSelected(true);
		    					BtnPlanSearchView.setSelected(false);  		
		        		}	            	
	            }
	
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
	        });
	     	 
		 BtnInstaSearchView.setSelected(true);
	     	
	 }
	
	 private List<Fragment> getSearchFragments() {	
	    	List<Fragment> search_frag_list = new ArrayList<Fragment>();	    	
			Fragment searchUserPlanFrag = new SearchUserPlanFrag();
			Fragment searchUserInstaFrag = new SearchUserInstaFrag();			
			search_frag_list.add(searchUserInstaFrag);
			search_frag_list.add(searchUserPlanFrag);
			return search_frag_list;					
		}
	    
	    private List<Fragment> getHistoryFragments() {		    	
	    	List<Fragment> history_frag_list = new ArrayList<Fragment>();				
			Fragment historyInstaShareFragment = new HistoryInstaShareFragment();
			Fragment historyPlanFragment = new HistoryPlanFragment();			
			history_frag_list.add(historyInstaShareFragment);
			history_frag_list.add(historyPlanFragment);		
			return history_frag_list;
		}
	    
	    class MyPageAdapter extends FragmentStatePagerAdapter {
	    	
	    	private List<Fragment> fragments;
	    	public List<Fragment> getFragments() {
				return fragments;
			}

			public void setFragments(List<Fragment> fragments) {
				this.fragments = fragments;
			}

			public MyPageAdapter(FragmentManager fm) {    
	    	super(fm);
	     	
	    	}
	    	
	    	@Override    
	    	public Fragment getItem(int position) {			
	    	return this.fragments.get(position);   
	    	
	    	}
	    
	    	@Override    
	    	public int getCount() {    
	    	return this.fragments.size();   
	    	}  	
		    
	    	}
	    
	   
   

}
