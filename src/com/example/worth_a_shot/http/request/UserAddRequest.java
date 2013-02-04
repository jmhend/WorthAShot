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
import com.example.worth_a_shot.models.User;

/**
 * Sign this bitch up.
 * @author jmhend
 *
 */
public class UserAddRequest extends ApiRequest {

	private static final String TAG = UserAddRequest.class.getSimpleName();
	
////=======================================================================================
//// Member variables.
////=======================================================================================
	
	private long mFacebookId;
	private String mFacebookToken;
	
////=======================================================================================
//// Constructor.
////=======================================================================================
	
	public UserAddRequest(Context appContext, long facebookId, String facebookToken) {
		super(appContext, "createuser.php");
		mFacebookId = facebookId;
		mFacebookToken = facebookToken;
	}

////=======================================================================================
//// ApiRequest methods.
////=======================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.http.ApiRequest#getAction()
	 */
	@Override
	public String getAction() {
		return Actions.USER_ADD;
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
		pairs.add(new BasicNameValuePair("fbid", "" + mFacebookId));
		pairs.add(new BasicNameValuePair("fboauth", mFacebookToken));
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
		User user = new User();
		user.fillFromJson(mContext, json);
		user.save(mContext);
	}
}
