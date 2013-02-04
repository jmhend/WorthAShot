/**
 * File:    ApiRequest.java
 * Created: Feb 29, 2012
 * Author:	Viacheslav Panasenko
 */
package com.example.worth_a_shot.http;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.example.worth_a_shot.utils.U;

/**
 * ApiRequest
 * This is an abstract class which represents an API request. It responsible for formatting data
 * for request and handling response result.
 */
public abstract class ApiRequest {

 	private static final String TAG = ApiRequest.class.getSimpleName();
 	
////=========================================================================================
//// Status.
////=========================================================================================

 	/**
 	 * RequestStatus
 	 * Denotes the current status of the ApiRequest.
 	 */
    public enum RequestStatus {
    	STATUS_DEFAULT(RestService.RESULT_NONE),
        STATUS_OK(RestService.RESULT_OK),
        STATUS_STARTED(RestService.RESULT_STARTED),
        STATUS_ERROR(RestService.RESULT_ERROR);

        private int mCode;

        /**
         * Enum constructor.
         * @param code
         */
        private RequestStatus(int code) {
            mCode = code;
        }

        /**
         * @return The current RequestStatus code.
         */
        public int getCode() {
            return mCode;
        }
    }
    
////=========================================================================================
//// FileBodyPair
////=========================================================================================
    
    /**
     * FileBodyPair
     * Pairs a HTTP Post parameter to a FileBody String path.
     */
    public static class FileBodyPair {
    	private final String mParam;
    	private final String mPath;
    	
    	/**
    	 * Pairs a HTTP Post parameter to a FileBody String path.
    	 * @param param
    	 * @param path
    	 */
    	public FileBodyPair(String param, String path) {
    		mParam = param;
    		mPath = path;
    	}   	
    	
    	/**
    	 * @return The HTTP Post paramter.
    	 */
    	public String getPostParam() {
    		return mParam;
    	}
    	
    	/**
    	 * @return The FileBody path.
    	 */
    	public String getPath() {
    		return mPath;
    	}
    }
    
////=========================================================================================
//// Static constants.
////=========================================================================================
    
    protected static final String JSON_SUCCESS = "success";
    
//    private static final String BASE_URL = "http://67.194.109.139/";
    private static final String BASE_URL = "http://67.194.109.139/mhack/";
    
////=========================================================================================
//// Member variables.
////=========================================================================================

    protected Context mContext;
    private String mMethod;
    private RequestStatus mStatus = RequestStatus.STATUS_DEFAULT;

    protected String mResponse;
    protected int mResponseCode;
    protected List<NameValuePair> mRequestParameters;
    private List<FileBodyPair> mFileBodyPairs;
    private String mErrorMessage;
    
    private boolean mHasRetried;
    private boolean mShouldIgnoreCooldown;
    
    private Bundle mExtras;

////=========================================================================================
//// Constructor.
////=========================================================================================
    /**
     * Default constructor.
     * @param appContext Parent context.
     * @param method Api method.
     */
    public ApiRequest(Context appContext, String method) {
        mContext = appContext.getApplicationContext();
        mMethod = method;
        mRequestParameters = new ArrayList<NameValuePair>();
        mShouldIgnoreCooldown = false;
    }

    
////=========================================================================================
//// Getters/Setters.
////=========================================================================================
    
    /**
     * @return This Api request's method.
     *         i.e, to which UpTo API URL it is POSTing to.
     */
    
    public String getMethod() {
        return mMethod;
    }

    /**
     * @param method This Api request's method.
     * i.e, to which UpTo API URL it is POSTing to.
     */
    public void setMethod(String method) {
        this.mMethod = method;
    }

    /**
     * @return The RequestStatus of this API request. i.e, STATUS_DEFAULT, STATUS_STARTED,
     *         STATUS_OK, or STATUS_ERROR.
     */
    public int getStatus() {
        return mStatus.getCode();
    }

    /**
     * @param status The Status of this API request. i.e, STATUS_STARTED,
     * STATUS_OK, or STATUS_ERROR.
     */
    public void setStatus(RequestStatus status) {
        this.mStatus = status;
    }

    /**
     * @return The response message given by the server.
     */
    public String getResponse() {
        return mResponse;
    }

    /**
     * @param response The response message given by the server.
     */
    public void setResponse(String response) {
        this.mResponse = response;
    }
    
	/**
	 * Adds a NameValuePair to POST paramaters.
	 * @param key
	 * @param value
	 */
	public void addPair(String key, String value) {
		if (mRequestParameters == null) mRequestParameters = new ArrayList<NameValuePair>();
		mRequestParameters.add(new BasicNameValuePair(key, value));
	}

    /**
     * @param requestParameters the requestParameters to set
     */
    public void setRequestParameters(List<NameValuePair> requestParameters) {
        this.mRequestParameters = requestParameters;
    }

