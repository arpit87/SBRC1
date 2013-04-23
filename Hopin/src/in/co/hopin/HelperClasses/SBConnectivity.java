package in.co.hopin.HelperClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SBConnectivity {
    final static Context context = Platform.getInstance().getContext();

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (Platform.getInstance().isLoggingEnabled()) Log.d("SBConnectivity", "ConnectivityManager:"+cm.toString());
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static boolean isConnected() {
        try {
            HttpGet request = new HttpGet(ServerConstants.SERVER_ADDRESS);
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 10000; //timeout of 10 seconds
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 10000; //socket timeout of 10 seconds
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            request.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return true;
            }
            //Log.d("in.co.hopin.HelperClasses.SBConnectivity", "No response");
            return false;
        } catch (Exception e) {
            //Log.e("in.co.hopin.HelperClasses.SBConnectivity", e.getMessage());
            return false;
        }

    }
}
