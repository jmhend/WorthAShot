/**
 * File:    PersistentObject.java
 * Created: Nov 16, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.models;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.worth_a_shot.sqlite.DatabaseHelper;
import com.example.worth_a_shot.utils.U;

/**
 * PersistentObject
 * Abstract class for all models to implement.
 */
public abstract class PersistentObject {
	private static final String TAG = PersistentObject.class.getSimpleName();
	
////=================================================================================================
//// Member variables.
////=================================================================================================
	
	/**
	 * @return The unique, locally-assigned ID should represent this model.
	 * Represents the row_id of this model's location in SQLite.
	 * Controlled by PersistentObject.
	 */
	private String mLocalId;
	
////=================================================================================================
//// Abstract methods.
////=================================================================================================
	
	/**
	 * @return The unique, server-assigned ID should represent this model.
	 * (viz. event_id, user_id, group_id, etc.)
	 */
	public abstract long getRemoteId();
	
	/**
	 * @return The SQLite table column name that corresponds to the 'getRemoteId()'
	 * (viz. EventFields.EVENT_ID, UserFields.USER_ID, GroupFields.GROUP_ID, etc.)
	 */
	protected abstract String getRemoteColumnName();
	
	/**
	 * @return The table name in SQLite that this model is saved to.
	 */
	public abstract String getTableName();
	
	/**
	 * @return The ContentValues to INSERT/UPDATE with in SQLite.
	 */
	public abstract ContentValues getContentValues();
	
	/**
	 * @return A List<NameValuePairs> that represents this model in server requests.
	 */
	public abstract List<NameValuePair> getObjectMapping();
	
	/**
	 * Saves this model into SQLite.
	 * Subclasses determine when it's appropriate to INSERT or UPDATE.
	 * @param context
	 * @return Success of the saving.
	 */
	public boolean save(Context context) {
		return (updateWithRemoteId(context)) ? true : insert(context);
	}
	
	/**
	 * DELETEs this model from SQLite.
	 * @return Success of the DELETE.
	 */
	public boolean delete(Context context) {
		return deleteWithRemoteId(context);
	}
	
	/**
	 * Fetches only this model's data from SQLite and sets it's member variables accordingly.
	 * @return Success of the fetching and variable setting.
	 */
	public boolean fill(Context context) {
		return fillWithRemoteId(context);
	}
	
	/**
	 * Uses the given Cursor to set this model's member variables.
	 * @param context TODO
	 * @param c A Cursor pointing to a valid SQLite row in this model's table.
	 * @return Success of the variable setting
	 */
	protected abstract boolean fillFromCursor(Context context, Cursor c);
	
	/**
	 * Maps the given JSONObject to this model's member variables.
	 * @param json The JSONObject of the same model type as this model.
	 * @return Success of a the variable mapping.
	 */
	public abstract boolean fillFromJson(Context context, JSONObject json);
	
////=================================================================================================
//// Utility
////=================================================================================================
	
	/**
	 * @return A String representation of this model's Class.
	 * (viz. "Event", "User", "Group", etc.)
	 */
	protected String getTypeAsString() {
		return this.getClass().getSimpleName();
	}

////=================================================================================================
//// SQLite INSERT'ing.
////=================================================================================================
	
	/**
	 * INSERT's this model into SQLite, and sets mLocalId with the resulting row_id.
	 * @return Success of the INSERT.
	 */
	protected final boolean insert(Context context) {
		long rowId = performInsertIntoSQLite(context);		
		mLocalId = Long.toString(rowId);
		
		return rowId >= 0;
	}
	
	/**
	 * Actually INSERT's this model into SQLite.
	 * @return The row_id where INSERT'd.
	 */
	private final long performInsertIntoSQLite(Context context) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
    	if (db == null) return -1;
    	
		return db.insert(getTableName(), getContentValues());
	}
	
