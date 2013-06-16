package in.co.hopin.Platform;

import in.co.hopin.Util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
		}
		
	}

}
