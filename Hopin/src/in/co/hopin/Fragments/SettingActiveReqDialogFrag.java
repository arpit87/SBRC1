package in.co.hopin.Fragments;

import in.co.hopin.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class SettingActiveReqDialogFrag extends DialogFragment{
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.settings_activereq_popup, container);
        
        ImageView dialogCloseButton = (ImageView)dialogView.findViewById(R.id.button_close_fb_login_dialog);
		// if button is clicked, close the custom dialog
		dialogCloseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		return dialogView;
	}


}
