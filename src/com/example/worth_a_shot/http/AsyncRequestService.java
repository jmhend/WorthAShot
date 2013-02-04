/**
 * File:    AsyncRequestService.java
 * Created: September 19, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.http;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * AsyncRequestService
 * Variation on Android's IntentService, but uses AsyncTasks as the backing thread structure, to allow for
 * multiple requests in parallel.
 */
@SuppressLint("NewApi")
public abstract class AsyncRequestService extends Service {
	private static final String TAG = AsyncRequestService.class.getSimpleName();
	
////====================================================================================================
//// Requestable.
////====================================================================================================
	
	/**
	 * Requestable
	 * Represents a queue-able server request object to submit to the ExecutorService.
	 */
	private class Requestable implements Runnable {
		private Intent intent;
		
		/**
		 * Constructor.
		 * @param intent The Intent to to process;
		 */
		public Requestable(Intent intent) {
			this.intent = intent;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			if (intent != null) {
				onHandleIntent(intent);
			}
		}
		
	}
	
////====================================================================================================
//// Static constants.
////====================================================================================================
	
	private static final int NUM_THREADS = 5;
	
////====================================================================================================
//// Member variables.
////====================================================================================================

	ExecutorService mExecutorService = Executors.newFixedThreadPool(NUM_THREADS);
	
    private boolean mRedelivery;
    
////====================================================================================================
//// Constructor.
////====================================================================================================

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AsyncRequestService() {
        super();
    }

    /**
     * Sets intent redelivery preferences.  Usually called from the constructor
     * with your preferred semantics.
     *
     * <p>If enabled is true,
     * {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_REDELIVER_INTENT}, so if this process dies before
     * {@link #onHandleIntent(Intent)} returns, the process will be restarted
     * and the intent redelivered.  If multiple Intents have been sent, only
     * the most recent one is guaranteed to be redelivered.
     *
     * <p>If enabled is false (the default),
     * {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_NOT_STICKY}, and if the process dies, the Intent
     * dies along with it.
     */
    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

////====================================================================================================
//// Service lifecycle.
////====================================================================================================
    
    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startId) {  	   	    	
    	mExecutorService.execute(new Requestable(intent));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
////====================================================================================================
//// Abstract methods.
////====================================================================================================  

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    protected abstract void onHandleIntent(Intent intent);
}