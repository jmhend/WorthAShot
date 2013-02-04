package com.example.worth_a_shot.http.request;

import java.text.ParseException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.example.worth_a_shot.http.ApiRequest;
import com.example.worth_a_shot.http.RequestType;
import com.example.worth_a_shot.models.Drink;
import com.example.worth_a_shot.models.Order;
import com.example.worth_a_shot.models.User;

public class OrderFeedRequest extends ApiRequest {

	public OrderFeedRequest(Context appContext) {
		super(appContext, "getalldrinks.php");
	}

	@Override
	public String getAction() {
		return Actions.ORDER_FEED;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public List<NameValuePair> getRequestParameters() {
		return null;
	}

	@Override
	public void onRequestStart() {
			
	}

	@Override
	public void onCallbackResponse() throws JSONException, ParseException {
		JSONArray arr = new JSONArray(mResponse);
		int len = arr.length();		
		for (int i = 0; i < len; i++) {
			JSONObject json = arr.getJSONObject(i);
			JSONObject userJson = json.getJSONArray("user").getJSONObject(0);
			JSONObject recipientJson = json.getJSONArray("recipient").getJSONObject(0);
			JSONObject drinkJson = json.getJSONArray("drink").getJSONObject(0);
			
			User user = new User();
			user.fillFromJson(mContext, userJson);
			
			User recipient = new User();
			recipient.fillFromJson(mContext, recipientJson);
			
			Drink drank = new Drink();
			drank.fillFromJson(mContext, drinkJson);
			
			long orderId = json.getLong("orderid");
			long orderTime = json.getLong("ordertime");
			String status = json.getString("status");
			
			Order order = new Order();
			order.setOrderId(orderId);
			order.setFacebookId(userJson.getLong("fbid"));
			order.setRecipientFacebookId(recipientJson.getLong("fbid"));
			order.setDrinkId(drinkJson.getLong("drinkid"));
			order.setOrderTime(orderTime);
			order.setStatus(status);
			
			user.save(mContext);
			recipient.save(mContext);
			drank.save(mContext);
			order.save(mContext);
		}
	}

}
