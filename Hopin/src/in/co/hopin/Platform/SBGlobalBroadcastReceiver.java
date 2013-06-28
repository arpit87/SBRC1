package in.co.hopin.Platform;

import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import in.co.hopin.Util.StringUtils;

public class SBGlobalBroadcastReceiver extends BroadcastReceiver {
	
	private static final String TAG = "in.co.hopin.Platform.SBGlobalBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d(TAG, "Got intent");
		String intentAction = intent.getAction();
		if(intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			Logger.d(TAG, "Got boot intent");
			Platform.getInstance().startChatService();
            if (!StringUtils.isEmpty(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID))) {
                Platform.getInstance().startGCMService();
            }
            //else userid has not been set yet, service will be started after add user response is received.
		}
		
	}

}
