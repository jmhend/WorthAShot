package com.example.worth_a_shot.http.request;

import java.text.ParseException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import android.content.Context;

import com.example.worth_a_shot.http.ApiRequest;
import com.example.worth_a_shot.http.RequestType;

public class FriendsRequest extends ApiRequest {
	
	private static final String TAG = FriendsRequest.class.getSimpleName();
	
////========================================================================================
//// Constructor.
////=======================================================================================

	public FriendsRequest(Context appContext) {
		super(appContext, "");
	}
	
////========================================================================================
//// ApiRequest methods.
////========================================================================================

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getAction()
	 */
	@Override
	public String getAction() {
		return Actions.FRIENDS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getRequestType()
	 */
	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getRequestParameters()
	 */
	@Override
	public List<NameValuePair> getRequestParameters() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#onRequestStart()
	 */
	@Override
	public void onRequestStart() { }

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#onCallbackResponse()
	 */
	@Override
	public void onCallbackResponse() throws JSONException, ParseException {
		// TODO Auto-generated method stub
		
	}

}
