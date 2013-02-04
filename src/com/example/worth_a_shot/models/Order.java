package com.example.worth_a_shot.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.worth_a_shot.sqlite.DatabaseHelper;
import com.example.worth_a_shot.sqlite.DatabaseHelper.OrdersFields;
import com.example.worth_a_shot.sqlite.DatabaseHelper.Tables;
import com.example.worth_a_shot.utils.U;

/**
 * ORDER UP
 * @author jmhend
 *
 */
public class Order extends PersistentObject {
	
	private static final String TAG = Order.class.getSimpleName();
	
	public static enum OrderStatus {
		OPEN("OPEN"),
		ClOSED("CLOSED"),
		EXPIRED("EXPIRED");
		
		private String mName;
		
		private OrderStatus(String name) {
			mName = name;
		}
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
////=================================================================================
//// Member variables.
////=================================================================================
	
	private long mOrderId;
	private long mFacebookId;
	private long mDrinkId;
	private long mOrderTime;
	private long mRecipientFacebookId;
	private OrderStatus mStatus = OrderStatus.OPEN;
	
	private User mUser;
	private User mRecipient;
	private Drink mDrink;
	
////=================================================================================
//// Getters/Setters
////=================================================================================
	
	public long getOrderId() {
		return mOrderId;
	}
	public void setOrderId(long orderId) {
		this.mOrderId = orderId;
	}
	public long getFacebookId() {
		return mFacebookId;
	}
	public void setFacebookId(long facebookId) {
		this.mFacebookId = facebookId;
	}
	public long getRecipientFacebookId() {
		return mRecipientFacebookId;
	}
	public void setRecipientFacebookId(long recFacebookId) {
		this.mRecipientFacebookId = recFacebookId;
	}
	public long getDrinkId() {
		return mDrinkId;
	}
	public void setDrinkId(long drinkId) {
		this.mDrinkId = drinkId;
	}
	public long getOrderTime() {
		return mOrderTime;
	}
	public void setOrderTime(long time) {
		this.mOrderTime = time;
	}

	public void setStatus(String status) {
		if ("OPEN".equals(status)) {
			mStatus = OrderStatus.OPEN;
		} else if ("CLOSED".equals(status)) {
			mStatus = OrderStatus.ClOSED;
		} else {
			mStatus = OrderStatus.EXPIRED;
		}
	}
	public OrderStatus getStatus() {
		return mStatus;
	}
	
	public User getUser() {
		return mUser;
	}
	public void setUser(User user) {
		mUser = user;
	}
	public User getRecipient() {
		return mRecipient;
	}
	public void setRecipient(User rec) {
		mRecipient = rec;
	}
	public Drink getDrink() {
		return mDrink;
	}
	public void setDrink(Drink drink) {
		mDrink = drink;
	}

	
////=================================================================================
//// PersistentObject methods.
////=================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteId()
	 */
	@Override
	public long getRemoteId() {
		return mOrderId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteColumnName()
	 */
	@Override
	protected String getRemoteColumnName() {
		return OrdersFields.ORDER_ID.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getTableName()
	 */
	@Override
	public String getTableName() {
		return Tables.ORDERS;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getContentValues()
	 */
	@Override
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(OrdersFields.ORDER_ID.toString(), mOrderId);
		values.put(OrdersFields.FACEBOOK_ID.toString(), mFacebookId);
		values.put(OrdersFields.FACEBOOK_RECIPIENT_ID.toString(), mRecipientFacebookId);
		values.put(OrdersFields.DRINK_ID.toString(), mDrinkId);
		values.put(OrdersFields.ORDER_TIME.toString(), mOrderTime);
		values.put(OrdersFields.STATUS.toString(), mStatus.toString());		
		
		return values;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getObjectMapping()
	 */
	@Override
	public List<NameValuePair> getObjectMapping() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("ordernum", "" + mOrderId));
		pairs.add(new BasicNameValuePair("fbid", "" + mFacebookId));
		pairs.add(new BasicNameValuePair("recipientfbid", "" + mRecipientFacebookId));
		pairs.add(new BasicNameValuePair("drinkid", "" + mDrinkId));
		pairs.add(new BasicNameValuePair("ordertime", "" + mOrderTime));
		pairs.add(new BasicNameValuePair("status", "" + mStatus.toString()));
		return pairs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#fillFromCursor(android.content.Context, android.database.Cursor)
	 */
	@Override
	protected boolean fillFromCursor(Context context, Cursor c) {
		if (c == null) return false;		
		mOrderId = c.getLong(OrdersFields.ORDER_ID.ordinal());
		mFacebookId = c.getInt(OrdersFields.FACEBOOK_ID.ordinal());
		mRecipientFacebookId = c.getInt(OrdersFields.FACEBOOK_RECIPIENT_ID.ordinal());
		mDrinkId = c.getLong(OrdersFields.DRINK_ID.ordinal());
		mOrderTime = c.getLong(OrdersFields.ORDER_TIME.ordinal());
		setStatus(c.getString(OrdersFields.STATUS.ordinal())); 
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#fillFromJson(android.content.Context, org.json.JSONObject)
	 */
	@Override
	public boolean fillFromJson(Context context, JSONObject json) {
		if (json == null) return false;
		try {
			mOrderId = json.getLong("orderid");
			mFacebookId = json.getLong("fbid");
			mRecipientFacebookId = json.getLong("recipientfbid");
			mDrinkId = json.getLong("drinkid");
			mOrderTime = json.getLong("ordertime");
			setStatus(json.getString("status"));
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
////=================================================================================
//// Getter methods.
////=================================================================================
	
	
	public static List<Order> getAllFriendOrders(Context context) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		if (db == null) return null;
		
		String where = Tables.ORDERS;
		String orderBy = OrdersFields.ORDER_TIME + " DESC";
		
		Cursor c = U.initCursor(db.query(where, null, null, null, null, null, orderBy));
		if (c == null) return null;
		
		List<Order> orders = new ArrayList<Order>();
		do {
			Order order = new Order();
			order.fillFromCursor(context, c);
			
			User user = User.findWithFacebookId(context, order.getFacebookId());
			User recipient = User.findWithFacebookId(context, order.getRecipientFacebookId());
			Drink drank = Drink.findWithDrinkId(context, order.getDrinkId());
			
			order.setUser(user);
			order.setRecipient(recipient);
			order.setDrink(drank);
			
			orders.add(order);
		} while (c.moveToNext());
		c.close();
		
		return orders;
	}
}







