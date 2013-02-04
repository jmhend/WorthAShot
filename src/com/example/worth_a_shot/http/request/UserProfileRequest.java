package com.example.worth_a_shot.http.request;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.example.worth_a_shot.http.ApiRequest;
import com.example.worth_a_shot.http.RequestType;
import com.example.worth_a_shot.models.Drink;
import com.example.worth_a_shot.models.Order;
import com.example.worth_a_shot.models.User;

public class UserProfileRequest extends ApiRequest {
	
	private static final String TAG = UserProfileRequest.class.getSimpleName();

////=====================================================================================
//// Member variables.
////=====================================================================================
	
	private long mFacebookId;
	
////=====================================================================================
//// Constructor.
////=====================================================================================

	/**
	 * Constructor.
	 * @param appContext
	 * @param method
	 */
	public UserProfileRequest(Context appContext, long facebookId) {
		super(appContext, "userprofile.php");
		// TODO Auto-generated constructor stub
	}
	
////=====================================================================================
/// ApiRequest methods.
////=====================================================================================

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getAction()
	 */
	@Override
	public String getAction() {
		return Actions.USER_INFO;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getRequestType()
	 */
	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getRequestParameters()
	 */
	@Override
	public List<NameValuePair> getRequestParameters() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("friendfbid", "" + mFacebookId));
		return pairs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#onRequestStart()
	 */
	@Override
	public void onRequestStart() {

	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#onCallbackResponse()
	 */
	@Override
	public void onCallbackResponse() throws JSONException, ParseException {

	}
	
	

}
