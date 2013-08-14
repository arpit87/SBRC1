package in.co.hopin.HttpClient;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Util.Logger;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

//Singleton class
//this class will have a thread pool to its disposal
//??all threads will be async???
//??if we want a synced job then we can customize the request ???
public class SBHttpClient {
	
	private static final String TAG = "in.co.hopin.HttpClient.SBHttpClient";
	private static SBHttpClient uniqueClient;
	private SBHttpClient(){};
	private ServerResponseBase response;
	private long reqTimeStamp =  0L;	
	
	public static SBHttpClient getInstance()
	{		
		if(uniqueClient == null)
			uniqueClient = new SBHttpClient();
		return uniqueClient; 		
	}
	
	public void executeRequest(SBHttpRequest... request)
	{
		//currently allowing max 3 sync requests
		int count = request.length;
		if (count<=3)
			new NewAsyncTask().execute(request);
		else {
			if (Platform.getInstance().isLoggingEnabled()) Log.e(TAG, "Max 3 http request per thread allowed");
        }
	}	
	
	private class NewAsyncTask extends AsyncTask <SBHttpRequest, Void, ServerResponseBase>
	{		
		protected ServerResponseBase doInBackground(SBHttpRequest... request) {
			 int count = request.length;	         
	         for(int i=0;i<count;i++)
	         {
	        	 Logger.d(TAG, "sending req:"+request[i].GetQueryURL());
	        	 response =  request[i].execute();
	        	 if(response.getStatus()!=ServerResponseBase.ResponseStatus.HttpStatus200)	        	   
	        		 return null;
	         }
	         return response;
		}
		
		protected void onPostExecute(ServerResponseBase response) {
			if(response!=null)
				response.process();
		}

				
	}

}
