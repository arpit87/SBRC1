package in.co.hopin.Server;

import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class RegisterGCMIdResponse extends ServerResponseBase {
    public static final String TAG = "in.co.hopin.Server.RegisterGCMIdResponse";

    public RegisterGCMIdResponse(HttpResponse httpResponse, String jObjStr,String api) {
        super(httpResponse, jObjStr,api);
    }

    @Override
    public void process() {
        Logger.i(TAG, "Processing RegisterGCMIdResponse response. status:" + this.getStatus());
        Logger.i(TAG,"Got json : " + jobj.toString());
        try {
            body = jobj.getJSONObject("body");
            if (body.getInt("success") != 1) {
                Logger.e(TAG, "RegisterGCMIdRequest failed.");
                logServererror();
            }
            logSuccess();
        } catch (JSONException e) {
        	logServererror();
            Logger.e(TAG, "Error returned by server on RegisterGCMIdRequest", e);
        }
    }
}
