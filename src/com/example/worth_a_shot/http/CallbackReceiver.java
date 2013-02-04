/**
 * File:	CallbackReceiver.java
 * Created:	May 24, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * CallbackReceiver
 * Class description A BroadcastReceiver that listens for response from server requests 
 * and filters actions based upon the resultCode received from the sent Intent object.
 */
public abstract class CallbackReceiver extends BroadcastReceiver {

	private static final String TAG = "CallbackReceiver";
	
////=======================================================================================
//// BroadcastReceiver methods.
////=======================================================================================

	/**
	 * Get resultCode from Intent object, then call appropriate onResult<> method.
	 */
	@Override
	public final void onReceive(final Context context, final Intent intent) {
		int resultCode = intent.getExtras().getInt(RestService.EXTRA_RESULT_CODE);
		switch(resultCode) {
			case RestService.RESULT_STARTED:
				onResultStarted(context, intent);
				break;
			case RestService.RESULT_OK:
				onResultOk(context, intent);
				break;
			case RestService.RESULT_ERROR:
				onResultError(context, intent);
				break;
			default:
				Log.e(TAG, "Invalid resultCode: " + resultCode);
				break;
		}
	}

////=======================================================================================
//// CallbackReceiver methods.
////=======================================================================================
	
	/**
	 * Method to perform when this Receiver intercepts a a broadcast with RESULT_STARTED
	 */
	public void onResultStarted(Context context, Intent intent) { }

	/**
	 * Method to perform when this Receiver intercepts a a broadcast with RESULT_OK
	 */
	public abstract void onResultOk(Context context, Intent intent);

	/**
	 * Method to perform when this Receiver intercepts a a broadcast with RESULT_ERROR
	 */
	public void onResultError(Context context, Intent intent) { }
}

