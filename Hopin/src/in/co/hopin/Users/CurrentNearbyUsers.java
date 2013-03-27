package in.co.hopin.Users;

import in.co.hopin.HelperClasses.JSONHandler;
import in.co.hopin.HelperClasses.ToastTracker;

import java.util.HashMap;
import java.util.List;


import org.json.JSONObject;

import android.util.Log;

/****
 * 
 * @author arpit87
 * this maintains a list of all nearby users from most recent getMatchingUsers request
 *
 */
public class CurrentNearbyUsers {
	
	private static String TAG = "my.b1701.SB.Users.CurrentNearbyUsers" ;
	private HashMap<String, NearbyUser> FBID_NearbyUserMap = new HashMap<String, NearbyUser>(); //store fbid<-> nearbyuser obj map
	private List<NearbyUser> mCurrentNearbyUserList = null;
	private List<NearbyUser> mNewNearbyUserList = null;
	private static CurrentNearbyUsers instance=new CurrentNearbyUsers();
	private boolean updatedToCurrent = false;	
	public static CurrentNearbyUsers getInstance() {
		 return instance;
	}
	
	public void updateNearbyUsersFromJSON(JSONObject body)
	{		
		//we temporarily put new users in new list and MapHandler has to check if changed and callupdate then we change current to new
		//we return null for 0 users so check for null always while getting nearby users
		Log.i(TAG,"updating nearby users");
		updatedToCurrent = false;
		mNewNearbyUserList = JSONHandler.getInstance().GetNearbyUsersInfoFromJSONObject(body);	
		if(mNewNearbyUserList!=null)
			ToastTracker.showToast("new users:"+mNewNearbyUserList.size());
		else
			ToastTracker.showToast("new users 0");
		
	}

	public List<NearbyUser> getAllNearbyUsers()
	{
		return mCurrentNearbyUserList;
	}
	
	private void updateCurrentToNew()
	{
		mCurrentNearbyUserList = mNewNearbyUserList ;
		FBID_NearbyUserMap.clear();
		if(mCurrentNearbyUserList!=null)
		{
			for(NearbyUser n : mCurrentNearbyUserList)
			{
				FBID_NearbyUserMap.put(n.getUserFBInfo().getFbid(), n);
			}
		}		
		updatedToCurrent = true;
	}
	
	public NearbyUser getNearbyUserWithFBID(String FBid)
	{
		NearbyUser n;
		n = FBID_NearbyUserMap.get(FBid);
		return n;
	}
	
	public NearbyUser getNearbyUserAtPosition(int id)
	{
		NearbyUser n;
		n = mCurrentNearbyUserList.get(id);
		return n;
	}
	
	public boolean usersHaveChanged()
	{
		Log.i(TAG,"chking if usr changed ");
		if(updatedToCurrent)
			return false;
		if(mCurrentNearbyUserList == null)
		{			
			if(mNewNearbyUserList == null)
				return false; //called before getMatch
			else
			{	
				updateCurrentToNew();			
				return true; //first time update
			}
		}
		else if(mNewNearbyUserList == null)
		{
			ToastTracker.showToast("users changed to 0");
			updateCurrentToNew();
			return true; //new number of users is 0 but currently we showing some who moved out	
		}
				
		//check for objects inside..we have overriden equals..yiipee
		for(NearbyUser n:mNewNearbyUserList)
		{
			if(mCurrentNearbyUserList.contains(n))
				continue;	
				
			Log.i(TAG,"user have changed ");
			ToastTracker.showToast("users changed");
			updateCurrentToNew();
			return true;
		}		
		Log.i(TAG,"user did not change ");
		ToastTracker.showToast("users not changed");
		return false;
	}
	
	public void clearAllData()
	{
		if(mCurrentNearbyUserList!=null)
			mCurrentNearbyUserList.clear();
		if(mNewNearbyUserList!=null)
			mNewNearbyUserList.clear();
		if(FBID_NearbyUserMap!=null)
			FBID_NearbyUserMap.clear();
	}
	
}
