package com.example.worth_a_shot.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.WorthAShot;
import com.example.worth_a_shot.http.ApiRequest.Actions;
import com.example.worth_a_shot.http.CallbackReceiver;
import com.example.worth_a_shot.http.ServiceHelper;
import com.example.worth_a_shot.http.request.UserAddRequest;
import com.example.worth_a_shot.models.User;
import com.example.worth_a_shot.utils.U;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

/**
 * LoginFacebookActivity
 * Class description
 */
public class FacebookAuthActivity extends LoaderActivity {
	
	private static final String TAG = FacebookAuthActivity.class.getSimpleName();
	
////=======================================================================================
//// Static constants.
////=======================================================================================
	
	public static final int REAUTH_ACTIVITY_CODE = 100;
	
////=======================================================================================
//// Member variables.
////=======================================================================================
		
	private UiLifecycleHelper mUiLifecycleHelper;
	private UserAddReceiver mReceiver;
	
	private ProgressDialog mDialog;

////=======================================================================================
//// Activity lifecycle.
////=======================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mReceiver = new UserAddReceiver();
		mUiLifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	// Let our activity know the facebook session state changed
		        onSessionStateChange(session, state, exception);
		    }
		});
		mUiLifecycleHelper.onCreate(savedInstanceState);
//		
//		// Auth or reauth with permission based on mAuthType
//		requestPermissions();
		

	    // start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {    	
	    	/*
	    	 * (non-Javadoc)
	    	 * @see com.facebook.Session.StatusCallback#call(com.facebook.Session, com.facebook.SessionState, java.lang.Exception)
	    	 */
	    	@Override
	    	public void call(final Session session, SessionState state, Exception exception) {
	    		if (exception != null) {
	    			exception.printStackTrace();
	    		}
	    	  
	    		if (session.isOpened()) {
	    			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	    				
	    				/*
	    				 * (non-Javadoc)
	    				 * @see com.facebook.Request.GraphUserCallback#onCompleted(com.facebook.model.GraphUser, com.facebook.Response)
	    				 */
	    				@Override
	    				public void onCompleted(GraphUser gUser, Response response) {
	    					Log.d(TAG, "MeRequest: " + response.toString());
	    					if (gUser != null) {
	    						User user = new User();
	    						user.setFacebookId(Long.parseLong(gUser.getId()));
	    						user.setFacebookToken(session.getAccessToken());
	    						user.save(getApplicationContext());
	    						WorthAShot.get(getApplicationContext()).setUser(user);
	    						
	    						List<String> p = session.getPermissions();
	    						for (String s : p) {
	    							Log.e(TAG, s);
	    						}
	    						
	    						mDialog = U.showWaitingDialog(FacebookAuthActivity.this, "Signing in...");
	    						
	    						UserAddRequest request = new UserAddRequest(getApplicationContext(), Long.parseLong(gUser.getId()), session.getAccessToken());
	    						ServiceHelper.getInstance(getApplicationContext()).startService(request);
	    					}	  
	    				}
	    			});
	    		} else {
	    			Log.e(TAG, "Session not open.");
	    		}
	    	}
	    });
	} 
	
    /*
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mUiLifecycleHelper != null) mUiLifecycleHelper.onResume();
        registerReceiver(mReceiver, new IntentFilter(Actions.USER_ADD));
    }

	/*
	 * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();       
        if (mUiLifecycleHelper != null) mUiLifecycleHelper.onPause();
        unregisterReceiver(mReceiver);
    }

	/*
	 * (non-Javadoc)
	 * @see com.facebook.FacebookActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (mUiLifecycleHelper != null) mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (mUiLifecycleHelper != null) mUiLifecycleHelper.onDestroy();
	}   

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if (mUiLifecycleHelper != null) mUiLifecycleHelper.onSaveInstanceState(outState);
	}
	
	//=================================================================================================================
	// Facebook state/Authorization
	//=================================================================================================================

	/**
	 * Called when the Facebook session state has changed.
	 * @param session
	 * @param state
	 * @param exception
	 */
	protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
		switch (state) {
		// Logged in or Extended Token
		case OPENED:
		case OPENED_TOKEN_UPDATED:			
			Log.e(TAG, "Opened");		
//			FacebookHelper.getInstance(getApplicationContext()).invalidatePermissions();
//			FacebookHelper.getInstance(getApplicationContext()).updatePermissions(0);
//			FacebookHelper.getInstance(getApplicationContext()).extendAccessToken();
//			
//			if (session != null && session.isOpened()) {
//				Log.e(TAG, "Requesting permissions");
//				requestPermissions();
//			}
			
			break;
		case CLOSED:
		case CLOSED_LOGIN_FAILED:			
//			Log.e(TAG, "Closed");
//			if (session != null) {
//				Log.e(TAG, "close and clear facebook session");
//				session.closeAndClearTokenInformation();
//			}
//			
//			FacebookHelper.invalidateFacebookSession(this);
//			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Save the users fb tokens
	 * @param user Facebook GraphUser object
	 */
	private void saveFbTokens(GraphUser user) {
		if (user == null) return;

		String fbId = user.getId();
		WorthAShot.get(getApplicationContext()).getUser().setFacebookId(Long.parseLong(fbId));

		Session session = Session.getActiveSession();
		if (session != null && session.getState().isOpened()) {
			WorthAShot.get(getApplicationContext()).getUser().setFacebookToken(session.getAccessToken());
		}
		
		WorthAShot.get(getApplicationContext()).getUser().save(getApplicationContext());
	}

////===============================================================================================================================
//// Receivers
////===============================================================================================================================

	private class UserAddReceiver extends CallbackReceiver {

		@Override
		public void onResultOk(Context context, Intent intent) {
			Intent i = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(i);
			finish();
			if (mDialog != null) mDialog.dismiss();
		}
		
		@Override
		public  void onResultError(Context context, Intent intent) {
			if (mDialog != null) mDialog.dismiss();
		}
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaded() {
		// TODO Auto-generated method stub
		
	}
}
