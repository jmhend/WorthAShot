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

public class DrinksSearchRequest extends ApiRequest {
	
	private static final String TAG = DrinksSearchRequest.class.getSimpleName();
	
	private List<String> mTerms;

	public DrinksSearchRequest(Context appContext, List<String> searchTerms) {
		super(appContext, "listdrinks.php");
		mTerms = searchTerms;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getAction()
	 */
	@Override
	public String getAction() {
		return Actions.DRINKS_SEARCH;
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
		List<NameValuePair> pairs = null;
		if (mTerms != null) {
			pairs = new ArrayList<NameValuePair>();
			for (String term : mTerms) {
				pairs.add(new BasicNameValuePair("", term));
			}
		}		
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
		JSONArray jsonArray = new JSONArray(mResponse);
		int len = jsonArray.length();
		
		for (int i = 0; i < len; i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Drink drink = new Drink();
			drink.fillFromJson(mContext, json);
			drink.save(mContext);
		}
	}

}
