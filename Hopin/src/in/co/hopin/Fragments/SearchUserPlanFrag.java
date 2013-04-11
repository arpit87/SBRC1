package in.co.hopin.Fragments;

import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.co.hopin.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SearchUserPlanFrag extends AbstractSearchInputFrag implements SeekBar.OnSeekBarChangeListener{
	
	View mPlanViewContainer;
	RadioGroup radio_group_daily_onetime;	
	RadioButton enterDateButton;
	ToggleButton am_pm_toggle;
	TextView timeView;
	SeekBar timeSeekbar;
	//these change as user chooses time
    String hourStr = "";
	String minstr = "";	
	int hour;
	int minutes;
	String datePickerDate = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(mPlanViewContainer == null){
        	mPlanViewContainer = inflater.inflate(R.layout.search_users_plan_fragment, null);
        	radio_group_daily_onetime = (RadioGroup)mPlanViewContainer.findViewById(R.id.search_user_plan_radio_group);
        	radio_group_daily_onetime.check(R.id.search_user_plan_radiobutton_daily);
        	enterDateButton = (RadioButton) mPlanViewContainer.findViewById(R.id.search_user_plan_radiobutton_enterdate);
        	source = (AutoCompleteTextView) mPlanViewContainer.findViewById(R.id.search_user_plan_source);
            destination = (AutoCompleteTextView) mPlanViewContainer.findViewById(R.id.search_user_plan_destination);
            takeRideButton = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_takeride);
            offerRideButton = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_offerride);
            cancelFindUsers = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_cancelfindusers);
            timeSeekbar = (SeekBar) mPlanViewContainer.findViewById(R.id.search_user_plan_timeseekBar);
            am_pm_toggle = (ToggleButton)mPlanViewContainer.findViewById(R.id.search_user_plan_btn_am_pm_toggle);
            timeView = (TextView)mPlanViewContainer.findViewById(R.id.search_user_plan_time);
            destination_progressbar = (ProgressBar)mPlanViewContainer.findViewById(R.id.search_user_plan_destinationprogress);
            source_progressbar = (ProgressBar)mPlanViewContainer.findViewById(R.id.search_user_plan_sourceprogress);
        }		
		
	    timeSeekbar.setMax(48);
	    timeSeekbar.setOnSeekBarChangeListener(this);	               
        
        //find current time and set seekbar to just after current
        Calendar now = Calendar.getInstance();
        hour = now.get(Calendar.HOUR);        
        minutes = now.get(Calendar.MINUTE);        
        if(hour == 12)
        	hour = 0;
        int progress  = hour*4 + (int)(minutes/15);
        if(now.get(Calendar.AM_PM) == 0)
        	am_pm_toggle.setChecked(true);
        else
        	am_pm_toggle.setChecked(false);
        
        timeSeekbar.setProgress((progress+1)%48);
        
        enterDateButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				DialogFragment newFragment = new SBDatePickerFragment();
			    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");				
			}
		});
        super.onCreate(savedInstanceState);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, null);
        if(mPlanViewContainer == null){
        	mPlanViewContainer = inflater.inflate(R.layout.search_users_plan_fragment, null);
        	radio_group_daily_onetime = (RadioGroup)mPlanViewContainer.findViewById(R.id.search_user_plan_radio_group);
        	radio_group_daily_onetime.check(R.id.search_user_plan_radiobutton_daily);
        	enterDateButton = (RadioButton) mPlanViewContainer.findViewById(R.id.search_user_plan_radiobutton_enterdate);
        	source = (AutoCompleteTextView) mPlanViewContainer.findViewById(R.id.search_user_plan_source);
            destination = (AutoCompleteTextView) mPlanViewContainer.findViewById(R.id.search_user_plan_destination);
            takeRideButton = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_takeride);
            offerRideButton = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_offerride);
            cancelFindUsers = (Button)mPlanViewContainer.findViewById(R.id.search_user_plan_cancelfindusers);
            timeSeekbar = (SeekBar) mPlanViewContainer.findViewById(R.id.search_user_plan_timeseekBar);
            destination_progressbar = (ProgressBar)mPlanViewContainer.findViewById(R.id.search_user_plan_destinationprogress);
            source_progressbar = (ProgressBar)mPlanViewContainer.findViewById(R.id.search_user_plan_sourceprogress);
        }
                
        return mPlanViewContainer;
	}
		

	@Override
	public String getSource() {
		if(source != null)
			return source.getText().toString();
		else return "";
	}

	@Override
	public String getDestination() {
		if(destination != null)
			return destination.getText().toString();
		else return "";
	}

	@Override
	public String getDate() {
		String date = StringUtils.gettodayDateInFormat("yyyy-MM-dd");
		int checkedButton = radio_group_daily_onetime.getCheckedRadioButtonId();
		if(checkedButton == R.id.search_user_plan_radiobutton_enterdate)
		{
			date = datePickerDate;			
		}
		else if(checkedButton == R.id.search_user_plan_radiobutton_tomo)
		{
			date = StringUtils.getFutureDateInformat(1, "yyyy-MM-dd");
			
		}else if(checkedButton == R.id.search_user_plan_radiobutton_today)
		{
			date = StringUtils.gettodayDateInFormat("yyyy-MM-dd");
			
		}
		return date;
	}

	@Override
	public String getTime() {		
		String time24HrFormat = "" ;
		if(am_pm_toggle.isChecked())
			time24HrFormat = Integer.toString(hour) +":" + Integer.toString(minutes);
		else 
			time24HrFormat = Integer.toString(hour+12) + ":" + Integer.toString(minutes);
		
		return time24HrFormat;
	}

	@Override
	public int getDailyInstaType() {
		int checkedButton = radio_group_daily_onetime.getCheckedRadioButtonId();
		if(checkedButton == R.id.search_user_plan_radiobutton_daily)
		{			
			return 0;
		}
		else
		    return 1;		
	}

	/***
	 * we set the source geoPoint to current if user chooses "my location"
	 */
	@Override
	public SBGeoPoint getSourceGeopoint() {		
		if(!sourceSet)
			return	ThisUserNew.getInstance().getCurrentGeoPoint();
		else
			return null;
	}

	
	@Override
	public SBGeoPoint getDestinationGeopoint() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
    public void onProgressChanged(SeekBar seekBar, int progress,
    		boolean fromUser) {		
		hour = progress/4;
		minutes = (progress%4)*15;
		
		
		if(hour == 0)
			hourStr = "12";
		else if (hour > 0 && hour < 10)
		    hourStr = "0" + Integer.toString(hour);
		else
			hourStr = Integer.toString(hour);
		
		if(minutes == 0)
			minstr = "00";	
		else
			minstr = Integer.toString(minutes);
		
		timeView.setText(hourStr+":"+minstr);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public class SBDatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
     
	// Use the current date as the default date in the picker
	Calendar c = Calendar.getInstance();
	int myear = c.get(Calendar.YEAR);
	int mmonth = c.get(Calendar.MONTH);
	int mday = c.get(Calendar.DAY_OF_MONTH);
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	// Create a new instance of DatePickerDialog and return it
	return new DatePickerDialog(getActivity(), this, myear, mmonth, mday+2);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		c.set(year, month, day);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date travelDate = c.getTime();				
		String date = dateFormat.format(travelDate);
		datePickerDate = date;	
	}
	}

	@Override
	public int getPlanInstaTabType() {
		// TODO Auto-generated method stub
		return 0;
	}		

	@Override
	public int getRadioButtonID() {
		// TODO Auto-generated method stub
		return radio_group_daily_onetime.getCheckedRadioButtonId();
	}

}