    /**
     * @return request URL (BASE_URL + METHOD).
     */
    public String getRequestUrl() {
        return BASE_URL + mMethod;
    }


    /**
     * @param mErrorMessage The Error message received from the server.
     */
    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

    /**
     * @return The Error message received from the server.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * @param responseCode The HTTP Response code from the server's response.
     */
    public void setResponseCode(int responseCode) {
        this.mResponseCode = responseCode;
    }

    /**
     * @return The HTTP Response code from the server's response.
     */
    public int getResponseCode() {
        return this.mResponseCode;
    }
    
    /**
     * @return True if this ApiRequest has already retried to contact the server, false otherwise.
     */
    public boolean hasRetried() {
    	return mHasRetried;
    }
    
    
////=========================================================================================
//// Request methods.
////=========================================================================================
    
    /**
     * Returns a Filebody that should be attached to the request parameters of this ApiRequest.
     * ApiRequest subclasses should @Override this method if they need to attach images, etc.
     * @return 
     */
    public List<FileBodyPair> getFileBodyPairs() {
    	return mFileBodyPairs;
    }
    
    /**
     * Add a pairing of HTTP Post parameter and FileBody String path to attach to this ApiRequest.
     * @param param
     * @param fileBodyPath
     */
    public void addFileBodyPair(String param, String fileBodyPath) {
    	if (U.strValid(param) && U.strValid(fileBodyPath)) {
    		if (mFileBodyPairs == null) mFileBodyPairs = new ArrayList<FileBodyPair>();
    		mFileBodyPairs.add(new FileBodyPair(param, fileBodyPath));
    	}
    }
    
    /**
     * ApiRequest subclasses should @Override this method if they need to be prevented from
     * sending for any reason.
     * @return True if this request should be performed, false otherwise.
     */
    public boolean shouldSendRequest() {
    	return true;
    }
    
    /**
     * @return The Extras that details this ApiRequest.
     */
    public Bundle getExtras() {
    	return mExtras;
    }
    /**
     * Adds a Bundle of extras to this ApiRequest that can be used to identify it's state, or any
     * other details about it.
     * @param extras The extras to attach.
     */
    public void setExtras(Bundle extras) {
    	mExtras = extras;
    }
        
    /**
     * How long between calls of this type of ApiRequest should wait before executing again.
     * @return The time (in milliseconds) between calls, or 0 if no cooldown necessary.
     */
    public long getCooldownTimeInMillis() {
    	return 0;
    }
    
    /**
     * Tells this ApiRequest that it needs to be called, even if its cooldown time hasn't elapsed yet.
     */
    public void ignoreCooldown() {
    	mShouldIgnoreCooldown = true;
    }

    
////=========================================================================================
//// HTTP Error response methods.
////=========================================================================================
    
    /**
     * How this ApiRequest should react if the server sends back an Unauthorized (HTTP 401) response.
     */
    public void onUnauthorizedRequest() { 

    }
    
    /**
     * How this ApiRequest should react if the server sends back an Internal Server Error (HTTP 500) response.
     */
    public void onInternalServerError() {
 
    }
   
    /**
     * How this ApiRequest should react if the request times out.
     */
    public void onRequestTimeout() {
    	// TODO: Retry?
    }
    
////=========================================================================================
//// Abstract methods.
////=========================================================================================
    
    /**
     * @return An action that is used for broadcasting status change notifications.
     */
    public abstract String getAction();
    
    /**
     * @return Which RequestType the give ApiRequest should use.
     */
    public abstract RequestType getRequestType();
    
    /**
     * @return The List of NameValuePairs to be used as parameters for API requests.
     */
    public abstract List<NameValuePair> getRequestParameters();

    /**
     * Called BEFORE this ApiRequest contacts the server. 
     */
    public abstract void onRequestStart();

    /**
     * Called when the server responds to this ApiRequest. Handle JSON parsing,
     * SQLite updates, etc.
     * @throws JSONException In case of errors in JSON.
     * @throws ParseException In case of parsing error.
     */
    public abstract void onCallbackResponse() throws JSONException, ParseException;
    
////=========================================================================================
//// Actions
////=========================================================================================
    
    
    /**
     * Actions
     * Lists the Action of ApiRequest subclasses.
     */
    public static class Actions {
    	private Actions() { }
    	
    	private static final String PREFIX = Actions.class.getCanonicalName() + ".";
    	
    	public static final String DRINKS_SEARCH = PREFIX + "DRINKS_FOR_VENUE";
    	public static final String DRINK_ORDER = PREFIX + "DRINK_ORDER";
    	public static final String FRIENDS = PREFIX + "FRIENDS";
    	public static final String ORDER_FEED = PREFIX + "ORDER_FEED";
    	public static final String USER_INFO = PREFIX + "USER_INFO";
    	public static final String USER_ADD = PREFIX + "USER_ADD";
    }
}







