package com.example.worth_a_shot.http.request;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.example.worth_a_shot.http.ApiRequest;
import com.example.worth_a_shot.http.RequestType;

/**
 * Get that drank.
 * @author jmhend
 *
 */
public class DrinkOrderRequest extends ApiRequest {
	
	private static final String TAG = DrinkOrderRequest.class.getSimpleName();
	
////=============================================================================================
////Member variables.
////=============================================================================================
	
	private long mRecipientFacebookId;
	private long mDrinkId;
	
////=============================================================================================
//// Constructor.
////=============================================================================================

	/**
	 * Constructor.
	 * @param appContext
	 * @param method
	 */
	public DrinkOrderRequest(Context appContext, long recipientFacebookId, long drinkId) {
		super(appContext, "orderdrink.php");
		mRecipientFacebookId = recipientFacebookId;
		mDrinkId = drinkId;
	}

////=============================================================================================
//// ApiRequest methods.
////=============================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getAction()
	 */
	@Override
	public String getAction() {
		return Actions.DRINK_ORDER;
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
		pairs.add(new BasicNameValuePair("drinkId", "" + mDrinkId));
		if (mRecipientFacebookId > 0) {
			pairs.add(new BasicNameValuePair("recipientfbid", "" + mRecipientFacebookId));
		}
		
		return pairs;
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
		JSONObject json = new JSONObject(mResponse);
		String success = json.getString("success");
		if (!"success".equals(success)) {
			throw new ParseException(success, 500);
		}
	}

}
