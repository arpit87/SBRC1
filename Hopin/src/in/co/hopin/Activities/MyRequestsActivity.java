package in.co.hopin.Activities;

import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.ActivityHandlers.MyRequestActivityHandler;
import in.co.hopin.HelperClasses.BroadCastConstants;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HttpClient.*;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyRequestsActivity extends Activity {
	
	private final String TAG = "in.co.hopin.Avtivity.MyRequestActivity";
	TextView carpoolsource;
	TextView carpooldestination;
	TextView carpooltime;
	TextView instasource;
	TextView instadestination;
	TextView instatime;
	Button deleteCarpoolReq;
	Button deleteInstaReq;	
	View instaActiveLayout;
	View carPoolActiveLayout;
	TextView carPoolNoActiveReq;
	TextView instaNoActiveReq;
	MyRequestActivityHandler reqHandler = null; // this receives broadcast on response post delete
    private Button showUsersInsta;
    private Button showUsersCarpool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_requests_layout);  
        carpoolsource = (TextView) findViewById(R.id.my_requests_carpool_source);
        carpooldestination = (TextView) findViewById(R.id.my_requests_carpool_destination);
        carpooltime = (TextView) findViewById(R.id.my_requests_carpool_details);
        instasource = (TextView) findViewById(R.id.my_requests_insta_source);
        instadestination = (TextView) findViewById(R.id.my_requests_insta_destination);
        instatime = (TextView) findViewById(R.id.my_requests_insta_details);
        showUsersInsta = (Button) findViewById(R.id.show_users_insta);
        showUsersCarpool = (Button) findViewById(R.id.show_users_carpool);
        deleteCarpoolReq = (Button) findViewById(R.id.my_requests_carpool_deletereq);
        deleteInstaReq = (Button) findViewById(R.id.my_requests_insta_deletereq);      
        instaActiveLayout = (View)findViewById(R.id.my_requests_instareq_layout);
        carPoolActiveLayout = (View)findViewById(R.id.my_requests_carpoolreq_layout);
        carPoolNoActiveReq = (TextView)findViewById(R.id.my_requests_carpool_noactivereq);
        instaNoActiveReq = (TextView)findViewById(R.id.my_requests_insta_noactivereq);
        reqHandler = new MyRequestActivityHandler(this);
        deleteCarpoolReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				registerReceiver(reqHandler, new IntentFilter(BroadCastConstants.CARPOOLREQ_DELETED));
				ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting carpool request", "Please wait");
				DeleteRequest deleteRequest = new DeleteRequest(0);
                SBHttpClient.getInstance().executeRequest(deleteRequest);
                
			}
		});
        
		deleteInstaReq.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View paramView) {
						HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:dailycarpool:delete",1L);
						HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:onetime:delete",1L);
						registerReceiver(reqHandler, new IntentFilter(BroadCastConstants.INSTAREQ_DELETED));
						ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting insta request", "Please wait");
						DeleteRequest deleteRequest = new DeleteRequest(1);
		                SBHttpClient.getInstance().executeRequest(deleteRequest);
		                
					}
				});

        showUsersCarpool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            	HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:dailycarpool:showusers",1L);
                try {
                    String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
                    final JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
                    MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
                    ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching carpool matches", "Please wait");
                    SBHttpRequest getNearbyUsersRequest = new DailyCarPoolRequest();
                    SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
                    finish();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        showUsersInsta.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                	HopinTracker.sendEvent("MyRequest","ButtonClick","myrequests:click:onetime:showusers",1L);
                    String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
                    final JSONObject responseJsonObj = new JSONObject(instaReqJson);
                    MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
                    ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching matches", "Please wait");
                    SBHttpRequest getNearbyUsersRequest = new InstaRequest();
                    SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
                    finish();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
       
    }

    @Override
    protected void onResume(){
    	super.onResume();
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);   
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"carpooljson:"+carpoolReqJson);
        if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"instajson:"+instaReqJson);
        if(!StringUtils.isBlank(carpoolReqJson))
        {
        	carPoolActiveLayout.setVisibility(View.VISIBLE);
        	carPoolNoActiveReq.setVisibility(View.GONE);
        	
        	try {
				JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
				String source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
				String destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
				carpoolsource.setText(source);
				carpooldestination.setText(destination);
				carpooltime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!StringUtils.isBlank(instaReqJson))
        {   
        	instaActiveLayout.setVisibility(View.VISIBLE);
        	instaNoActiveReq.setVisibility(View.GONE);
        	try {
				JSONObject responseJsonObj = new JSONObject(instaReqJson);
				String source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
				String destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
				instasource.setText(source);
				instadestination.setText(destination);
				instatime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM, hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    
    public void setCarpoolReqLayoutToNoActiveReq()
    {
    	carPoolActiveLayout.setVisibility(View.GONE);
    	carPoolNoActiveReq.setVisibility(View.VISIBLE);
    	unregisterReceiver(reqHandler);
    }
    
    public void setInstaReqLayoutToNoActiveReq()
    {
    	instaActiveLayout.setVisibility(View.GONE);
    	instaNoActiveReq.setVisibility(View.VISIBLE);
    	unregisterReceiver(reqHandler);
    }

    @Override
    public void onStart(){
        super.onStart();
        HopinTracker.sendView("MyRequests");
        HopinTracker.sendEvent("MyRequests","ScreenOpen","myrequests:open",1L);
        //EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        //EasyTracker.getInstance().activityStop(this);
    }
    
    @Override
	public void onBackPressed() {
    	super.onBackPressed();
    	HopinTracker.sendEvent("MyRequest","BackButton","myrequests:click:back",1L);
    }
}
