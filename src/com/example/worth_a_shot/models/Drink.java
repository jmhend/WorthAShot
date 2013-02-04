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
import com.example.worth_a_shot.sqlite.DatabaseHelper.DrinksFields;
import com.example.worth_a_shot.sqlite.DatabaseHelper.Tables;
import com.example.worth_a_shot.utils.U;

/**
 * DRANKKK
 * @author jmhend
 *
 */
public class Drink extends PersistentObject {
	
	private static final String TAG = Drink.class.getSimpleName();
	
////===============================================================================================
//// Member variables.
////===============================================================================================
	
	private long mDrinkId;
	private String mName;
	private List<String> mIngredients;
	
////===============================================================================================
//// Getters/Setters
////===============================================================================================

	public long getDrinkId() {
		return mDrinkId;
	}

	public void setDrinkId(long drinkId) {
		this.mDrinkId = drinkId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public List<String> getIngredients() {
		return mIngredients;
	}

	public void setIngredient(List<String> ingredients) {
		this.mIngredients = ingredients;
	}
	
	public String encodeIngredients() {
		return null;
	}
	
	public List<String> decodeIngredients(String ingredientsHash) {
		return null;
	}
	
////===============================================================================================
//// PersistentObject methods.
////===============================================================================================

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteId()
	 */
	@Override
	public long getRemoteId() {
		return mDrinkId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getRemoteColumnName()
	 */
	@Override
	protected String getRemoteColumnName() {
		return DrinksFields.DRINK_ID.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getTableName()
	 */
	@Override
	public String getTableName() {
		return Tables.DRINKS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getContentValues()
	 */
	@Override
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(DrinksFields.DRINK_ID.toString(), mDrinkId + "");
		values.put(DrinksFields.NAME.toString(), mName);
		values.put(DrinksFields.INGREDIENTS.toString(), encodeIngredients());
		return values;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#getObjectMapping()
	 */
	@Override
	public List<NameValuePair> getObjectMapping() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("drinkid", "" + mDrinkId));
		pairs.add(new BasicNameValuePair("drinkname", "" + mName));
		
		return pairs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.models.PersistentObject#fillFromCursor(android.content.Context, android.database.Cursor)
	 */
	@Override
	protected boolean fillFromCursor(Context context, Cursor c) {
		if (c == null) return false;		
		mDrinkId = c.getInt(DrinksFields.DRINK_ID.ordinal());
		mName = c.getString(DrinksFields.NAME.ordinal());
		mIngredients = decodeIngredients(c.getString(DrinksFields.INGREDIENTS.ordinal()));
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
			mDrinkId = json.getLong("drinkid");
			mName = json.getString("drinkname");
			mIngredients = decodeIngredients(json.getString("ingredients"));
			
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
////===============================================================================================
//// Getters
////===============================================================================================

	/**
	 * Find the User with the given 'userId'.
	 * @param context
	 * @param facebookId
	 * @return
	 */
	public static Drink findWithDrinkId(Context context, long drinkId) {
		if (drinkId <= 0) return null;
		
    	Drink drink = new Drink();
    	boolean created = drink.fillWhere(context, DrinksFields.DRINK_ID.toString() + "=?", "" + drinkId);
   	
    	return (created) ? drink : null;
	}
	
	public static List<Drink> getAllDrinks(Context context) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		if (db == null) return null;
		
		String where = Tables.DRINKS;
		String orderBy = DrinksFields.NAME + " ASC";
		Cursor c = U.initCursor(db.query(where, null, null, null, null, null, orderBy));
		if (c == null) return null;
		
		List<Drink> drinks = new ArrayList<Drink>();
		do {
			Drink drink = new Drink();
			drink.fillFromCursor(context, c);
			drinks.add(drink);
		} while (c.moveToNext());
		c.close();
		
		return drinks;
	}
}










