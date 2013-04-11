package in.co.hopin.Fragments;

import in.co.hopin.Activities.Tutorial;
import in.co.hopin.ActivityHandlers.MapListActivityHandler;
import in.co.hopin.FacebookHelpers.FacebookConnector;
import in.co.hopin.HelperClasses.ProgressHandler;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import in.co.hopin.R;


public class FBLoginDialogFragment extends DialogFragment{
	
	static FacebookConnector fbconnect;
		
	public static FBLoginDialogFragment newInstance(FacebookConnector fbconnector)
	{
		FBLoginDialogFragment f = new FBLoginDialogFragment();
		fbconnect = fbconnector;
		return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.fblogin_newdialog, container);
        
        ImageView dialogCloseButton = (ImageView)dialogView.findViewById(R.id.button_close_fb_login_dialog);
		// if button is clicked, close the custom dialog
		dialogCloseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Button fbLoginButton = (Button) dialogView.findViewById(R.id.fb_login_signviafb);
		// if button is clicked, close the custom dialog
		fbLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Trying logging", "Please wait");
				dismiss();
				fbconnect.loginToFB();				
			}
		});
        
		return dialogView;
	}
	
	/*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setView(inflater.inflate(R.layout.fblogin_newdialog, null)).setTitle("Facebook Login")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	MapListActivityHandler.getInstance().getUnderlyingActivity().getFbConnector().loginToFB();
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }*/
	
}
