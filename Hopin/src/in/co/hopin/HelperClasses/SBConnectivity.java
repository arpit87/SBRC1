package in.co.hopin.HelperClasses;

import in.co.hopin.Platform.Platform;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Util.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SBConnectivity {
    final static Context context = Platform.getInstance().getContext();
    private static String TAG = "in.co.hopin.HelperClasses.SBConnectivity";
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Logger.d("SBConnectivity", "ConnectivityManager:"+cm.toString());
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
    
    public static boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Logger.d("SBConnectivity", "ConnectivityManager:"+cm.toString());
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ni != null && ni.isConnected();
    }

    public static boolean isConnected() {
        try {
        	if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "chking data available by http timeout");
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
            	if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "yes data available by http timeout");
                return true;
            }
            //Log.d("in.co.hopin.HelperClasses.SBConnectivity", "No response");
            if (Platform.getInstance().isLoggingEnabled()) Log.d(TAG, "no data available by http timeout");
            return false;
        } catch (Exception e) {
            //Log.e("in.co.hopin.HelperClasses.SBConnectivity", e.getMessage());
            return false;
        }

    }
    
    public static String getipAddress() { 
    	Logger.i(TAG,"getting ip address");
    	String ipaddress = "";
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                    	if(ipaddress.equals(""))
                    		ipaddress=inetAddress.getHostAddress().toString();
                    	else
                    		ipaddress=ipaddress+","+inetAddress.getHostAddress().toString();
                        Logger.i(TAG,"ip address:"+ipaddress);                       
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.e(TAG,"Socket exception in GetIP Address of Utilities:"+ex.toString());
        }
        return ipaddress; 
}  
      
    
}
