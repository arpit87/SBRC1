package in.co.hopin.MapHelpers;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.UserFBInfo;
import in.co.hopin.Util.StringUtils;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.maps.MapView;

public class NearbyUserOverlayItem extends BaseOverlayItem{

	private static String TAG = "in.co.hopin.MapHelpers.NearbyUserOverlayItem";

	protected SBMapView mMapView = null;
	private static Context context =MapListActivityHandler.getInstance().getUnderlyingActivity();
	protected static LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	private View viewOnMarkerSmall = null; 
	private View viewOnMarkerExpanded = null;	
	private TextView expandedBalloonHeader = null;
	private ImageView picViewSmall = null;
	private ImageView picViewExpanded = null;
	private ImageView chatIcon = null;
	//private ImageView smsIcon = null;
	private ImageView hopinIcon = null;
	private ImageView facebookIcon = null;
	private ImageView buttonClose = null;
	private SBGeoPoint mGeoPoint = null;
	private String mImageURL= "";
	private String mUserFBID= "";
	private String mUserName= "";
	private String mUserID = "";
	private String mUserFBName = "";
	boolean isVisibleSmall = false;
	boolean isVisibleExpanded = false;
	private NearbyUser mNearbyUser = null;
	private UserFBInfo mUserFBInfo = null;
	private ScrollView fbInfoScrollView = null;
    
    private int chatIconImgSrc;
  //private int smsIconImgSrc;
    private int hopinIconImgSrc;
    private int facebookIconImgSrc;
		
	public NearbyUserOverlayItem(NearbyUser user ,SBMapView mapView) {
		super(user.getUserLocInfo().getGeoPoint(), user.getUserFBInfo().getImageURL(), user.getUserFBInfo().getFbid());
		this.mGeoPoint = user.getUserLocInfo().getGeoPoint();		
		this.mMapView = mapView;
		this.mUserName = user.getUserOtherInfo().getUserName();
		this.mUserID = user.getUserOtherInfo().getUserID();
		this.mUserFBInfo = user.getUserFBInfo();
		this.mImageURL = mUserFBInfo.getImageURL();
		this.mUserFBID = mUserFBInfo.getFbid();
		this.mUserFBName = mUserFBInfo.getFullName();
		this.mNearbyUser = user;
		createAndDisplaySmallView();
		/*Drawable icon= Platform.getInstance().getContext().getResources().getDrawable(R.drawable.green_marker);
		icon.setBounds(0, 0, icon.getIntrinsicHeight(), icon.getIntrinsicWidth());
		this.mMarker = icon;*/
	}
	
	public SBGeoPoint getGeoPoint() {
		return mGeoPoint;
	}

	protected void createAndDisplaySmallView()
	{
		if(mMapView == null || mGeoPoint == null)
			return;
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mGeoPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		if(viewOnMarkerSmall==null)
		{	
			if(mNearbyUser.getUserOtherInfo().isOfferingRide())
				viewOnMarkerSmall = mInflater.inflate(R.layout.map_frame_layout_green, null);
			else
				viewOnMarkerSmall = mInflater.inflate(R.layout.map_frame_layout_blue, null);
			
			picViewSmall = (ImageView)viewOnMarkerSmall.findViewById(R.id.userpic);	
			
			viewOnMarkerSmall.setOnTouchListener(new NearbyUserOnTouchListener());
			
			if(mImageURL != "")
				SBImageLoader.getInstance().displayImageElseStub(mImageURL, picViewSmall, R.drawable.userpicicon);
			else
				picViewSmall.setImageDrawable( Platform.getInstance().getContext().getResources().getDrawable(R.drawable.nearbyusericon));
			//SBImageLoader.getInstance().displayImage(mImageURL, picViewSmall);
			
            mMapView.addNearbyUserView(viewOnMarkerSmall, params);
			viewOnMarkerSmall.setVisibility(View.VISIBLE);
			isVisibleSmall = true;
		}
			
	}
	
