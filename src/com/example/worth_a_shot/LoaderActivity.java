package com.example.worth_a_shot;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class LoaderActivity extends SherlockFragmentActivity {
	private static final String TAG = LoaderActivity.class.getSimpleName();
	
////========================================================================================
//// Abstract methods.
////========================================================================================
	
	/**
	 * What to load in the background.
	 */
	public void load() { }
	
	/**
	 * What to do once the data has been loaded.
	 */
	public void onLoaded() { }
	
	/**
	 * Request a load.
	 * @return
	 */
	public boolean requestLoad() {
		if (mLoaderTask != null) {
			if (mLoaderTask.getStatus() == Status.PENDING ||
				mLoaderTask.getStatus() == Status.RUNNING) {
				return false;
			}		
			mLoaderTask.unbind();
		}		
		mLoaderTask = new LoaderTask(this);
		mLoaderTask.execute();		
		return true;
	}
	
////========================================================================================
//// Member variables.
////========================================================================================
	
	private LoaderTask mLoaderTask;
	
////========================================================================================
//// Activity lifecycle.
////========================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		if (mLoaderTask != null) {
			mLoaderTask.unbind();
		}
		super.onDestroy();
	}
	
////========================================================================================
//// Receiver.
////========================================================================================
	
    /*
     * (non-Javadoc)
     * @see android.content.ContextWrapper#registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)
     */
    @Override 
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    	LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    	
    	return null;
    }
    
    /*
     * (non-Javadoc)
     * @see android.content.ContextWrapper#unregisterReceiver(android.content.BroadcastReceiver)
     */
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
	
	
////========================================================================================
//// LoaderTask
////========================================================================================
	
	/**
	 * Load shit.
	 * @author jmhend
	 */
	private static class LoaderTask extends AsyncTask<Void, Void, Void> {

		private LoaderActivity mActivity;
		
		/**
		 * Constructor.
		 * @param activity
		 */
		public LoaderTask(LoaderActivity activity) {
			mActivity = activity;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			mActivity.load();
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		public void onPostExecute(Void result) {
			mActivity.onLoaded();
		}
		
		/**
		 * Unbind the enclosing Activity from this AsyncTask.
		 */
		public void unbind() {
			mActivity = null;
		}
	}

}
