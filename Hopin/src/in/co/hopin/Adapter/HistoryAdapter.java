package in.co.hopin.Adapter;

import in.co.hopin.Activities.SearchInputActivityNew;
import in.co.hopin.Util.StringUtils;

import java.util.List;

import in.co.hopin.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter{

    private List<HistoryItem> historyItemList;
    private LayoutInflater inflater;
    private Activity underlyingActiviy = null;

    public HistoryAdapter(Activity activity, List<HistoryItem> historyItemList){
        this.historyItemList = historyItemList;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.underlyingActiviy = activity;
    }

    @Override
    public int getCount() {
        return historyItemList.size();
    }

    @Override
    public HistoryItem getItem(int i) {
        return historyItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HistoryItem historyItem = historyItemList.get(position);
        View view=convertView;
        if(convertView==null) {
            view = inflater.inflate(R.layout.history_list_row, null);
        }
                      
        TextView source = (TextView)view.findViewById(R.id.history_source);
        TextView destination = (TextView)view.findViewById(R.id.history_destination);        
        TextView details = (TextView)view.findViewById(R.id.history_details);     
       // ImageView edit_button = (ImageView)view.findViewById(R.id.history_editbutton);
        TextView reqDateView = (TextView)view.findViewById(R.id.history_req_date);        
        int type = historyItem.getPlanInstantType();//plan 0,insta 1       
        String reqdate = historyItem.getReqDate();
        source.setText(historyItem.getSource());
        destination.setText(historyItem.getDestination());
        details.setText(historyItem.getTimeforHistoryLayout());
        reqDateView.setText(reqdate); 
	    return view;
    }

    public static class HistoryItem {      
        Integer dailyInstantType;
        Integer planInstantType;
        Integer takeOffer;        
        String reqDate;   
        String source;
		String destination;
        String timeOfTravel;
        String dateOfTravel;
        String details; 
        int radioButtonID;

        public HistoryItem( String source, String destination,String timeOfTravel,String dateOfTravel,
        		             int dailyInstantType,int planInstantType, int takeOffer, String reqdate,
        		             int radioButtonID
        		           ) {           
            this.planInstantType = planInstantType;
            this.dailyInstantType = dailyInstantType;
            this.takeOffer = takeOffer;            
            this.reqDate = reqdate;
            this.source = source;
            this.destination = destination;
            this.timeOfTravel = timeOfTravel;
            this.dateOfTravel = dateOfTravel;
            this.radioButtonID = radioButtonID;
            processData();
        }      

        private void processData()
        {
        	//this build the string to show in Time: of history layout
        	if(StringUtils.isBlank(source))
        		source = "My Location";
        	if(dailyInstantType == 0)//dailypool
        	{
        		details = "DailyPool,"+StringUtils.formatDate("HH:mm", "hh:mm a", timeOfTravel);
        	}
        	else
        	{
        		//can get 5 min 15 min,today,tomo,enter date type        		
        		switch(radioButtonID)
        		{
        			case R.id.search_user_insta_radiobutton_5min:
        				timeOfTravel = StringUtils.getFutureTimeInformat(5, "HH:mm");
        				dateOfTravel = StringUtils.gettodayDateInFormat("yyyy-MM-dd");
        				details = "in 5min";
        			break;
        			case R.id.search_user_insta_radiobutton_15min:
        				timeOfTravel = StringUtils.getFutureTimeInformat(15, "HH:mm");
        				dateOfTravel = StringUtils.gettodayDateInFormat("yyyy-MM-dd");
        				details = "in 15min";
        			break;
        			case R.id.search_user_insta_radiobutton_30min:
        				timeOfTravel = StringUtils.getFutureTimeInformat(30, "HH:mm");
        				dateOfTravel = StringUtils.gettodayDateInFormat("yyyy-MM-dd");
        				details = "in 30min";
        			break;	
        			case R.id.search_user_plan_radiobutton_today:        				
        				dateOfTravel = StringUtils.gettodayDateInFormat("yyyy-MM-dd");        				
        				details = "Today,"+StringUtils.formatDate("HH:mm", "hh:mm a", timeOfTravel);
        			break;
        			case R.id.search_user_plan_radiobutton_tomo:        				
        				dateOfTravel = StringUtils.getFutureDateInformat(1,"yyyy-MM-dd");
        				details = "Tomorrow,"+StringUtils.formatDate("HH:mm", "hh:mm a", timeOfTravel);
        			break;
        			case R.id.search_user_plan_radiobutton_enterdate:        				
        				String dateTime = dateOfTravel + " " + timeOfTravel;
        				details = StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM,hh:mm a", dateTime);
        			break;
        				
        		}
        	}
        }
        
        public String getTimeforHistoryLayout() {
            return details;
        }
        public int getPlanInstantType() {
            return planInstantType;
        }

        public Integer getDailyInstantType() {
			return dailyInstantType;
		}

		public void setDailyInstantType(Integer dailyInstantType) {
			this.dailyInstantType = dailyInstantType;
		}

		public int getTakeOffer() {
            return takeOffer;
        }

        
        /**
         * it of format d MMM,ie.  14 Feb
         * @return
         */
        public String getReqDate() {
            return reqDate;
        }
        
        public String getSource() {
			return source;
		}

		public String getDestination() {
			return destination;
		}

		public String getTimeOfTravel() {
			return timeOfTravel;
		}

		public String getDateOfTravel() {
			return dateOfTravel;
		}
        

		
    }
}

