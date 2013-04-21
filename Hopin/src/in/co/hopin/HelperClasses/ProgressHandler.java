package in.co.hopin.HelperClasses;

import in.co.hopin.Platform.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.ProgressBar;

/**
 * 
 * @author arpit87
 *This class is thread safe,call from anywhere to show and dismiss dialog
 */



public class ProgressHandler {
	ProgressBar progressBar = null;	
	private static ProgressDialog progressDialog = null;
	private static AtomicBoolean isshowing = new AtomicBoolean(false);
	private int count = 10;
	
	private static Activity underlying_activity = null;
	private static String title = "";
	private static String message = "";
	private static Runnable cancelableRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(progressDialog!=null && isshowing.get())
			{
				progressDialog.setTitle("Taking too long?");
				progressDialog.setMessage("It seems network connection is too slow,press back to cancel");				
				progressDialog.setCancelable(true);
			}			
		}
	};
	
	private static Runnable startRunnable = new Runnable() {
		
		@Override
		public void run() {
			progressDialog = ProgressDialog.show(underlying_activity, title, message, true);
			progressDialog.setOnCancelListener(new OnCancelListener() {				
				@Override
				public void onCancel(DialogInterface dialog) {
					isshowing.set(false);					
				}		
		});
	}};
	
	public static void showInfiniteProgressDialoge(final Activity underlying_activity,final String title,final String message)
	{
		if(!isshowing.getAndSet(true)) 
		{
			ProgressHandler.underlying_activity = underlying_activity;
			ProgressHandler.title = title;
			ProgressHandler.message = message;
			Platform.getInstance().getHandler().post(startRunnable);			
			//set cancelable after 10 sec of delay
			Platform.getInstance().getHandler().postDelayed(cancelableRunnable,10000);
		}
		else
		{
			if(progressDialog!=null)
			{
				Platform.getInstance().getHandler().post((new Runnable(){
					public void run() {
						progressDialog.setTitle(title);
						progressDialog.setMessage(message);
				}}));
			}
		}
	}
	
	public static void dismissDialoge()
	{
		if(isshowing.getAndSet(false))
		{
			Platform.getInstance().getHandler().removeCallbacks(cancelableRunnable);
			Platform.getInstance().getHandler().post((new Runnable(){
			public void run() {
				progressDialog.dismiss();						
			}}));
		}
	}	
	

}
