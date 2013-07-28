package in.co.hopin.MapHelpers;


import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.GridViewImageAdapter;
import in.co.hopin.CustomViewsAndListeners.SBMapView;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.NearbyUser;
import in.co.hopin.Users.NearbyUserGroup;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.maps.MapView;

public class GroupedNearbyUsersOverlayItem extends BaseOverlayItem{
	
	private static String TAG = "in.co.hopin.MapHelpers.NearbyUserOverlayItem";

	protected SBMapView mMapView = null;
	private static Context context =MapListActivityHandler.getInstance().getUnderlyingActivity();
	protected static LayoutInflater mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	private View viewOnMarkerSmall = null; 
	private View viewOnMarkerExpanded = null;	
	private View viewOnMarkerIndividualExpanded = null;	
	public SBGeoPoint mGeoPoint = null;
	boolean isVisibleSmall = false;
	boolean isVisibleExpanded = false;
	boolean isVisibleExpandedIndividual = false;	
	private TextView textViewNumberOfUSers = null;
	private ScrollView fbInfoScrollView = null;
	
	private NearbyUserGroup mUserGroup = null;
	private ImageView picViewExpanded = null ;		
	private TextView expandedBalloonHeader = null;
	private ImageView chatIcon = null;
	//private ImageView smsIcon = null;
	private ImageView hopinIcon = null;
	private ImageView facebookIcon = null ;
	private ImageView buttonClose = null;
	
	 
    private int chatIconImgSrc;
    //private int smsIconImgSrc;
    private int hopinIconImgSrc;
    private int facebookIconImgSrc;

	public GroupedNearbyUsersOverlayItem(NearbyUserGroup user_group ,SBMapView mapView) {	
		super(user_group.getGeoPointOfGroup(), "", "");
		this.mUserGroup = user_group;
		this.mGeoPoint = user_group.getGeoPointOfGroup();		
		this.mMapView = mapView;
		createAndDisplaySmallView();
	}
	
