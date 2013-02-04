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
import com.example.worth_a_shot.sqlite.DatabaseHelper.Tables;
import com.example.worth_a_shot.sqlite.DatabaseHelper.UserFields;

public class User extends PersistentObject {
	
	private static final String TAG = User.class.getSimpleName();
	
////=======================================================================================
//// Member variables.
////=======================================================================================
	
	private long mFacebookId;
	private String mFacebookToken;
	private String mName;
	private String mAvatarUrl;
	private String mAuthToken;
	
////=======================================================================================
//// Getters/setters.
////=======================================================================================

	public long getFacebookId() {
		return mFacebookId;
	}
	
	public void setFacebookId(long fbId) {
		mFacebookId = fbId;
	}
	
	public String getFacebookToken() {
		return mFacebookToken;
	}
	
	public void setFacebookToken(String auth) {
		mFacebookToken = auth;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getAvatarUrl() {
		return mAvatarUrl;
	}
	
	public void setAvatarUrl(String avatar) {
		mAvatarUrl = avatar;
	}
	
	public String getAuthToken() {
		return mAuthToken;
	}
	
	public void setAuthToken(String auth) {
		mAuthToken = auth;
	}
	

	
////=======================================================================================
//// PersistentObject methods.
////=======================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteId()
	 */
	@Override
	public long getRemoteId() {
		return mFacebookId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteColumnName()
	 */
	@Override
	protected String getRemoteColumnName() {
		return UserFields.FB_ID.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getTableName()
	 */
	@Override
	public String getTableName() {
		return Tables.USERS;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getContentValues()
	 */
	@Override
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(UserFields.FB_ID.toString(), mFacebookId);
		values.put(UserFields.FB_TOKEN.toString(), mFacebookToken);
		values.put(UserFields.NAME.toString(), mName);
		values.put(UserFields.AUTH_TOKEN.toString(), mAuthToken);
		values.put(UserFields.AVATAR_URL.toString(), mAvatarUrl);
		return values;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getObjectMapping()
	 */
	@Override
	public List<NameValuePair> getObjectMapping() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("fbid", "" + mFacebookId));
		pairs.add(new BasicNameValuePair("fboauth", mFacebookToken));
		pairs.add(new BasicNameValuePair("name", mName));
		pairs.add(new BasicNameValuePair("fbprofpic", mAvatarUrl));
		pairs.add(new BasicNameValuePair("authtoken", mAuthToken));
		return pairs;
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#fillFromCursor(android.content.Context, android.database.Cursor)
	 */
	@Override
	protected boolean fillFromCursor(Context context, Cursor c) {
		if (c == null) return false;
		mFacebookId = c.getLong(UserFields.FB_ID.ordinal());
		mFacebookToken = c.getString(UserFields.FB_TOKEN.ordinal());
		mName = c.getString(UserFields.NAME.ordinal());
		mAvatarUrl = c.getString(UserFields.AVATAR_URL.ordinal());
		mAuthToken = c.getString(UserFields.AUTH_TOKEN.ordinal());
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
			mFacebookId = json.getLong("fbid");
			mFacebookToken = json.getString("fboauth");
			mAvatarUrl = json.getString("fbprofpic");	
			mName = json.getString("name");
			mAuthToken = json.getString("authtoken");
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return false;
	}
	
////=======================================================================================
//// Finders.
////=======================================================================================
	
	
	/**
	 * Find the User with the given 'userId'.
	 * @param context
	 * @param facebookId
	 * @return
	 */
	public static User findWithFacebookId(Context context, long facebookId) {
		if (facebookId <= 0) return null;
		
    	User user = new User();
    	boolean created = user.fillWhere(context, UserFields.FB_ID.toString() + "=?", "" + facebookId);
   	
    	return (created) ? user : null;
	}
	
////=======================================================================================
//// Getters
////=======================================================================================
	
	public static List<User> getAllFriends(Context context) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		if (db == null) return null;
		
		return null;
	}
	
	
	
	
	
	
	
	

}
