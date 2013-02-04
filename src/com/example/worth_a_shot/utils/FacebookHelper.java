/**
 * File:    FacebookHelper.java
 * Created: Oct 26, 2012
 * Author:	Kyle Gordon
 */
package com.example.worth_a_shot.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.worth_a_shot.WorthAShot;
import com.example.worth_a_shot.activity.FacebookAuthActivity;
import com.example.worth_a_shot.models.User;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

/**
 * FacebookHelper
 * Assists in Facebook authorization, calls, etc.
 */
public class FacebookHelper {

	private static final String TAG = FacebookHelper.class.getSimpleName();


	////======================================================================================================
	////Static constants.
	////======================================================================================================

	// Facebook Login/Authorization
	public static final String FACEBOOK_APP_ID = "530938486939129";  
	public static final String FACEBOOK_APP_SECRET = "01f86b3b6fc252d21030add0c1ba8d0f";  
	public static final String FACEBOOK_PROPERTY_EMAIL = "email";
	public static final String FACEBOOK_PROPERTY_GENDER = "gender";
	public static final List<String> FACEBOOK_READ_PERMISSIONS = Arrays.asList(
					"read_friendlists");
	public static final List<String> FACEBOOK_PUBLISH_PERMISSIONS = 
			Arrays.asList("publish_stream");

	public static final int REAUTH_ACTIVITY_CODE = 100;

	private static final String PREFS_LAST_UPDATE = "PREFS_LAST_UPDATE";
	private static final String PREFS_HAS_PUBLISH_PERMISSION = "PREFS_HAS_PUBLISH_PERMISSION";
	
	//==================================================================================
	// Member variable
	//===================================================================================

	private Context mContext;

	private int mAuthType;
	private static FacebookHelper _instance;

	/////======================================================================================================
	////Constructor/Initializer.
	////======================================================================================================
	
	/*
	 * Private to force calls to getInstance()
	 * @param context Context of Activity that calls getInstance().
	 */
	private FacebookHelper(Context context) {
		mContext = context.getApplicationContext();	
	}

	/**
	 * Instantiates and returns new instance of FacebookHelper if one doesn't exist, or returns already instantiated instance.
	 * @param context Context of Activity that calls method.
	 * @return Instance of class.
	 */
	public static FacebookHelper getInstance(Context context) {
		if (_instance == null) {
			_instance = new FacebookHelper(context);
		} 
		
		return _instance;
	}

	////======================================================================================================
	//// State.
	////======================================================================================================

	/**
	 * @return True if user's Facebook sessions is valid, false otherwise.
	 */
	public boolean isSessionValid() {
		return Session.getActiveSession() == null ? false : true;
	}

	////======================================================================================================
	//// Authentication/Authorization.
	////======================================================================================================

