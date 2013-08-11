package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.FBLoginDialogFragment;
import in.co.hopin.Fragments.SelfAboutMeFrag;
import in.co.hopin.Fragments.SelfFriends;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SelfProfileRequest;
import in.co.hopin.LocationHelpers.SBLocationManager;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class SelfProfileActivity extends FragmentActivity {
	
	private static String TAG = "in.co.hopin.Activities.SelfProfileActivity";
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Button button1;
    private Button button2;
	private String fbinfoJsonStr = "";	
	private UserFBInfo userFBInfo = null;
	private JSONObject fbInfoJSON;
	private boolean fbloginPromptIsShowing = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_profile); 
        setInfoOnWindow();        
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();    	
    	
    	 if(ThisUserNew.getInstance().getUserFBInfo()==null && ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))    		
    	{
	    		//need to fetch data from out server
	    		ProgressHandler.showInfiniteProgressDialoge(this, "Fetching your profile data", "Please wait..");
	    		SelfProfileRequest req = new SelfProfileRequest();
				SBHttpClient.getInstance().executeRequest(req);
    	}
    	else
    	{
	        // Instantiate a ViewPager and a PagerAdapter.
	        mPager = (ViewPager) findViewById(R.id.self_profile_viewpager);
	        List<Fragment> fragments = getFragments();       
	        mPagerAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);      
	        mPager.setAdapter(mPagerAdapter);        
	        
	        // Watch for button clicks.
	        button1 = (Button)findViewById(R.id.self_profile_abountme);
	        button2 = (Button)findViewById(R.id.self_profile_friendtab);
	        button1.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	aboutSelected();
	            	mPager.setCurrentItem(0);
	            }
	        });        
	        
	        button2.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	FriendsSelected();
	            	mPager.setCurrentItem(1);
	            }
	        });
	        
	        mPager.setOnPageChangeListener(new OnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	switch(position)
	        		{
	        			case 0: //about button selected
	        				aboutSelected();
	        			break;
	        			case 1:
	        				FriendsSelected();
	        			break;
	        			default:
	        				aboutSelected();    		
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
	        
	        aboutSelected(); // initially select "about" view
	        mPager.setCurrentItem(0);
	       
    	}
    	
    }
    
    private List<Fragment> getFragments() {
		List<Fragment> frag_list= new ArrayList<Fragment>(); 
		Fragment aboutMeFrag = new SelfAboutMeFrag();
		Fragment friendsFrag = new SelfFriends();		
		frag_list.add(aboutMeFrag);
		frag_list.add(friendsFrag);
		return frag_list;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }
    
	@Override
    public void onBackPressed() {
        if (mPager==null || mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
	
	public void aboutSelected()
	{
		button1.setTypeface(null,Typeface.BOLD);
    	button2.setTypeface(null,Typeface.NORMAL);
    	button2.setSelected(false);    
    	button1.setSelected(true);
	}
	
	public void FriendsSelected()
	{
		button1.setTypeface(null,Typeface.NORMAL);
    	button1.setSelected(false);
    	button2.setTypeface(null,Typeface.BOLD);    
    	button2.setSelected(true);
	}
	
	
	
	private void setInfoOnWindow()
	{
		ImageView userPic;  
		TextView userNameView;
		ImageView maleIcon,femaleIcon; 
		userPic = (ImageView) findViewById(R.id.self_profile_thumbnail);
		userNameView = (TextView) findViewById(R.id.self_profile_name);
		maleIcon = (ImageView) findViewById(R.id.self_profile_maleicon);	
		femaleIcon = (ImageView) findViewById(R.id.self_profile_femaleicon);
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			String userName = ThisUserConfig.getInstance().getString(ThisUserConfig.USERNAME);
			userNameView.setText(userName);
			maleIcon.setVisibility(View.GONE);
			userPic.setImageDrawable(getResources().getDrawable(R.drawable.userpicicon));
		}		
		else
		{
			String imageURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL); 
			SBImageLoader.getInstance().displayImageElseStub(imageURL, userPic, R.drawable.nearbyusericon);
			String userName = ThisUserConfig.getInstance().getString(ThisUserConfig.FB_FULLNAME);
			userNameView.setText(userName);
			if("female".equalsIgnoreCase(ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER)))
			{
				maleIcon.setVisibility(View.GONE);
				femaleIcon.setVisibility(View.VISIBLE);
			}
			
		}
			
				
	}

	 @Override
	    public void onStart(){
	        super.onStart();
	        HopinTracker.sendView("SelfProfile");
	        HopinTracker.sendEvent("Profile","ScreenOpen","userprofile:self:open",1L);
	    }

    @Override
    public void onStop(){
        super.onStop();
       // EasyTracker.getInstance().activityStop(this);
    }
    
    @Override
	public void onPause(){
    	super.onPause();    	
        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);    	
    }

    class MyPageAdapter extends FragmentPagerAdapter {
    	
    	private List<Fragment> fragments;
    	public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {    
    	super(fm);
    
    	this.fragments = fragments;    	
    	}
    	
    	@Override    
    	public Fragment getItem(int position) {
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{			
			CommunicationHelper.getInstance().FBLoginpromptPopup_show(SelfProfileActivity.this, true) ;
		}
    	return this.fragments.get(position);   
    	
    	}
    
    	@Override    
    	public int getCount() {    
    	return this.fragments.size();   
    	}
    
    	}

}
