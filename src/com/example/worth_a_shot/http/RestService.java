/**
 * File:    AuthorizationService.java
 * Created: Jan 18, 2012
 * Author:	Viacheslav Panasenko
 */
package com.example.worth_a_shot.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.worth_a_shot.http.ApiRequest.RequestStatus;
import com.example.worth_a_shot.http.HttpEngine.HttpCodes;

/**
 * AuthorizationService
 * Implements back-end communication for user authorization.
 */
public class RestService extends AsyncRequestService {

    private static final String TAG = RestService.class.getSimpleName();
    
    // Service extras for REST engine 
    public static final String EXTRA_RESULT_CODE = "com.upto.android.RESULT_CODE";
    public static final String EXTRA_REQUEST_ID = "com.upto.android.REQUEST_ID";
    public static final String EXTRA_ERROR_MESSAGE = "com.upto.android.ERROR_MESSAGE";
    public static final String EXTRA_RESPONSE_CODE = "com.upto.android.RESPONSE_CODE";
    public static final String EXTRA_STRING_EXTRA = "com.upto.android.STRING_EXTRA";
    
    // Result codes
    public static final int RESULT_STARTED = 100;
    public static final int RESULT_ERROR = 400;
    public static final int RESULT_OK = 200;
    public static final int RESULT_NONE = -1;
    
////==================================================================================
//// Constructors/initializers.
////==================================================================================

    /**
     * Default constructor.
     */
    public RestService() {
        super();
    }

    /*
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
	@Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int requestId = extras.getInt(ServiceHelper.EXTRA_REQUEST_ID);
            ApiRequest request = ServiceHelper.getInstance(this).getCallback(requestId);
            
            // Broken callback
            if (request == null) {
            	Log.e(TAG, "NULL callback with RequestID: " + requestId);
            	ServiceHelper.getInstance(this).removePendingCallback(requestId);
            	return;
            }

 
            request.setStatus(RequestStatus.STATUS_STARTED);
            request.onRequestStart();
            sendStatusBroadcast(request);
            
            String serverResponse = "";                     
            try {
                serverResponse = HttpEngine.getInstance(this).performRequest(request);
                
            // If not sending this request, return.
            } catch (NotSendingRequestException e) {
            	request.setStatus(RequestStatus.STATUS_OK);
            	sendStatusBroadcast(request);
            	ServiceHelper.getInstance(this).removePendingCallback(requestId);
            	return;
            
        	// Handle specific HttpResponseExceptions.
        	} catch (HttpResponseException e) {
            	setRequestError(request, e);
                if (e != null) {              	
                	String httpError = e.getMessage();
                	
                	int httpCode = e.getStatusCode();
                	Log.e(TAG + "HTTP Code: ", "" + httpCode);
                	Log.e(TAG + " HTTP Message: ", httpError);

                    request.setErrorMessage(httpError);
                    request.setResponseCode(httpCode);   
                    
                    // Depending on the HTTP status code, react accordingly.
                    if (httpCode == HttpCodes.UNAUTHORIZED) {
                    	request.onUnauthorizedRequest();
                    } else if (httpCode == HttpCodes.TIMEOUT) {
                    	request.onRequestTimeout();
                    } else if (httpCode == HttpCodes.INTERNAL_SERVER_ERROR) {
                    	request.onInternalServerError();
                    } else if (httpCode == HttpCodes.NOT_FOUND) {
                    	request.setErrorMessage("Invalid login.");
                    }
                }
            } catch (UnsupportedEncodingException e) {
            	setRequestError(request, e);
            } catch (ClientProtocolException e) {
            	setRequestError(request, e);
            } catch (IOException e) {
                setRequestError(request, e);
            }

            Log.d(TAG, "2");
            
            try {
            	Log.d(TAG, "status: " + request.getStatus());
                request.setResponse(serverResponse);
                request.onCallbackResponse();
                if (request.getStatus() == RESULT_STARTED) request.setStatus(RequestStatus.STATUS_OK);                   
            } catch (Exception e) {
            	e.printStackTrace();
                request.setStatus(RequestStatus.STATUS_ERROR);
            } finally {
            	Log.d(TAG, "sending broadcase");
            	sendStatusBroadcast(request);
            	ServiceHelper.getInstance(this).removePendingCallback(requestId);
            }
        }
    }
    
////===============================================================================================
//// Status Broadcast.
////===============================================================================================
	
    /**
     * Sends an Application-wide broadcast alerting any interested CallbackReceivers
     * the status of this request.
     * @param callback The ApiRequest callback whose status is to be broadcasted.
     */
    private void sendStatusBroadcast(ApiRequest callback) {
        Intent broadcast = new Intent(callback.getAction());
        broadcast.putExtra(EXTRA_RESULT_CODE, callback.getStatus());
        broadcast.putExtra(EXTRA_ERROR_MESSAGE, callback.getErrorMessage());
        broadcast.putExtra(EXTRA_RESPONSE_CODE, callback.getResponseCode());
        
        Bundle extras = callback.getExtras();
        if (extras != null) broadcast.putExtras(extras);
        
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }
    
////===============================================================================================
//// Utility methods.
////===============================================================================================
    
    /**
     * Handle request Exceptions.
     * @param request
     * @param e
     */
    private void setRequestError(ApiRequest request, Exception e) {
    	Log.e(TAG, "Error in onHandleIntent", e);
        request.setStatus(RequestStatus.STATUS_ERROR);
    }
    

}