	/**
	 * Remove any credentials relating to Facebook from this User's local UpTo data.s
	 */
	public static void invalidateFacebookSession(Context context) {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
			session = null;
		}
	}

	public boolean checkForPublishPermissions(Activity activity) {
		Session session = Session.getActiveSession();
	    if (session != null) {    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(FACEBOOK_PUBLISH_PERMISSIONS, permissions)) {
	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, FACEBOOK_PUBLISH_PERMISSIONS);
		        session.requestNewPublishPermissions(newPermissionsRequest);
	            return false;	        
	        }	        
	        return true;
	    }
	    return false;
	}
	    
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
	
	
	/**
	 * 
	 * @return True if the current user has logged in with Facebook. False otherwise.
	 */
	public static boolean hasLoggedInWithFacebook(Context context) {
		User user = WorthAShot.get(context).getUser();
		if (user == null) {
			return false;
		}
		
		return user.getFacebookId() > 0 && U.strValid(user.getFacebookToken());
	}

	/**
	 * Check if the user is logged in with Facebook. If they are and the session is still valid,
	 * then we have the basic read information from the user.
	 * @param authType Constants.FACEBOOK_PUBLISH_PERMISSION or Constants.FACEBOOK_READ_PERMISSION
	 * @return true if the user is logged in and a valid session. Otherwise will return false and
	 * try to autorize or reauthorize the user.
	 */
	public boolean getReadPermission(Activity activity, String callingActivity, int authType) {
		mAuthType = authType;

		updatePermissions(1);

		Session session = Session.getActiveSession();
		if (session == null || session.isClosed() || !hasLoggedInWithFacebook(mContext)) {
			// Authorize the user
			launchFacebookAuthActivity(activity);
		} else {
			// As far as the calling activity knows, publish permission has
			// been authorized so do nothing.
			return true;
		}	

		return false;
	}
	

	/**
	 * Launch the FacebookAuthActivity to reauth or auth the app.
	 */
	private void launchFacebookAuthActivity(Activity activity) {
		Intent facebookAuthIntent = new Intent(activity, FacebookAuthActivity.class);
		activity.startActivityForResult(facebookAuthIntent, REAUTH_ACTIVITY_CODE);
	}
	
    /**
     * @return Time of the last call to facebook graph me/permissions
     */
    public long getFacebookLastPermissionUpdate() {    	
    	return WorthAShot.prefs(mContext).getLong(PREFS_LAST_UPDATE, 0);
    }

    /**
     * @param time of Facebook graph me/permissions update.
     */
    public void setFacebookLastPermissionUpdate(long time) {

    	SharedPreferences.Editor editor = WorthAShot.prefs(mContext).edit();
    	editor.putLong(PREFS_LAST_UPDATE, time);
    	editor.commit();
    }

	/**
	 * 
	 * @return True if the user has allowed UpTo to post to their wall.
	 * False otherwise.
	 */
	public boolean hasPublishPermission() {
		SharedPreferences prefs = WorthAShot.prefs(mContext);
    	return prefs.getInt(PREFS_HAS_PUBLISH_PERMISSION, 0) == 1;
	}
	
    /**
     * @param hasAllowed true if the user has allowed posting to their facebook wall.
     * False otherwise.
     */
    public void setHasUserAllowedFacebookPublishPermission(boolean hasAllowed) {
    	SharedPreferences.Editor editor = WorthAShot.prefs(mContext).edit();
    	editor.putInt(PREFS_HAS_PUBLISH_PERMISSION, hasAllowed? 1 : 0);
    	editor.commit();
    }
    

	/**
	 * Update the local store of the users allowed Facebook permissions. Will
	 * only do it every one minute.
	 * @param A value for the number of minutes since the last permissions update to wait
	 * before the next one.
	 */
	public void updatePermissions(int delay) {
		long lastUpdate = getFacebookLastPermissionUpdate();
		long timeSinceLastUpdate = Calendar.getInstance().getTimeInMillis() - lastUpdate;
		if (timeSinceLastUpdate >= (60 * 1000) * delay) {
			setFacebookLastPermissionUpdate(Calendar.getInstance().getTimeInMillis());
			Session session = Session.getActiveSession();
//			Request.executeGraphPathRequestAsync(session, FacebookHelper.FACEBOOK_PERMISSIONS_GRAPH_REQUEST, mePermissionsCallback);	
		}
	}

	/**
	 * Clear the user allowed permissions in our local store.
	 * @return true if removal was successful.
	 */
	public boolean invalidatePermissions() {
		setHasUserAllowedFacebookPublishPermission(false);
		setFacebookLastPermissionUpdate(0);

		SharedPreferences.Editor editor = WorthAShot.prefs(mContext).edit();
		editor.remove(PREFS_LAST_UPDATE);
		editor.remove(PREFS_HAS_PUBLISH_PERMISSION);

		return editor.commit();
	}

	/**
	 * Callback when making a me/permissions request
	 */
	private Request.Callback mePermissionsCallback = new Request.Callback() {		

		@Override
		public void onCompleted(Response response) {		
			if(response == null) {
				invalidatePermissions();
				return;
			}

			GraphObject graphObject = response.getGraphObject();			
			if(graphObject == null) {
				invalidatePermissions();
				return;
			}

			JSONArray data = (JSONArray) graphObject.getProperty("data");
			if(data == null) {
				invalidatePermissions();
				return;
			} 

			JSONObject permissions = data.optJSONObject(0);
			if(permissions == null) {
				invalidatePermissions();
				return;
			}

			// Update the local store user allowed publish permission
			setHasUserAllowedFacebookPublishPermission(permissions.optInt(FacebookHelper.FACEBOOK_PUBLISH_PERMISSIONS.get(0)) == 1);
			return;			
		}
	};

	//===============================================================================================
	// Facebook permissions and Facebook graph request
	//===============================================================================================

	/**
	 * Session (Facebook class) automatically refreshes its token. So we need to make
	 * sure our token in the local store is updated.
	 */
	public void extendAccessToken() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
//			WorthAShot.get(mContext).getUser().setFacebookToken(session.getAccessToken());
		}		
	}
}
