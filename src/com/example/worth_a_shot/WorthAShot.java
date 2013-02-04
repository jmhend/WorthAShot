package com.example.worth_a_shot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.worth_a_shot.models.User;
import com.example.worth_a_shot.utils.uil.cache.memory.impl.LRULimitedMemoryCache;
import com.example.worth_a_shot.utils.uil.core.DisplayImageOptions;
import com.example.worth_a_shot.utils.uil.core.ImageLoader;
import com.example.worth_a_shot.utils.uil.core.ImageLoaderConfiguration;

/**
 * Application-wide Context.
 * @author jmhend
 *
 */
public class WorthAShot extends Application {
	
	private static final String TAG = WorthAShot.class.getSimpleName();
	
////================================================================================================
//// AppState
////================================================================================================
	
	public static enum AppState {
		LOGGED_OUT,
		LOGGED_IN
	}
	
////================================================================================================
//// Static constants.
////================================================================================================
	
	private static final String PREFS_FILE = WorthAShot.class.getCanonicalName() + ".PREFS";
	private static final String PREFS_USER_ID = WorthAShot.class.getCanonicalName() + ".USER_ID";
	
////================================================================================================
//// Member variables.
////================================================================================================
	
	private User mUser;

////================================================================================================
//// Constructor.
////================================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		loadUser();
		initImageLoader();
	}
	
////================================================================================================
//// Getters/Setters.
////================================================================================================
	
    /**
     * Initializes the ImageLoader.
     */
    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory()
        .cacheOnDisc()
        .resetViewBeforeLoading()
        .build();
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .memoryCacheExtraOptions(256, 256)
        .defaultDisplayImageOptions(defaultOptions)
        .discCacheSize(40 * 1024 * 1024)
        .memoryCache(new LRULimitedMemoryCache(6 * 1024 * 1024))
        .memoryCacheSize(6 * 1024 * 1024)
        .threadPoolSize(5)
        .build();
        
        ImageLoader.getInstance().init(config);
    }
    
	/**
	 * Get it.
	 * @param context
	 * @return
	 */
	public static WorthAShot get(Context context) {
		if (context == null) {
			return null;
		}
		
		return (WorthAShot) context.getApplicationContext();
	}
	
	/**
	 * App's current User.
	 * @return
	 */
	public User getUser() {
		if (mUser == null) {
			loadUser();
		}
		
		return mUser;
	}
	
	public void setUser(User user) {
		mUser = user;
		if (mUser != null) {
			Editor editor = WorthAShot.prefs(getApplicationContext()).edit();
			editor.putLong(PREFS_USER_ID, mUser.getFacebookId());
			editor.commit();
		}
	}
	
////================================================================================================
//// Application state.
////================================================================================================
	
	public void setAppState(AppState state) {
		switch(state) {
		case LOGGED_OUT:
			processLogout();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Checks to see if there is a user logged in.
	 * @return
	 */
	public boolean isLoggedIn() {
		return getUser() != null;
	}
	
	/**
	 * Load current User from SQLite.
	 * @return True if User loaded correctly.
	 */
	private boolean loadUser() {
		long fbId = fetchFacebookId();
		Log.e(TAG, "fbID: " + fbId);
		
		if (fbId == 0) {
			mUser = null;
			return false;
		}
		
		mUser = new User();
		mUser.setFacebookId(fbId);
		mUser = User.findWithFacebookId(getApplicationContext(), fbId);
		return mUser != null;
	}
	
    public void logout(Activity activity) {
    	new LogoutTask(activity).execute();
    }
	
    /**
     * Perform long running operations to log the User out of the app.
     * @return
     */
	private void processLogout() {
		SharedPreferences.Editor editor = WorthAShot.prefs(getApplicationContext()).edit();
		editor.clear();
		editor.commit();
		
		mUser = null;
		
		// TODO: Invalidate Facebook
	}

    /**
     * LogoutTask
     * Logs the current User out of UpTo and displays the SplashScreen on success.
     */
    private static class LogoutTask extends AsyncTask<Void, Void, Void> {
    	private Activity mActivity;
   	
    	public LogoutTask(Activity activity) {
    		mActivity = activity;
    	}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see android.os.AsyncTask#onPreExecute()
    	 */
    	@Override 
    	protected void onPreExecute() {
    		// Add ProgressDialog in mActivity.
    	}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see android.os.AsyncTask#doInBackground(Params[])
    	 */
		@Override
		protected Void doInBackground(Void... params) {
	    	try {
	    		WorthAShot.get(mActivity).processLogout();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
			return null;
		}   	
		
        /*
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Void result) {
            // hide ProgressDialog.
        	
        	mActivity = null;
        }
    }
  
	
////================================================================================================
//// Utility.
////================================================================================================
	
	/**
	 * Get the current UserID from SharedPrefs.
	 * @return
	 */
	private long fetchFacebookId() {
		SharedPreferences prefs = WorthAShot.prefs(getApplicationContext());
		return prefs.getLong(PREFS_USER_ID, 0);
	}
	
    /**
     * Returns an application-wide SharedPreferences file that can be used throughout Upto.
     */
    public static SharedPreferences prefs(Context context) {
    	return context.getApplicationContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }
}