	public SBGeoPoint getGeopoint()
	{
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
			
			viewOnMarkerSmall = mInflater.inflate(R.layout.map_frame_multipleusers, null);			
			
			textViewNumberOfUSers = (TextView)viewOnMarkerSmall.findViewById(R.id.map_frame_multipleusers_numbertextview);	
			textViewNumberOfUSers.setText(((Integer)mUserGroup.getNumberOfUsersInGroup()).toString());
			
			viewOnMarkerSmall.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					removeSmallView();
					createAndDisplayExpandedView();
					MapListActivityHandler.getInstance().centreMapTo(mGeoPoint);
					return true;
				}
			});		
			
            mMapView.addNearbyUserView(viewOnMarkerSmall, params);
			viewOnMarkerSmall.setVisibility(View.VISIBLE);
			isVisibleSmall = true;
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
	
	public void removeIndividualUserExpandedView()
	{
		if(viewOnMarkerIndividualExpanded!=null)
		{
			viewOnMarkerIndividualExpanded.setVisibility(View.GONE);			
		}
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG,"trying to remove expanded null View");
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

	public void showIndividualUserExpandedView(final NearbyUser n)
	{
		//to expand view if not yet expanded or chage info on current view
		if(viewOnMarkerIndividualExpanded!=null)
		{
            boolean isOtherUserFbInfoAvailable = n.getUserFBInfo().FBInfoAvailable();
            //boolean isOtherUserPhoneAvailable = n.getUserFBInfo().isPhoneAvailable();
            boolean isThisUserFbLoggedIn = ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN);

            if (!(isOtherUserFbInfoAvailable)){
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
                
                if (hopinIconImgSrc != R.drawable.launchernewmedium_disabled){
                	hopinIcon.setImageResource(R.drawable.launchernewmedium_disabled);
                	hopinIcon.invalidate();
                	hopinIconImgSrc = R.drawable.launchernewmedium_disabled;
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
                
                if (hopinIconImgSrc != R.drawable.launchernewmedium){
                	hopinIcon.setImageResource(R.drawable.launchernewmedium);
                	hopinIcon.invalidate();
                	hopinIconImgSrc = R.drawable.launchernewmedium;
                }
            }

          /*  if (!(isOtherUserPhoneAvailable && isThisUserFbLoggedIn)){
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
            
            /*smsIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onSmsClickWithUser(n.getUserOtherInfo().getUserID(),n.getUserFBInfo().isPhoneAvailable());
				}
				});*/
            	hopinIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onHopinProfileClickWithUser((FragmentActivity)context, n.getUserFBInfo());
				}
				});
			//SBImageLoader.getInstance().displayImageElseStub(mImageURL, picView, R.drawable.userpicicon);
			
			//set balloon info
			setFBInfoOnExpandedBalloon(viewOnMarkerIndividualExpanded,n.getUserFBInfo());
			

			if(!StringUtils.isBlank(n.getUserFBInfo().getFullName()))
				expandedBalloonHeader.setText(n.getUserFBInfo().getFullName());
			else
				expandedBalloonHeader.setText(n.getUserOtherInfo().getUserName());
			
			SBImageLoader.getInstance().displayImageElseStub(n.getUserFBInfo().getImageURL(), picViewExpanded,R.drawable.userpicicon);
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onChatClickWithUser((FragmentActivity)context,n.getUserFBInfo().getFbid(),n.getUserFBInfo().getFullName());						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser((FragmentActivity)context,n.getUserFBInfo().getFbid(),n.getUserFBInfo().getFBUsername());						
				}
			});
           
            viewOnMarkerIndividualExpanded.setVisibility(View.VISIBLE);
			
		}
		else
		{
			createAndDisplayIndividualUserExpandedView(n);			
		}
	}

	
	protected void createAndDisplayIndividualUserExpandedView(final NearbyUser n)
	{
		//only to expand view for the first time
		if(mMapView == null || mGeoPoint == null)
			return;
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mGeoPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
				
		viewOnMarkerIndividualExpanded = mInflater.inflate(R.layout.map_expanded_layout, null);
		 mMapView.addNearbyUserView(viewOnMarkerIndividualExpanded, params);			
		 fbInfoScrollView = (ScrollView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_bio_scroll);
		 picViewExpanded = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_pic);		
		 expandedBalloonHeader = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_balloon_header);		
		 chatIcon = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.chat_icon_view);
		 //smsIcon = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.sms_icon);
		 hopinIcon = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.hopin_icon);
		 facebookIcon = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.fb_icon_view);
		 buttonClose = (ImageView)viewOnMarkerIndividualExpanded.findViewById(R.id.button_close_balloon_expandedview);
		
	
		if(!StringUtils.isBlank(n.getUserFBInfo().getFullName()))
			expandedBalloonHeader.setText(n.getUserFBInfo().getFullName());
		else
			expandedBalloonHeader.setText(n.getUserOtherInfo().getUserName());
		
		/*if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
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
		else*/ if(!n.getUserFBInfo().FBInfoAvailable())
		{
			chatIcon.setImageResource(R.drawable.chat_icon_blue_disabled);
			chatIcon.invalidate();				
			facebookIcon.setImageResource(R.drawable.fb_icon_disabled);
			facebookIcon.invalidate();
			hopinIcon.setImageResource(R.drawable.launchernewmedium_disabled);
			hopinIcon.invalidate();
            chatIconImgSrc = R.drawable.chat_icon_blue_disabled;              
            facebookIconImgSrc = R.drawable.fb_icon_disabled;
            hopinIconImgSrc = R.drawable.launchernewmedium_disabled;
		}
		
		/*if(!n.getUserFBInfo().isPhoneAvailable())
		{				
			smsIcon.setImageResource(R.drawable.sms_icon_disabled);
			smsIcon.invalidate();
            smsIconImgSrc = R.drawable.sms_icon_disabled;
          
		}*/
					
		buttonClose.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View buttonClose) {
				removeIndividualUserExpandedView();
				createAndDisplayExpandedView();
			}
			});
		
		/*smsIcon.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View buttonClose) {
				CommunicationHelper.getInstance().onSmsClickWithUser(n.getUserOtherInfo().getUserID(),n.getUserFBInfo().isPhoneAvailable());
			}
			});*/
		
		hopinIcon.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View buttonClose) {
				CommunicationHelper.getInstance().onHopinProfileClickWithUser((FragmentActivity)context, n.getUserFBInfo());
			}
			});
		//SBImageLoader.getInstance().displayImageElseStub(mImageURL, picView, R.drawable.userpicicon);
		
		//set balloon info
		setFBInfoOnExpandedBalloon(viewOnMarkerIndividualExpanded,n.getUserFBInfo());
		
		
		SBImageLoader.getInstance().displayImage(n.getUserFBInfo().getImageURL(), picViewExpanded);
		
		chatIcon.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View chatIconView) {
				CommunicationHelper.getInstance().onChatClickWithUser((FragmentActivity)context,n.getUserFBInfo().getFbid(),n.getUserFBInfo().getFullName());						
			}
		});
		
		facebookIcon.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View chatIconView) {
				CommunicationHelper.getInstance().onFBIconClickWithUser((FragmentActivity)context,n.getUserFBInfo().getFbid(),n.getUserFBInfo().getFBUsername());						
			}
		});
       
        viewOnMarkerIndividualExpanded.setVisibility(View.VISIBLE);
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
			viewOnMarkerExpanded = mInflater.inflate(R.layout.map_expanded_grid_layout, null);			 
			GridView picGridView = (GridView) viewOnMarkerExpanded.findViewById(R.id.gridview);
			
			GridViewImageAdapter gridViewAdapter = new GridViewImageAdapter(mUserGroup.getAllFBIds());
			picGridView.setAdapter(gridViewAdapter);
			picGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int position, long id) {
					removeExpandedView();
					showIndividualUserExpandedView(mUserGroup.getNearbyUserAt(position));
					
				}
				
				
			});
			ImageView buttonClose = (ImageView)viewOnMarkerExpanded.findViewById(R.id.map_expanded_grid_buttonclose);
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					removeExpandedView();
					showSmallView();
				}
				});			
			LayoutParams gridParameters = picGridView.getLayoutParams();
			int numUsers = gridViewAdapter.getCount();
			if(numUsers <= 3)
			{
				gridParameters.width = numUsers * 100;
				picGridView.setNumColumns(numUsers);
				//picGridView.setLayoutParams(gridParameters);
				
			}
			else
			{
				gridParameters.width = 4 * 100;
				if(numUsers > 8)
				{
					gridParameters.height = 3 * 120;
				}
				picGridView.setNumColumns(4);
				//picGridView.setLayoutParams(gridParameters);
				
			}
			
            mMapView.addNearbyUserView(viewOnMarkerExpanded, params);        
            //LinearLayout.LayoutParams headerWithParam = new LinearLayout.LayoutParams(picGridView.getLayoutParams().width, LayoutParams.WRAP_CONTENT);
            // expandedBalloonHeaderLayout.setLayoutParams(headerWithParam);
			viewOnMarkerExpanded.setVisibility(View.VISIBLE);
			isVisibleExpanded = true;
		}
		else
		{
			viewOnMarkerExpanded.setVisibility(View.VISIBLE);
			isVisibleExpanded = true;
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
		userNotLoggedIn = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.usernotloggedintext);
		
		if(!userFBInfo.FBInfoAvailable())
		{			
			userNotLoggedIn.setVisibility(View.VISIBLE);
			fbInfoScrollView.setVisibility(View.GONE);
			return;
		}
		
		userNotLoggedIn.setVisibility(View.GONE);
		fbInfoScrollView.setVisibility(View.VISIBLE);
		fb_name = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_balloon_header);
		works_at = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_work);
		studied_at = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_education);
		hometown = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_from);
		gender = (TextView)viewOnMarkerIndividualExpanded.findViewById(R.id.expanded_gender);
		
		name_str = userFBInfo.getFullName();
		worksat_str = userFBInfo.getWorksAt();
		studiedat_str = userFBInfo.getStudiedAt();
		hometown_str = userFBInfo.getHometown();
		gender_str = userFBInfo.getGender();
		
		if(name_str!="")
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
	
	public void toggleSmallView()
	{
		if(isVisibleSmall)
			removeSmallView();
		else
			showSmallView();
	}
	
	public void showOnlySmallView()
	{
		if(!isVisibleSmall){
			removeExpandedView();
			removeIndividualUserExpandedView();
			showSmallView();
		}
	}
	
	

}
