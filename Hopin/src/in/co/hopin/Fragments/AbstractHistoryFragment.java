package in.co.hopin.Fragments;

import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.Adapter.HistoryAdapter;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HttpClient.AddThisUserScrDstCarPoolRequest;
import in.co.hopin.HttpClient.AddThisUserSrcDstRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.SBHttpRequest;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Users.ThisUserNew;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import in.co.hopin.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AbstractHistoryFragment extends ListFragment {
    public static final String TAG = "in.co.hopin.Fragments.AbstractHistoryFragment";

    HistoryAdapter adapter;
	View mListViewContainer;
	TextView mEmptyListTextView;
	List<HistoryAdapter.HistoryItem> historyList;

    public abstract int getPlanInstantType();    

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        historyList = fetchHistory();
        adapter = new HistoryAdapter(getActivity(), historyList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(adapter!=null)
        {
            if (!SBConnectivity.isConnected()){
                showErrorDialog("No Network found!", "Please check your network connection.");
                return;
            }
            HistoryAdapter.HistoryItem historyItem = adapter.getItem(position);
            CreateRequestFromHistory asyncReq = new CreateRequestFromHistory();
            asyncReq.execute(historyItem);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, null);
        mListViewContainer = inflater.inflate(R.layout.historyfrag_listview, null);
        mEmptyListTextView = (TextView)mListViewContainer.findViewById(R.id.history_emptyList);
        if(historyList.isEmpty())
        {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }
        return mListViewContainer;
    }

    protected List<HistoryAdapter.HistoryItem> fetchHistory() {
        List<HistoryAdapter.HistoryItem> historyItems = new ArrayList<HistoryAdapter.HistoryItem>();
        List<HistoryAdapter.HistoryItem> historyItemList = ThisUserNew.getInstance().getHistoryItemList();
        for (HistoryAdapter.HistoryItem historyItem : historyItemList) {
            if (historyItem.getPlanInstantType() == getPlanInstantType()){            	
                historyItems.add(historyItem); //plan is 0,insta is 1
            }
        }
        return historyItems;
    }


    private void showErrorDialog(final String title, final String message) {
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
    
    protected class CreateRequestFromHistory extends AsyncTask<HistoryAdapter.HistoryItem, Void, Void> {

        @Override
        protected Void doInBackground(HistoryAdapter.HistoryItem... historyItems) {
            HistoryAdapter.HistoryItem historyItem = historyItems[0];                       
            int takeride = historyItem.getTakeOffer();
            String source = historyItem.getSource();
            String destination = historyItem.getDestination();
            int daily_inta_type = historyItem.getDailyInstantType();
            
            //set all data in thisUser from where http req picks up to form req
            //doing this to make things uncoupled
            if(source.equalsIgnoreCase("my location"))      {      
            	ThisUserNew.getInstance().setCurrentGeoPointToSourceGeopoint();
            	source = "";
            }
            else
            	ThisUserNew.getInstance().setSourceGeoPoint(null);
            ThisUserNew.getInstance().setDestinationGeoPoint(null);
            ThisUserNew.getInstance().setSourceFullAddress(source);
            ThisUserNew.getInstance().setDestinationFullAddress(destination);
            ThisUserNew.getInstance().setTimeOfTravel(historyItem.getTimeOfTravel());
            ThisUserNew.getInstance().setDateOfTravel(historyItem.getDateOfTravel());            
            ThisUserNew.getInstance().set_Daily_Instant_Type(daily_inta_type);//0 daily pool,1 instant share
            ThisUserNew.getInstance().set_Take_Offer_Type(takeride);//0 take ,1 offer

            Platform.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    MapListActivityHandler.getInstance().updateSrcDstTimeInListView();
                }
            });

            //Log.i(TAG, "Executing query");
            if(daily_inta_type == 1) //1 = insta
            {
            	SBHttpRequest addThisUserSrcDstRequest= new AddThisUserSrcDstRequest();
            	SBHttpClient.getInstance().executeRequest(addThisUserSrcDstRequest);
            }
            else
            {
            	SBHttpRequest addThisCarpoolUserSrcDstRequest= new AddThisUserScrDstCarPoolRequest();
            	SBHttpClient.getInstance().executeRequest(addThisCarpoolUserSrcDstRequest);
            }
            return null;
        }       
		
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching users", "Please wait..");
        }
    }
}
