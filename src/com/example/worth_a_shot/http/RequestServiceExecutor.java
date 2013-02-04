/**
 * File:    RequestServiceExecutor.java
 * Created: September 19, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.http;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestServiceExecutor
 * Manages the ThreadPool of the AsyncRequestService's AsyncTasks.
 */
public class RequestServiceExecutor {

	 private static final String TAG = RequestServiceExecutor.class.getSimpleName();
	 
////========================================================================================
//// Static constants.
////========================================================================================
	 
	 private static final int CORE_POOL_SIZE = 6;
	 private static final int MAXIMUM_POOL_SIZE = 128;
	 private static final int KEEP_ALIVE = 1;
	 
////========================================================================================
//// Member variables.
////========================================================================================
	 
	 private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		 private final AtomicInteger mCount = new AtomicInteger(1);
		 
		 public Thread newThread(Runnable r) {
			 return new Thread(r, "AsyncRequestService AsyncTask #" + mCount.getAndIncrement());
		 }
	 };
	 
	 private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

	 public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
}
