package in.co.hopin.Fragments;

import in.co.hopin.R;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.HistoryAdapter;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.AddThisUserScrDstCarPoolRequest;
import in.co.hopin.HttpClient.AddThisUserSrcDstRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.LocationHelpers.SBGeoPoint;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.provider.HistoryContentProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public abstract class AbstractSearchInputFrag extends Fragment{
	
	private static final String TAG = "in.co.hopin.Activities.AbstractSearchInputFragment";
	private static final int MAX_HISTORY_COUNT = 10;
	private static Uri mHistoryUri = Uri.parse("content://" + HistoryContentProvider.AUTHORITY + "/history");
	private static final int MAX_TRIES = 5;
	private static final String GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	private static final String API_KEY = "AIzaSyAbahSqDp47FsP_U60bwXdknL_cAUgalrw";

	    private static String[] columns = new String[]{	  
	    	    "sourceAddress",
	    	    "destinationAddress",
	    	    "timeOfTravel",
	    	    "dateOfTravel",
	            "dailyInstantType",
	            "planInstantType",
	            "takeOffer",	           
	            "reqDate", 
	            "radioButtonId",
	            "date"
	    };
	 
	PlacesAutoCompleteAdapter sourceAutoCompleteAdapter = null;
	PlacesAutoCompleteAdapter destinationAutoCompleteAdapter = null;
	
		
	String time = "";
	String date = "";
	String planInstaStr = "";
	Button cancelFindUsers = null;
	Button takeRideButton = null;
	Button offerRideButton = null;
	AutoCompleteTextView source = null;
	AutoCompleteTextView destination = null;	
	boolean takeRide = false;
	boolean destinationSet = false;
	boolean sourceSet = false;
	ProgressBar source_progressbar = null;
	ProgressBar destination_progressbar = null;
	//0 daily pool,1 instant share
	//0 take ,1 offer	
	
	public abstract SBGeoPoint getSourceGeopoint();
	public abstract SBGeoPoint getDestinationGeopoint();
	public abstract int getRadioButtonID();
	public abstract String getSource();
	public abstract String getDestination();
	public abstract String getDate();
	public abstract String getTime();
	public abstract int getDailyInstaType(); //required to decide which api to call, daily Vs (today,tomorrow,in 15min, enter date) 
	public abstract int getPlanInstaTabType(); // means sent from plan tab,insta tab and is used to store history
	
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sourceAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity().getApplicationContext(), R.layout.address_suggestion_layout,source_progressbar);        
        destinationAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity().getApplicationContext(), R.layout.address_suggestion_layout,destination_progressbar);
        if(getPlanInstaTabType() == 1)
        	planInstaStr = "insta";
        else 
        	planInstaStr = "plan";
        cancelFindUsers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HopinTracker.sendEvent("SearchUsers","ButtonClick","searchusers:"+planInstaStr+":click:cancel",1L);
				getActivity().finish();				
			}
		});
        
        takeRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("SearchUsers","ButtonClick","searchusers:"+planInstaStr+":click:takeRide",1L);
				takeRide = true;
				findUsers();			
			}
		});
        offerRideButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				HopinTracker.sendEvent("SearchUsers","ButtonClick","searchusers:"+planInstaStr+":click:offerRide",1L);
                takeRide = false;
                findUsers();
			}
		});
        
        if(source!= null)
        {         	
	        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
	            	HopinTracker.sendEvent("SearchUsers","AutoCompleteClick","searchusers:"+planInstaStr+":click:source:autocomplete",1L);
	                 String sourceAddress =(String) adapterView.getItemAtPosition(i);
	                 if(!StringUtils.isBlank(sourceAddress))
	                 {
		                 hideSoftKeyboard();
		                 source.setSelection(0);
		                 source.clearFocus();
		                 sourceSet = true;
	                } 
	                 else 
	                {
	                    source.setText("");
	                    showErrorDialog("Failed to get Source address", "Please try again...");
	                }
	                 source_progressbar.setVisibility(View.INVISIBLE);
	            }
	        
	        });
	        source.addTextChangedListener(new CustomTextWatcher(source_progressbar,true));
	        source.setAdapter(sourceAutoCompleteAdapter);
        } 
        
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	HopinTracker.sendEvent("SearchUsers","AutoCompleteClick","searchusers:"+planInstaStr+":click:destination:autocomplete",1L);
                 String destinationAddress =(String) adapterView.getItemAtPosition(i);
                 if(!StringUtils.isBlank(destinationAddress))
                 {
	                 hideSoftKeyboard();
	                 destination.setSelection(0);
	                 destination.clearFocus();
	                 destinationSet = true;
                } else 
                {
                	destination.setText("");
                    showErrorDialog("Failed to get destination address", "Please try again...");
                }
               destination_progressbar.setVisibility(View.INVISIBLE);
            }
        
        });
        destination.addTextChangedListener(new CustomTextWatcher(destination_progressbar,false));
        destination.setAdapter(destinationAutoCompleteAdapter);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return  super.onCreateView(inflater, container, savedInstanceState);
    }

	public void showDestinationSuggestionPopup()
	{
		ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching destination suggestions..", "Please wait..");
		FetchAddressSuggestionsAndShowPopup showSourceList = new FetchAddressSuggestionsAndShowPopup(destination,false);
		showSourceList.execute(destination.getText().toString());
	}
	
	public void showSourceSuggestionPopup()
	{
		ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching source suggestions..", "Please wait..");
		FetchAddressSuggestionsAndShowPopup showSourceList = new FetchAddressSuggestionsAndShowPopup(source,true);
		showSourceList.execute(source.getText().toString());
	}	
	
	
	public void findUsers()
	{ 
		if(source_progressbar!=null)
			source_progressbar.setVisibility(View.INVISIBLE);
		destination_progressbar.setVisibility(View.INVISIBLE);
		if (!SBConnectivity.isOnline()){
            showErrorDialog("No Network found!", "Please check your network connection.");
            return;        
        }
		
		if(!sourceSet && getPlanInstaTabType()==0)
			showSourceSuggestionPopup();
		else if(!destinationSet)				
			showDestinationSuggestionPopup();			
		else
		{
			//here in abstract type we are setting all..its responsibility of individual class to 
			// implement these methods and return "" is its not required
			//we use ThisUSer as intermediate storage place of current state of req of this user
			//anything map or list which needs to update picks from ThisUSer
			// we are setting any of source desti address,lat longi
			ThisUserNew.getInstance().setSourceGeoPoint(getSourceGeopoint());  //this could be null
			ThisUserNew.getInstance().setDestinationGeoPoint(getDestinationGeopoint());  //this could be null
			ThisUserNew.getInstance().setSourceFullAddress(getSource());
			ThisUserNew.getInstance().setDestinationFullAddress(getDestination());
			ThisUserNew.getInstance().setTimeOfTravel(getTime());
			ThisUserNew.getInstance().set_Plan_Instant_Type(getPlanInstaTabType());
			ThisUserNew.getInstance().setDateOfTravel(getDate());
			ThisUserNew.getInstance().set_Daily_Instant_Type(getDailyInstaType());//0 daily pool,1 instant share
			ThisUserNew.getInstance().set_Take_Offer_Type(takeRide?0:1);//0 take ,1 offer
			ThisUserNew.getInstance().setSelected_radio_button_id(getRadioButtonID());
			MapListActivityHandler.getInstance().updateSrcDstTimeInListView();
			
			getActivity().finish();
			if (Platform.getInstance().isLoggingEnabled()) Log.i(TAG, "user destination set... querying server");
			ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching users", "Please wait..");
			SBHttpRequest addThisUserSrcDstRequest;
			if(getDailyInstaType() == 0)        		
				addThisUserSrcDstRequest = new AddThisUserScrDstCarPoolRequest();        		
			else
				addThisUserSrcDstRequest = new AddThisUserSrcDstRequest();        		
	         
	        SBHttpClient.getInstance().executeRequest(addThisUserSrcDstRequest);
	        saveSearch();
	        
	        //log
	        
        
        //moveTaskToBack(true);			
		}
	}
	
	 private void showErrorDialog(String title, String message) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(title).setMessage(message);
	        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialogInterface, int i) {
	                return;
	            }
	        });
	        builder.create().show();
	    }
	   
	    
	    @Override
	    public void onStart(){
	        super.onStart();
	        EasyTracker.getInstance().activityStart(getActivity());
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        EasyTracker.getInstance().activityStop(getActivity());
	    }
	
	public void hideSoftKeyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	}
	
    private void saveSearch() {

        new Thread("saveSearch") {
            @Override
            public void run() {
                saveHistoryBlocking();
            }
        }.start();
    }

   
	private void saveHistoryBlocking() {        
        ContentResolver cr = getActivity().getContentResolver();       
        ThisUserNew thisUser = ThisUserNew.getInstance();

        // Use content resolver (not cursor) to insert/update this query
        try {
            ContentValues values = new ContentValues();            
            values.put(columns[0], thisUser.getSourceFullAddress());
            values.put(columns[1], thisUser.getDestinationFullAddress());
            values.put(columns[2], thisUser.getTimeOfTravel());           
            values.put(columns[3], thisUser.getDateOfTravel());
            values.put(columns[4], thisUser.get_Daily_Instant_Type());
            values.put(columns[5], thisUser.get_Plan_Instant_Type()); 
            values.put(columns[6], thisUser.get_Take_Offer_Type());
            values.put(columns[7], StringUtils.gettodayDateInFormat("d MMM")); 
            values.put(columns[8], thisUser.getSelected_radio_button_id());
            values.put(columns[9], System.currentTimeMillis());
            cr.insert(mHistoryUri, values);
            if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "saveHistoryQuery:" +  values.toString());
        } catch (RuntimeException e) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "saveHistoryQueryerror", e);
        }

        // Shorten the list (if it has become too long)
        truncateHistory(cr, MAX_HISTORY_COUNT);

        //update the inmemory cache
        HistoryAdapter.HistoryItem historyItem = new HistoryAdapter.HistoryItem(
        		thisUser.getSourceFullAddress(),
                thisUser.getDestinationFullAddress(),
                thisUser.getTimeOfTravel(),          
                thisUser.getDateOfTravel(),
                thisUser.get_Daily_Instant_Type(),
                thisUser.get_Plan_Instant_Type(), 
                thisUser.get_Take_Offer_Type(),
                StringUtils.gettodayDateInFormat("d MMM"),
                thisUser.getSelected_radio_button_id()
               );

        addHistoryToMemory(historyItem);
        
    }

    private void addHistoryToMemory(HistoryAdapter.HistoryItem historyItem) {
        List<HistoryAdapter.HistoryItem> historyItemList = ThisUserNew.getInstance().getHistoryItemList();
        if (historyItemList != null) {
            historyItemList.add(0, historyItem);
            if (historyItemList.size() > MAX_HISTORY_COUNT) {
                for (int i = historyItemList.size() - 1; i >= MAX_HISTORY_COUNT; i--) {
                    historyItemList.remove(i);
                }
            }
        }
    }

    private void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries < 0) {
            throw new IllegalArgumentException();
        }

        try {
            // null means "delete all".  otherwise "delete but leave n newest"
            String selection = null;
            if (maxEntries > 0) {
                selection = "_id IN " +
                        "(SELECT _id FROM history" +
                        " ORDER BY date DESC" +
                        " LIMIT -1 OFFSET " + String.valueOf(maxEntries) + ")";
            }
            cr.delete(mHistoryUri, selection, null);
        } catch (RuntimeException e) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "truncateHistory", e);
        }
    }
    
    private class FetchAddressSuggestionsAndShowPopup extends AsyncTask<String, Void, ArrayList<String>>
    { 
    	String mAddress = "";
    	AutoCompleteTextView mThisTextView;
    	boolean mIsSource = false;
    	ProgressBar mThisProgressBar = null;
    	
    	FetchAddressSuggestionsAndShowPopup(AutoCompleteTextView thisTextView,boolean isSource)
    	{
    		super();
    		mThisTextView = thisTextView;
    		mIsSource = isSource;    		
    	}
    	
    	@Override
    	protected ArrayList<String> doInBackground(String... address) {    		
    		mAddress = address[0];
    		ArrayList<String> address_list = autocomplete(mAddress);
    		return address_list;
    	}	
    	
    	protected void onPostExecute(ArrayList<String> address_list) {
    		ProgressHandler.dismissDialoge();
    		if(address_list!= null && address_list.size()>0)
    		{	    		
    			final Dialog suggestion_dialog = new Dialog(getActivity());
    			suggestion_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    			suggestion_dialog.setCancelable(true);
    			suggestion_dialog.setContentView(R.layout.search_suggestions);	    		
    			suggestion_dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    		ListView suggestion_list = (ListView) suggestion_dialog.findViewById(R.id.search_suggestions_listview);	    		
	    		// Create ArrayAdapter  
	    		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.address_suggestion_popup_row, address_list);
	    		suggestion_list.setAdapter(listAdapter);	    		
	    		listAdapter.notifyDataSetChanged();	    		
	    		suggestion_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
						TextView addressRow = (TextView)v;
						mThisTextView.setText(addressRow.getText().toString());
						if(mIsSource)
							sourceSet = true;
						else
							destinationSet = true;
						suggestion_dialog.dismiss();
						findUsers();						
					}
	    			
				}); 
	    		suggestion_dialog.show();
    		}
    		else
    		{
    			ToastTracker.showToast(mAddress + " not recognized");
    			if(mIsSource)
					sourceSet = false;
				else
					destinationSet = false;
    		}
    	
    	}

    	
    }
    
    
    /**************************************************************?
     * below all is code for auto complete textview
     * @param input
     * @return
     */
    
    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(GOOGLE_PLACES_URL);
            sb.append("?sensor=false&key=" + API_KEY);            
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    
    private class CustomTextWatcher implements TextWatcher {
    	
    	private ProgressBar thisTextProgressBar = null;
    	private boolean mIsSource = false;
    	public CustomTextWatcher( ProgressBar progressBar, boolean isSource)
    	{
    		super();
    		this.mIsSource = isSource;
    		this.thisTextProgressBar = progressBar;
    	}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { 
        	
            	thisTextProgressBar.setVisibility(View.VISIBLE);
            	
            	if(mIsSource)
            		sourceSet = false;
            	else
            		destinationSet = false;            

        }

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}
    }
    
    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;
        private ProgressBar thisTextProgressBar = null;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, ProgressBar progressBar) {
            super(context, textViewResourceId);
            thisTextProgressBar = progressBar;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {                    	
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                    thisTextProgressBar.setVisibility(View.INVISIBLE);                    
                    
                }};
            return filter;
        }
    }

}
