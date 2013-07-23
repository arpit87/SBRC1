package in.co.hopin.HelperClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.hopin.R;
import in.co.hopin.Activities.OtherUserProfileActivityNew;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.ChatClient.ChatWindow;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.Fragments.FBLoginDialogFragment;
import in.co.hopin.HttpClient.ChatServiceCreateUser;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.HttpClient.SaveFBInfoRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.Logger;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/****
 * 
 * @author arpit87
 * handler code for various scenaiors on chat click here
 * like not logged in to server,not fb login yet etc
 * to start chat from anywhere call this class
 */
public class CommunicationHelper {
	
	private static String TAG = "in.co.hopin.ActivityHandler.ChatHandler";
	static CommunicationHelper instance = new CommunicationHelper();
	Context context = Platform.getInstance().getContext(); 
	private boolean isFBPromptShowing = false;
	PopupWindow fbPopupWindow;
	View popUpView = null;
	//private FacebookConnector fbconnect;
		
	public static CommunicationHelper getInstance()
	{
		return instance;
	}
	
	
	public void onChatClickWithUser(FragmentActivity underLyingActivity,String fbid,String full_name)
	{
		//chat username and id are set only after successful addition to chat server
		//if these missing =?not yet added on chat server
		
		String thiUserChatUserName = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
		String thisUserChatPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
		
		if(thiUserChatUserName == "" || thisUserChatPassword == "")
		{
			if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			{
				//make popup 
				FBLoginpromptPopup_show(underLyingActivity,true);
			}
			else 
			{
				if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG,"FBLogged in but not chat!!Server working properly for chat req?sending again");
				//sending fbinfo n chatreq again
				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
				{
					//server couldnt receive fbinfo
					SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
					SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);
				}
				
				SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
		     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);							
			}
			//Intent fbLoginIntent = new Intent(context,LoginActivity.class);			
			//MapListActivityHandler.getInstance().getUnderlyingActivity().startActivity(fbLoginIntent);
		}	
		else 
		{
			if(fbid!="" && full_name!="")
			{
				Intent startChatIntent = new Intent(Platform.getInstance().getContext(),ChatWindow.class);					
				startChatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
			 			| Intent.FLAG_ACTIVITY_NEW_TASK);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT, fbid);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, full_name);
				context.startActivity(startChatIntent);
			}
			else
				ToastTracker.showToast("Sorry, user is not logged in");
		}
		
	
	}
	
	public void onHopinProfileClickWithUser(FragmentActivity underLyingActivity,UserFBInfo fbInfo)
	{
		
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			FBLoginpromptPopup_show(underLyingActivity,true);
		}
		else if(fbInfo.FBInfoAvailable())
		{			
			Intent hopinNewProfile = new Intent(underLyingActivity,OtherUserProfileActivityNew.class);
	    	hopinNewProfile.putExtra("fb_info", fbInfo.getJsonObj().toString());
	    	hopinNewProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	underLyingActivity.startActivity(hopinNewProfile);
		}
		else
			ToastTracker.showToast("Sorry, user is not logged in");
	}
	
	public void onFBIconClickWithUser(FragmentActivity underlyingActivity, String userFBID, String userFBName)
	{
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			FBLoginpromptPopup_show(underlyingActivity,true);
		}
		else if(userFBID!="" || userFBName !="")
		{
			FacebookConnector.getInstance(underlyingActivity).openFacebookPage(userFBID,userFBName);
		}
		else
			ToastTracker.showToast("Not available, user not FB logged in");
	}
	
	public void FBLoginDialog_show(final FragmentActivity underlyingActivity)
	{
		MapListActivityHandler.getInstance().closeExpandedViews();
		FBLoginDialogFragment fblogin_dialog = FBLoginDialogFragment.newInstance(FacebookConnector.getInstance(underlyingActivity));
		fblogin_dialog.show(underlyingActivity.getSupportFragmentManager(), "fblogin_dialog");		
	}
	
	public void FBLoginpromptPopup_show(final FragmentActivity underlyingActivity , final boolean show)
		{		 	
		   //we keeping a track for which all activities we are showing prompt
		   //if already showing then blink it		 	
			if(show)
			{				
				if(!isFBPromptShowing )
				{	
					isFBPromptShowing = true;
					Logger.i(TAG,"showing fblogin prompt");	
					popUpView = (ViewGroup) underlyingActivity.getLayoutInflater().inflate(R.layout.fbloginpromptpopup, null);					
					fbPopupWindow = new PopupWindow(popUpView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,false); //Creation of popup					
					fbPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);   
					fbPopupWindow.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);    // Displaying popup							
			        fbPopupWindow.setTouchable(true);
			        fbPopupWindow.setFocusable(false);
			        //fbPopupWindow.setOutsideTouchable(true);
			        ViewGroup fbloginlayout = (ViewGroup)popUpView.findViewById(R.id.fbloginpromptloginlayout);
			        fbloginlayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							FBLoginDialog_show(underlyingActivity);
							fbPopupWindow.dismiss();
							isFBPromptShowing = false;	
							Logger.i(TAG,"fblogin prompt clicked");
						}
					});
			        ImageView buttonClosefbprompt = (ImageView) popUpView.findViewById(R.id.fbloginpromptclose);		        
			        buttonClosefbprompt.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							fbPopupWindow.dismiss();
							isFBPromptShowing = false;	
						}
					});
				}
				else
				{
					//will flicker prompt here if already showing
					TextView fblogintext = (TextView) popUpView.findViewById(R.id.fbloginprompttext);
					Animation anim = new AlphaAnimation(0.0f, 1.0f);
			        anim.setDuration(50); //You can manage the time of the blink with this parameter
			        anim.setStartOffset(20);
			        anim.setRepeatMode(Animation.REVERSE);
			        anim.setRepeatCount(6);
			        fblogintext.startAnimation(anim);				
				}
				//popUpView.setBackgroundResource(R.drawable.transparent_black);
			}
			else
			{
				if(isFBPromptShowing && fbPopupWindow!=null && fbPopupWindow.isShowing())
					fbPopupWindow.dismiss();
				isFBPromptShowing = false;
			}
							
		}

	public void authorizeCallback(FragmentActivity underlaying_activity,int requestCode, int resultCode, Intent data) {
		FacebookConnector.getInstance(underlaying_activity).authorizeCallback(requestCode, resultCode, data);
		
	}
		
	

}
