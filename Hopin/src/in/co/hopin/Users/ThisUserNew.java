package in.co.hopin.Users;

import android.util.Log;

import in.co.hopin.Adapter.HistoryAdapter;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Util.StringUtils;

import java.util.LinkedList;
/***
 * This class has latest data to set all current req data of this user
 * any activity to be updated like list map picks from this location
 * @author arpit87
 *
 */
public class ThisUserNew {
	
	
	private SBGeoPoint currentGeoPoint=null;
	private SBGeoPoint sourceGeoPoint=null;
	private SBGeoPoint destinationGeoPoint=null;
	private String sourceFullAddress = "";
	private String destinationFullAddress = "";
	private String sourceLocality = "";
	private String destiantionLocality = "";
	private String timeOfRequest = "";
	private String dateOfRequest = "";
	private int take_offer_type = 0; //0=>offer 1=>share
	private int daily_instant_type = 0;//pool 0.instant 1
	private int plan_instant_type = 0;//sent by plan tab 0.instant tab 1
	private int selected_radio_button_id = 0;//used in history to store which radio button was selected
	private static final String TAG = "in.co.hopin.Users.ThisUserNew";
	private static ThisUserNew instance = null;
	private LinkedList<HistoryAdapter.HistoryItem> historyItemList = new LinkedList<HistoryAdapter.HistoryItem>();
	private String userID;
	private String formattedTraveDetails;	
				
    public void reset(){
        currentGeoPoint = null;
        sourceGeoPoint = null;
        destinationGeoPoint = null;
        sourceFullAddress = "";
        destinationFullAddress = "";
        sourceLocality = "";
        destiantionLocality = "";
        timeOfRequest = "";
        dateOfRequest = "";
        take_offer_type = 0;
        daily_instant_type = 0;
        plan_instant_type = 0;
        selected_radio_button_id = 0;
        formattedTraveDetails  = "";
    }

	public void setUserID(String userID) {
		//Log.i(TAG,"set user id");
		this.userID = userID;
	}
	
	public String getUserID() {
		//Log.i(TAG,"get user id"+this.userID);
		return this.userID;
	}
	public static ThisUserNew getInstance() {
		if(instance == null)
			instance = new ThisUserNew();
		 return instance;
	}
	
	public void setCurrentGeoPointToSourceGeopoint() {
		sourceGeoPoint = currentGeoPoint;
	}
	
	public SBGeoPoint getCurrentGeoPoint() {
		return currentGeoPoint;
	}

	public SBGeoPoint getSourceGeoPoint() {
		return sourceGeoPoint;
	}
	public SBGeoPoint getDestinationGeoPoint() {
		return destinationGeoPoint;
	}
	public String getSourceFullAddress() {
		return sourceFullAddress;
	}
	public String getDestinationFullAddress() {
		return destinationFullAddress;
	}
	public String getSourceLocality() {
		return sourceLocality;
	}
	public String getDestiantionLocality() {
		return destiantionLocality;
	}	
	public void setCurrentGeoPoint(SBGeoPoint currentGeoPoint) {
		this.currentGeoPoint = currentGeoPoint;
	}
	public void setSourceGeoPoint(SBGeoPoint sourceGeoPoint) {
		this.sourceGeoPoint = sourceGeoPoint;
	}
	public void setDestinationGeoPoint(SBGeoPoint destinationGeoPoint) {
		this.destinationGeoPoint = destinationGeoPoint;
	}
	public void setSourceFullAddress(String sourceFullAddress) {
		this.sourceFullAddress = sourceFullAddress;
	}
	public void setDestinationFullAddress(String destinationFullAddress) {
		this.destinationFullAddress = destinationFullAddress;
	}
	public void setSourceLocality(String sourceLocality) {
		this.sourceLocality = sourceLocality;
	}
	public void setDestiantionLocality(String destiantionLocality) {
		this.destiantionLocality = destiantionLocality;
	}	
	/**
	 * this is in 24hr clock
	 * @return
	 */
	public String getTimeOfTravel() {
		return timeOfRequest;
	}

	public void setDateOfTravel(String dateOfRequest) {
		this.dateOfRequest = dateOfRequest;
	}	
	
	public String getDateOfTravel() {
		return dateOfRequest;
	}

	public void setTimeOfTravel(String timeOfRequest) {
		this.timeOfRequest = timeOfRequest;
	}
	
	public int getSelected_radio_button_id() {
		return selected_radio_button_id;
	}

	public void setSelected_radio_button_id(int selected_radio_button_id) {
		this.selected_radio_button_id = selected_radio_button_id;
	}

	public String getDetailsOfTravel()
	{
		String time = StringUtils.formatDate("HH:mm", "hh:mm a", getTimeOfTravel());
		if(get_Daily_Instant_Type() == 0)
			return "Daily@"+time;
		else
			return StringUtils.formatDate("yyyy-MM-dd","d MMM",getDateOfTravel()) +"," + time;
	}
	
	public String getDateAndTimeOfTravel() {
		return dateOfRequest + " " + timeOfRequest;
	}
	/**
	 * if offering => 1
	 * take => 0
	 * @return
	 */
	public int get_Take_Offer_Type() {
		return take_offer_type;
	}
	
	public void set_Take_Offer_Type(int i)
	{
		take_offer_type = i;
	}
	/**
	 * if instant => 1
	 * pool => 0
	 * @return
	 */
	public int get_Daily_Instant_Type() {		
			return daily_instant_type;
	}

	public void set_Daily_Instant_Type(int i)
	{
		daily_instant_type = i;
	}
	
	/**
	 * if instant tab => 1
	 * pool tab=> 0
	 * this is used to store history and display it
	 * @return
	 */
	public int get_Plan_Instant_Type() {		
			return plan_instant_type;
	}

	public void set_Plan_Instant_Type(int i)
	{
		plan_instant_type = i;
	}
	
	public LinkedList<HistoryAdapter.HistoryItem> getHistoryItemList(){
        return historyItemList;
    }

    public void setHistoryItemList(LinkedList<HistoryAdapter.HistoryItem> historyItemList){
        this.historyItemList = historyItemList;
    }
    
    public String getFormattedTravelDetails()
	{
		String travelInfo = getSourceLocality() + " to " + getDestiantionLocality();
		String travelTimeInfo = getTimeOfTravel();			
		if(ThisUserNew.getInstance().get_Daily_Instant_Type() == 0)
			formattedTraveDetails = travelInfo + " Daily@"+StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "hh:mm a", travelTimeInfo);
		
		else
			formattedTraveDetails = travelInfo +","+ StringUtils.formatDate("yyyy-MM-dd HH:mm:ss", "d MMM hh:mm a", travelTimeInfo);
		return formattedTraveDetails;
	}
    
    public static void clearAllData()
    {
    	instance = new ThisUserNew();
    }	

}