////=================================================================================================
//// SQLite UPDATING'ing.
////=================================================================================================	
	
	/**
	 * UPDATE's this model in SQLite using the remote_id.
	 * @return Success of the UPDATE.
	 */
	protected final boolean updateWithRemoteId(Context context) { 
		return performUpdateInSQLite(context, getRemoteColumnName() + "=?", Long.toString(getRemoteId()));
	}
	
	/**
	 * UPDATE's this model in SQLite using the 'where' clause and 'whereArgs' arguments.
	 * @return Success of the UPDATE.
	 */
	protected final boolean updateWhere(Context context, String where, String ...whereArgs) {
		return performUpdateInSQLite(context, where, whereArgs);
	}
	
	/**
	 * Actually UPDATE's this model in SQLite.
	 * @param whereColumn
	 * @param whereValue
	 * @return Success of the UPDATE.
	 */
	private final boolean performUpdateInSQLite(Context context, String where, String ... whereArgs) {		
		DatabaseHelper db = DatabaseHelper.getInstance(context);
    	if (db == null) return false;
		
		int rowsUpdated = db.update(
				getTableName(), 
				getContentValues(), 
				where, 
				whereArgs);
		
		return rowsUpdated >= 1;
	}
	
////=================================================================================================
//// SQLite DELETE'ing.
////=================================================================================================
	
	/**
	 * DELETE's this model from SQLite using the remote_id.
	 * @return Success of the DELETE.
	 */
	protected final boolean deleteWithRemoteId(Context context) {
		return performDeleteFromSQLite(context, getRemoteColumnName() + "=?", Long.toString(getRemoteId()));
	}
	
	/**
	 * DELETE's this model from SQLite using there 'where' clause and 'whereArgs' arguments.
	 * @return Success of the DELETE.
	 */
	protected final boolean deleteWhere(Context context, String where, String ... whereArgs) {
		return performDeleteFromSQLite(context, where, whereArgs);
	}
	
	/**
	 * Actually DELETE's this model from SQLite.
	 * @param where
	 * @param whereArgs
	 * @return Success of the DELETE.
	 */
	private final boolean performDeleteFromSQLite(Context context, String where, String ... whereArgs) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
    	if (db == null) return false;
		
		return db.delete(
				getTableName(), 
				where, 
				whereArgs) > 0;
	}
	
////=================================================================================================
//// SQLite fetching.
////=================================================================================================
	
	/**
	 * Fetches this model's data from SQLite using the remote_id and sets it to it's member variables.
	 * @return Success of the filling.
	 */
	protected final boolean fillWithRemoteId(Context context) {	
		return performFillFromSQLite(context, getRemoteColumnName() + "=?", Long.toString(getRemoteId())); 
	}

	/**
	 * Fetches this model's data from SQLite using the 'where' clause and 'whereArgs' arguments, and sets it to it's member variables.
	 * @return Success of the filling.
	 */
	protected final boolean fillWhere(Context context, String where, String ... whereArgs) {
		return performFillFromSQLite(context, where, whereArgs);
	}
	
	/**
	 * Actually fetches this model's data from SQLite and sets it to it's member variables.
	 * @param whereColumn
	 * @param whereValue
	 * @return Success of the filling.
	 */
	private final boolean performFillFromSQLite(Context context, String where, String ... whereArgs) {
		Cursor c = getObjectCursorFromSQLite(context, where, whereArgs);
		if (c == null) {
			return false;
		}
		
		try {
			fillFromCursor(context, c);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (c != null) c.close();
		}
		
		return true;
	}
	
////=================================================================================================
//// Utility functions.
////=================================================================================================
	
	/**
	 * Gets a Cursor from SQLite using the provided 'where' clause and 'whereArgs' arguments.
	 * @param whereColumn
	 * @param whereValue
	 * @return The fetched Cursor, or NULL if the fields can't be matched.
	 */
	private final Cursor getObjectCursorFromSQLite(Context context, String where, String[] whereArgs) {
		DatabaseHelper db = DatabaseHelper.getInstance(context);
    	if (db == null) return null;
    	
		return U.initCursor(db.query(
				getTableName(), 
				null, 
				where, 
				whereArgs,
				null, 
				null, 
				null));
	}	
	
	/**
	 * Prints this model.
	 */
	public void print() {
		Log.i(getTypeAsString(), toString());
	}
	
	/**
	 * Prints this model in *RED*.
	 */
	public void printAsError(String errorMessage) {
		Log.e(TAG, (errorMessage != null)? errorMessage : "");
		Log.e(getTypeAsString(), toString());
	}
}
