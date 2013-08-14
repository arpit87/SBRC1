package in.co.hopin.HttpClient;

import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Server.UploadEventsResponse;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UploadEventsRequest extends SBHttpRequest {
    private static final String TAG = "in.co.hopin.HttpClient.UploadEventsRequest";

    private String RESTAPI = "UploadEvents";
    public static final String URL = "http://hopin.co.in/mayank/loggerv4.php";
    
    private final HttpPost httpQuery;
    private final HttpClient httpclient = new DefaultHttpClient();    
    private long lastTimeStamp;

    public UploadEventsRequest(String logsJson, long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
        queryMethod = QueryMethod.Post;
        httpQuery =  new HttpPost(URL);
        URLStr = URL;

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("logs", logsJson));
            Logger.d(TAG, "calling server:" + logsJson);
            httpQuery.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /*StringBuilder sb = new StringBuilder();
        sb.append(URL).append("?").append("logs").append("=").append("test");
        Logger.d(TAG, sb.toString());

        httpQuery = new HttpGet(sb.toString());*/
    }

    @Override
    public ServerResponseBase execute() {
    	HopinTracker.sendEvent("HttpRequest","UploadEvents","httprequest:uploadevents:execute",1L);
        try {
            response = httpclient.execute(httpQuery);
        } catch (Exception e) {
        	HopinTracker.sendEvent("HttpRequest","UploadEvents","httprequest:uploadevents:execut:executeeexception",1L);
            Logger.e(TAG, e.getMessage());
        }

        if (response == null) {
            return null;
        }

        try {
            String jsonStr = responseHandler.handleResponse(response);            
            UploadEventsResponse uploadRes = new UploadEventsResponse(response, jsonStr, lastTimeStamp,RESTAPI);
            uploadRes.setReqTimeStamp(this.reqTimeStamp);
            return uploadRes;
        } catch (Exception e) {
        	HopinTracker.sendEvent("HttpRequest","UploadEvents","httprequest:uploadevents:execute:responseexception",1L);
            Logger.e(TAG, e.getMessage());
        }

        return null;
    }
}
