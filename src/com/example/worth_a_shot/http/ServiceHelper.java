/**
 * File:    ServiceHelper.java
 * Created: Jan 18, 2012
 * Author:	Viacheslav Panasenko
 */
package com.example.worth_a_shot.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ServiceHelper
 * Helper class to handle requests to various intent services and provide basic
 * functionality.
 */
public class ServiceHelper {

    private static final String TAG = ServiceHelper.class.getSimpleName();
    
    public static final String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    
////================================================================================================
//// Member variables.
////================================================================================================

    private static ServiceHelper _instance;

    private Context mContext;
    public Map<Integer, ApiRequest> mPendingRequests = Collections.synchronizedMap(new HashMap<Integer, ApiRequest>(5));
    private volatile int mUid = 1;

////================================================================================================
//// Contstructor/Initializer.
////================================================================================================
    
    /**
     * Default constructor.
     * @param context Parent context.
     */
    private ServiceHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Creates (if needed) and returns instance of this class.
     * @return ServiceHelper instance.
     */
    public static ServiceHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new ServiceHelper(context);
        }
        
        return _instance;
    }
    
////================================================================================================
//// Getters/Setters
////================================================================================================

    /**
     * Returns callback object that corresponds to given id.
     * @param id Id of the stored callback.
     * @return Callback instance.
     */
    public ApiRequest getCallback(int id) {
        return mPendingRequests.get(Integer.valueOf(id));
    }

////================================================================================================
//// Servicing methods.
////================================================================================================
    
    /**
     * Removes pending callback.
     * @param id Id of the callback.
     */
    public void removePendingCallback(int id) {
        mPendingRequests.remove(Integer.valueOf(id));
    }

    /**
     * Starts service for requested action.
     * @return true if service successfully started, false otherwise.
     */
    public boolean startService(ApiRequest request) {
    	Log.e(TAG, "startService");
        Intent serviceIntent = new Intent(mContext, RestService.class);
        serviceIntent.putExtra(EXTRA_REQUEST_ID, mUid);
                  
        mPendingRequests.put(Integer.valueOf(mUid), request);

        mUid++;

        mContext.startService(serviceIntent);

        return true;
    }
}
