package in.co.hopin.Platform;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SBGlobalBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String intentAction = intent.getAction();
		if(intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) 
			Platform.getInstance().stopChatService();	    
		
	}

}
