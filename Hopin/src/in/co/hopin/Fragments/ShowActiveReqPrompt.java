package in.co.hopin.Fragments;

import in.co.hopin.Activities.MyChatsActivity;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.GetMatchingCarPoolUsersRequest;
import in.co.hopin.HttpClient.GetMatchingNearbyUsersRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Users.UserAttributes;
import in.co.hopin.Util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.co.hopin.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ShowActiveReqPrompt extends DialogFragment{
	TextView carpoolsource;
	TextView carpooldestination;
	TextView carpooltime;
	TextView instasource;
	TextView instadestination;
	TextView instatime;
	View instaActiveLayout;
	View carPoolActiveLayout;
	TextView carPoolNoActiveReq;
	TextView instaNoActiveReq;
	private static final String TAG = "in.co.hopin.Fragments.ShowActiveReqPrompt";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.show_active_req_prompt, container);
        carpoolsource = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_source);
        carpooldestination = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_destination);
        carpooltime = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_time);
        instasource = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_source);
        instadestination = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_destination);
        instatime = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_time);              
        instaActiveLayout = (View)dialogView.findViewById(R.id.show_active_req_prompt_instadetails);
        carPoolActiveLayout = (View)dialogView.findViewById(R.id.show_active_req_prompt_carpooldetails);
        carPoolNoActiveReq = (TextView)dialogView.findViewById(R.id.show_active_req_prompt_carpool_noreq);
        instaNoActiveReq = (TextView)dialogView.findViewById(R.id.show_active_req_prompt_insta_noreq);
        
        
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL); 
        
        if(!StringUtils.isBlank(carpoolReqJson))
        { 
        	try {
        		carPoolActiveLayout.setVisibility(View.VISIBLE);
            	carPoolNoActiveReq.setVisibility(View.GONE);
            	
    				final JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
    				String source = responseJsonObj.getString(UserAttributes.SRCLOCALITY);
    				String destination = responseJsonObj.getString(UserAttributes.DSTLOCALITY);
    				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
    				if(source.equalsIgnoreCase("null") || StringUtils.isBlank(source))
    					source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
    				if(destination.equalsIgnoreCase("null") || StringUtils.isBlank(destination))
    					destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
    				carpoolsource.setText(source);
    				carpooldestination.setText(destination);
    				carpooltime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime));
    							
            	carPoolActiveLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
								ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching carpool matches", "Please wait");
								SBHttpRequest getNearbyUsersRequest = new GetMatchingCarPoolUsersRequest();
						        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
						        dismiss(); 
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					});
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!StringUtils.isBlank(instaReqJson))
        {  
        
        	try {
        		
        		final JSONObject responseJsonObj = new JSONObject(instaReqJson);
        		String datetime = responseJsonObj.getString(UserAttributes.DATETIME);        		
				if(StringUtils.checkIfRequestExpired(datetime))
				{
					ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_INSTA, "");
					//ToastTracker.showToast("Active insta req expired");
				}
				else
				{
				
        		instaActiveLayout.setVisibility(View.VISIBLE);
            	instaNoActiveReq.setVisibility(View.GONE);
            					
				String source = responseJsonObj.getString(UserAttributes.SRCLOCALITY);
				String destination = responseJsonObj.getString(UserAttributes.DSTLOCALITY);
				if(source.equalsIgnoreCase("null") || StringUtils.isBlank(source))
					source = responseJsonObj.getString(UserAttributes.SRCADDRESS);
				if(destination.equalsIgnoreCase("null") || StringUtils.isBlank(destination))
					destination = responseJsonObj.getString(UserAttributes.DSTADDRESS);
				instasource.setText(source);
				instadestination.setText(destination);
				instatime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "d MMM, hh:mm a", datetime));
    					
				 instaActiveLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
								ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching  matches", "Please wait");
								SBHttpRequest getNearbyUsersRequest = new GetMatchingNearbyUsersRequest();
						        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
						        dismiss(); 
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					});
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }        
       
        
        
        Button newRequestButton = (Button)dialogView.findViewById(R.id.show_active_req_prompt_newreqbutton);
		// if button is clicked, close the custom dialog
        newRequestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        
        ImageView chat_button = (ImageView)dialogView.findViewById(R.id.show_active_req_prompt_chat);
        chat_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					dismiss();
				  Intent myChatsIntent = new Intent(getActivity(), MyChatsActivity.class);
			       myChatsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			       startActivity(myChatsIntent);
				
			}
		});
	       
		return dialogView;
	}
	
	
}