	private void setFBInfoOnExpandedBalloon(View balloonView,UserFBInfo userFBInfo)
	{
		TextView userNotLoggedIn = null;
		TextView fb_name = null;
		TextView works_at = null;
		TextView studied_at = null;
		TextView hometown = null;
		TextView gender = null;
		
		String name_str,worksat_str,studiedat_str,hometown_str,gender_str = "";
		userNotLoggedIn = (TextView)viewOnMarkerExpanded.findViewById(R.id.usernotloggedintext);
		
		if(!userFBInfo.FBInfoAvailable())
		{			
			userNotLoggedIn.setVisibility(View.VISIBLE);
			fbInfoScrollView.setVisibility(View.GONE);
			return;
		}		
		
		userNotLoggedIn.setVisibility(View.GONE);
		fbInfoScrollView.setVisibility(View.VISIBLE);
		fb_name = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_balloon_header);
		works_at = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_work);
		studied_at = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_education);
		hometown = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_from);
		gender = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_gender);
		
		name_str = mNearbyUser.getUserFBInfo().getFullName();
		worksat_str = mNearbyUser.getUserFBInfo().getWorksAt();
		studiedat_str = mNearbyUser.getUserFBInfo().getStudiedAt();
		hometown_str = mNearbyUser.getUserFBInfo().getHometown();
		gender_str = mNearbyUser.getUserFBInfo().getGender();
		
		fb_name.setText(name_str);		
		
		if(worksat_str!="null")
			works_at.setText(getSpannedText("Works at ", worksat_str));
		else
			works_at.setVisibility(View.GONE);
		
		if(studiedat_str!="null")
			studied_at.setText(getSpannedText("Studied at " ,studiedat_str));
		else
			studied_at.setVisibility(View.GONE);
		
		if(hometown_str!="null")
			hometown.setText(getSpannedText("HomeTown " , hometown_str));
		else
			hometown.setVisibility(View.GONE);
		
		if(gender_str!="null")
			gender.setText(getSpannedText("Gender ",gender_str));
		else
			gender.setVisibility(View.GONE);
		
	}
	
	private Spannable getSpannedText(String label, String text)
	{
		StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
		Spannable label_span = new SpannableString(label + " "+text);		
		label_span.setSpan(bold, 0,label.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return label_span;
	}
	
	protected void createAndDisplayExpandedView()
	{
		if(mMapView == null || mGeoPoint == null)
			return;
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mGeoPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		if(viewOnMarkerExpanded==null)
		{		
			removeSmallView();
			viewOnMarkerExpanded = mInflater.inflate(R.layout.map_expanded_layout, null);
			fbInfoScrollView = (ScrollView)viewOnMarkerExpanded.findViewById(R.id.expanded_bio_scroll);
			picViewExpanded = (ImageView)viewOnMarkerExpanded.findViewById(R.id.expanded_pic);		
			expandedBalloonHeader = (TextView)viewOnMarkerExpanded.findViewById(R.id.expanded_balloon_header);
			chatIcon = (ImageView)viewOnMarkerExpanded.findViewById(R.id.chat_icon_view);
			//smsIcon = (ImageView)viewOnMarkerExpanded.findViewById(R.id.sms_icon);
			hopinIcon = (ImageView)viewOnMarkerExpanded.findViewById(R.id.hopin_icon);
			facebookIcon = (ImageView)viewOnMarkerExpanded.findViewById(R.id.fb_icon_view);
			buttonClose = (ImageView)viewOnMarkerExpanded.findViewById(R.id.button_close_balloon_expandedview);
			
            chatIconImgSrc = R.drawable.chat_icon_blue_selector;
            //smsIconImgSrc = R.drawable.sms_icon;
            hopinIconImgSrc = R.drawable.launchernewmedium;
            facebookIconImgSrc = R.drawable.fb_icon_selector;

			if(!StringUtils.isBlank(mUserFBName))
				expandedBalloonHeader.setText(mUserFBName);
			else
				expandedBalloonHeader.setText(mUserName);
			
			if(/*!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN) ||*/ !mUserFBInfo.FBInfoAvailable())
			{
				chatIcon.setImageResource(R.drawable.chat_icon_blue_disabled);
				chatIcon.invalidate();
				hopinIcon.setImageResource(R.drawable.launchernewmedium_disabled);
				hopinIcon.invalidate();
				facebookIcon.setImageResource(R.drawable.fb_icon_disabled);
				facebookIcon.invalidate();

                chatIconImgSrc = R.drawable.chat_icon_blue_disabled;
                hopinIconImgSrc = R.drawable.launchernewmedium_disabled;
                facebookIconImgSrc = R.drawable.fb_icon_disabled;
            }
			
			
			/*if(!mNearbyUser.getUserFBInfo().isPhoneAvailable())
			{				
				smsIcon.setImageResource(R.drawable.sms_icon_disabled);
				smsIcon.invalidate();
                smsIconImgSrc = R.drawable.sms_icon_disabled;
			}*/
			
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					showSmallIfExpanded();
				}
				});
			
			/*smsIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onSmsClickWithUser(mUserID,mNearbyUser.getUserFBInfo().isPhoneAvailable());
				}
				});*/
			hopinIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onHopinProfileClickWithUser((FragmentActivity)context, mNearbyUser.getUserFBInfo());
				}
				});
			//SBImageLoader.getInstance().displayImageElseStub(mImageURL, picView, R.drawable.userpicicon);
			
			//set balloon info
			setFBInfoOnExpandedBalloon(viewOnMarkerExpanded,mNearbyUser.getUserFBInfo());
			
			
			SBImageLoader.getInstance().displayImage(this.mImageURL, picViewExpanded);
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onChatClickWithUser((FragmentActivity)context,mNearbyUser.getUserFBInfo().getFbid(),mNearbyUser.getUserFBInfo().getFullName());						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser((FragmentActivity)context,mUserFBID,mUserFBName);						
				}
			});		
						
		
            mMapView.addNearbyUserView(viewOnMarkerExpanded, params);
			viewOnMarkerExpanded.setVisibility(View.VISIBLE);
			isVisibleExpanded = true;
		}
			
	}
	
	public void removeExpandedView()
	{
		if(viewOnMarkerExpanded!=null && isVisibleExpanded == true)
		{
			viewOnMarkerExpanded.setVisibility(View.GONE);
			isVisibleExpanded = false;
		}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"trying to remove expanded null View");
        }
	}	
	
	public void removeSmallView()
	{
		if(viewOnMarkerSmall!=null && isVisibleSmall == true)
		{
			viewOnMarkerSmall.setVisibility(View.GONE);
			isVisibleSmall = false;
		}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"trying to remove null View");
        }
	}	
	
	public void toggleSmallView()
	{
		if(isVisibleSmall)
			removeSmallView();
		else
			showSmallView();
	}
	
	public void showSmallIfExpanded()
	{
		if(isVisibleExpanded)
		{
			removeExpandedView();
			showSmallView();
		}		
	}
	
	public void showExpandedIfSmall()
	{
		if(isVisibleSmall)
		{
			removeSmallView();
			showExpandedView();
		}		
	}
	
	public void showSmallView()
	{
		if(viewOnMarkerSmall!=null)
		{			
			viewOnMarkerSmall.setVisibility(View.VISIBLE);
			isVisibleSmall = true;
		}
		else
		{
			createAndDisplaySmallView();			
		}
	}
	
	public void showExpandedView()
	{
		if(viewOnMarkerExpanded!=null)
		{
            boolean isOtherUserFbInfoAvailable = mUserFBInfo.FBInfoAvailable();
            //boolean isOtherUserPhoneAvailable = mNearbyUser.getUserFBInfo().isPhoneAvailable();
            boolean isThisUserFbLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);

            if (!(isOtherUserFbInfoAvailable/* && isThisUserFbLoggedIn*/)){
                if (chatIconImgSrc != R.drawable.chat_icon_blue_disabled) {
                    chatIcon.setImageResource(R.drawable.chat_icon_blue_disabled);
                    chatIcon.invalidate();
                    chatIconImgSrc = R.drawable.chat_icon_blue_disabled;
                }
                
                if (facebookIconImgSrc != R.drawable.fb_icon_disabled){
                    facebookIcon.setImageResource(R.drawable.fb_icon_disabled);
                    facebookIcon.invalidate();
                    facebookIconImgSrc = R.drawable.fb_icon_disabled;
                }
            } else {
                if (chatIconImgSrc != R.drawable.chat_icon_blue_selector){
                    chatIcon.setImageResource(R.drawable.chat_icon_blue_selector);
                    chatIcon.invalidate();
                    chatIconImgSrc = R.drawable.chat_icon_blue_selector;
                }
                
                if (facebookIconImgSrc != R.drawable.fb_icon_selector){
                    facebookIcon.setImageResource(R.drawable.fb_icon_selector);
                    facebookIcon.invalidate();
                    facebookIconImgSrc = R.drawable.fb_icon_selector;
                }
            }

           /* if (!(isOtherUserPhoneAvailable && isThisUserFbLoggedIn)){
                if (smsIconImgSrc != R.drawable.sms_icon_disabled) {
                    smsIcon.setImageResource(R.drawable.sms_icon_disabled);
                    smsIcon.invalidate();
                    smsIconImgSrc = R.drawable.sms_icon_disabled;
                }
            } else {
                if (smsIconImgSrc != R.drawable.sms_icon){
                    smsIcon.setImageResource(R.drawable.sms_icon);
                    smsIcon.invalidate();
                    smsIconImgSrc = R.drawable.sms_icon;
                }
            }*/
			viewOnMarkerExpanded.setVisibility(View.VISIBLE);
			isVisibleExpanded = true;
		}
		else
		{
			createAndDisplayExpandedView();			
		}
	}

	
	public class NearbyUserOnTouchListener implements OnTouchListener
	{		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			removeSmallView();
			showExpandedView();
			MapListActivityHandler.getInstance().centreMapTo(mGeoPoint);
			return true;
		}
		
	}
	

}
