package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Server.RegisterGCMIdResponse;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Util.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class RegisterGCMIdRequest extends SBHttpRequest {
    private static final String TAG = "in.co.hopin.HttpClient.RegisterGCMIdRequest";

    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERSERVICE + "/addRegID/";

    private final HttpGet httpQuery;
    private final HttpClient httpclient = new DefaultHttpClient();

    public RegisterGCMIdRequest() {
        String userId = ThisUserConfig.getInstance().getString(ThisUserConfig.USERID);
        String uuid = ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID);
        String regId = ThisAppConfig.getInstance().getString(ThisAppConfig.PROPERTY_REG_ID);

        StringBuilder sb = new StringBuilder();
        sb.append(URL).append("?").append(ThisUserConfig.USERID).append("=").append(userId)
                .append("&").append(ThisAppConfig.APPUUID).append("=").append(uuid)
                .append("&").append("reg_id").append("=").append(regId);
        Logger.d(TAG, sb.toString());

        httpQuery = new HttpGet(sb.toString());
    }

    @Override
    public ServerResponseBase execute() {
        try {
            response = httpclient.execute(httpQuery);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }

        if (response == null) {
            return null;
        }
        
        try {
            String jsonStr = responseHandler.handleResponse(response);
            RegisterGCMIdResponse registerGCMIdResponse = new RegisterGCMIdResponse(response, jsonStr);
            return registerGCMIdResponse;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }

        return null;
    }
}