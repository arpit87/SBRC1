package in.co.hopin.Server;

import in.co.hopin.HelperClasses.Event;
import in.co.hopin.Util.Logger;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadEventsResponse extends ServerResponseBase {

    public static final String TAG = "in.co.hopin.Server.UploadEventsResponse";

    private long lastTimeStamp;

    public UploadEventsResponse(HttpResponse httpResponse, String jObjStr, long lastTimeStamp) {
        super(httpResponse, jObjStr);
        this.lastTimeStamp = lastTimeStamp;
    }

    @Override
    public void process() {
        Logger.i(TAG, "Processing UploadEventsResponse response. status:" + this.getStatus());
        Logger.i(TAG, "Got json : " + jobj.toString());
        try {
            if (jobj.has("error")) {
                JSONObject errorJson = jobj.getJSONObject("error");
                Logger.e(TAG, "Upload failed with error: " + errorJson.getString("error"));
            } else {
                Event.deleteEvent(lastTimeStamp);

                JSONObject body = jobj.getJSONObject("body");
                int failCount = body.getInt("failcount");
                if (failCount != 0) {
                    Logger.e(TAG, "No. of log entries rejected: " + failCount);
                }
            }
        } catch (JSONException e) {
            Logger.e(TAG, "Error returned by server on UploadEventsRequest", e);
        }
    }
}
