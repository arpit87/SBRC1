package in.co.hopin.Activities;

import com.google.analytics.tracking.android.EasyTracker;
import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ToastTracker;
import in.co.hopin.HttpClient.FeedbackRequest;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends Activity{
	

	Button btn_cancel;
	Button btn_sendfeedback;
	EditText feedbackTextView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback_dialog);
        boolean showprompt = getIntent().getBooleanExtra("showprompt", false);
        if(showprompt)
        	buildfeedbackAlertMessage();
      		
	}
	
	@Override
    public void onResume(){
    	super.onResume();	
	feedbackTextView = (EditText)findViewById(R.id.feedback_feedbackedittext);       
    
    Button dialogCloseButton = (Button)findViewById(R.id.feedback_btn_cancel);
	// if button is clicked, close the custom dialog
	dialogCloseButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});
	
	Button sendFeedbackButton = (Button)findViewById(R.id.feedback_btn_send);
	// if button is clicked, close the custom dialog
	sendFeedbackButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {			
			 FeedbackRequest feedbackRequest = new FeedbackRequest(feedbackTextView.getText().toString());
             SBHttpClient.getInstance().executeRequest(feedbackRequest);
             Toast.makeText(FeedbackActivity.this, "Thank you for valuable feedback", Toast.LENGTH_SHORT).show();
             finish();
		}
	});
		
}
	
   private void buildfeedbackAlertMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to send some feedback for hopin?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    	 dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

   @Override
   public void onStart(){
       super.onStart();
       EasyTracker.getInstance().activityStart(this);
   }

   @Override
   public void onStop(){
       super.onStop();
       EasyTracker.getInstance().activityStop(this);
   }
}
