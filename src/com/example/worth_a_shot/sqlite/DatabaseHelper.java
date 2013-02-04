/**
 * File:    DatabaseHelper.java
 * Created: July 16, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.worth_a_shot.WorthAShot;
import com.example.worth_a_shot.models.User;
import com.example.worth_a_shot.models.Order.OrderStatus;
import com.example.worth_a_shot.utils.U;

/**
 * DatabaseHelper
 * Provides access for the UpTo application to SQLite. Includes database create/upgrade statements, table
 * descriptions, standard CRUD (Create, Read, Update, Delete) methods, and utility methods.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    
////======================================================================================================
//// Static constants.
////======================================================================================================
    
    private static final int DATABASE_VERSION = 1;
  
////======================================================================================================
//// Member variables.
////======================================================================================================
    
    private static DatabaseHelper sInstance;
    
////======================================================================================================
//// Constructors/Initializers/Getters.
////======================================================================================================
    
    /**
     * Constructor.
     * Creates a new SQLite database.
     * @param context
     * @param dbName The userId of the current User, used to name her distinct database.
     */
    private DatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }
    
    /**
     * Returns a single connection to the SQLite database that is used throughout the application.
     * CAN return null, so needs to be checked for.
     * @param context
     * @return
     */
    public static DatabaseHelper getInstance(Context context) {
    	if (sInstance == null) {
    		User user = WorthAShot.get(context).getUser();
    		if (user == null) return null;
    		
			sInstance = new DatabaseHelper(context.getApplicationContext(), "" + user.getFacebookId());
    	}   	
    	return sInstance;
    }
    
////======================================================================================================
//// Transaction methods.
////======================================================================================================
    
    /**
     * Marks the beginning of a SQLite transaction.
     * HINT: Use SQLite transactions to speed up large database accesses, 
     * and to help ensure data integrity.
     */
    public void beginTransaction() {
    	getWritableDatabase().beginTransaction();
    }
    
    /**
     * Notifies the SQLite database that the transaction was successful, 
     * and should thus save the changes made.
     */
    public void setTransactionSuccessful() {
    	getWritableDatabase().setTransactionSuccessful();
    }
    
    /**
     * Marks the end of a SQLite transaction. ALWAYS call endTransaction() if 
     * you've called beginTransaction(), EVEN IF the transaction fails/throws.
     */
    public void endTransaction() {
    	getWritableDatabase().endTransaction();
    }
    
////======================================================================================================
//// CRUD methods.
////======================================================================================================
    
    /**
     * Convenience function for querying the database.
     * @param from
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return A Cursor with results from the database query.
     */
    public Cursor query(String from, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
    	return getWritableDatabase().query(from, projection, selection, selectionArgs, groupBy, having, orderBy);
    }
    
    /**
     * Convenience methods for INSERT OR REPLACEing an item into the UpTo database.
     * @param table The tables in which the item is to be INSERT'd.
     * @param values The ContentValues describing which fields to be INSERT'd.
     * @return The row ID where the item was INSERT'd, or -1 if error occurred.
     */
    public long insert(String table, ContentValues values) {
    	return getWritableDatabase().replace(table, null, values);
    }
    
    /**
     * UPDATEs rows from the UpTo database.
     * @param from The Table to UPDATE.
     * @param values The ContentValues that need to be UPDATE'd.
     * @param where The conditions to match that will result in an UPDATE.
     * @param whereArgs The arguments for the 'where' predicate.
     * @return The number of rows UPDATE'd.
     */
    public int update(String from, ContentValues values, String where, String[] whereArgs) {
    	return getWritableDatabase().update(from, values, where, whereArgs);
    }
    
    /**
     * DELETEs rows from the UpTo database.
     * @param from The Table to DELETE from.
     * @param where The conditions to match that will result in a DELETE.
     * @param whereArgs The arguments for the 'where' predicate.
     * @return The number of rows DELETE'd.
     */
    public int delete(String from, String where, String[] whereArgs) {
    	return getWritableDatabase().delete(from, where, whereArgs);
    }
    
////======================================================================================================
//// Database utility methods.
////====================================================================================================== 
    
	 /**
	  * DEBUGGING
	  * Logs the contents of a Cursor object.
	  * @param c The cursor to print.
	  * @param cursorDescription A description of the Cursor.
	  */   
	 public static void printCursor(Cursor c, String cursorDescription) {
	     if (c == null) {
	         Log.e(cursorDescription, "Null cursor for " + cursorDescription);
	         return;
	     }
	
	     if (!c.moveToFirst()) { 
	         Log.e(cursorDescription, "No cursor results for " + cursorDescription);
	         return;
	     }
	     
         do {
             String str = "";
             for (int i = 0; i < c.getColumnCount(); i++) {
                 str += c.getString(i) + " | ";	
             }
             Log.d(cursorDescription, str);
         } while (c.moveToNext());
         
         c.moveToFirst();
	 }
	 
	 /**
	  * DEBUGGING
	  * Prints the entire contents of a database table.
	  * @param tableName The name of the table to print.
	  */
	 public void printTable(String tableName) {
		 Cursor c = U.initCursor(getWritableDatabase().query(tableName, null, null, null, null, null, null));
		 if (c == null) {
			 Log.d(TAG, tableName + " is empty");
			 return;
		 }
		 
		 String tag = "TABLE: " + tableName;
		 
		 String[] colNames = c.getColumnNames();
		 
		 Log.d(tag, "================"+tag+"================");
		 do {
			 for (int i = 0; i < c.getColumnCount(); i++) {
				 Log.d(tag, colNames[i] + ": '" + c.getString(i)+"'");
			 }
			 Log.d(tag, "---------------------------------------------------------------");
		 } while (c.moveToNext());
		 c.close();
	 }
    
////======================================================================================================
//// Table fields and constants.
////======================================================================================================

    
    /**
     * Labels of each table in the UpTo database.
     */
    public interface Tables {
        public static final String USERS = "users";
        public static final String FRIENDS = "friends";
        public static final String DRINKS = "drinks";
        public static final String ORDERS = "orders";
        public static final String FAVORITES = "favorites";
    }
    
    /**
     * QualifiedFields
     * Interface for all SQL table enums to implement to get a qualified (tableName + columnName) String.
     */
    private interface QualifiedFields {
    	public String qual();
    }
 
    public enum UserFields implements QualifiedFields {
    	ID("_id"),
        FB_ID("fb_id"),
        FB_TOKEN("fb_auth"),
        NAME("name"),
        AVATAR_URL("avatar_url"),
        AUTH_TOKEN("token");
  	
    	private final String name;
    	
    	private UserFields(String columnName) { 
    		name = columnName;
    	}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Enum#toString()
    	 */
    	@Override
    	public String toString() {
    		return name;
    	}

		/* (non-Javadoc)
		 * @see com.upto.android.core.sqlite.DatabaseHelper.QualifiedFields#qual(java.lang.String, java.lang.String)
		 */
		@Override
		public String qual() {
			return Tables.USERS + "." + name;
		}	
    }
    
    public enum FriendsFields implements QualifiedFields {
        ID("_id"),
        USER_ID_1("user_id_1"),
        USER_ID_2("user_id_2");
    	
    	private final String name;
    	
    	private FriendsFields(String columnName) { 
    		name = columnName;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Enum#toString()
    	 */
    	@Override
    	public String toString() {
    		return name;
    	}
    	
		/* (non-Javadoc)
		 * @see com.upto.android.core.sqlite.DatabaseHelper.QualifiedFields#qual(java.lang.String, java.lang.String)
		 */
		@Override
		public String qual() {
			return Tables.FRIENDS + "." + name;
		}  	
    }

    public enum DrinksFields implements QualifiedFields {
    	ID("_id"),
    	DRINK_ID("drink_id"),
    	NAME("name"),
    	INGREDIENTS("ingredients");
    	
    	private final String name;
    	
    	private DrinksFields(String columnName) { 
    		name = columnName;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Enum#toString()
    	 */
    	@Override
    	public String toString() {
    		return name;
    	}
    	
		/* (non-Javadoc)
		 * @see com.upto.android.core.sqlite.DatabaseHelper.QualifiedFields#qual(java.lang.String, java.lang.String)
		 */
		@Override
		public String qual() {
			return Tables.DRINKS + "." + name;
		}	
    }
    
    public enum OrdersFields implements QualifiedFields {
    	ID("_id"),
    	ORDER_ID("order_id"),
    	FACEBOOK_ID("facebook_id"),
    	FACEBOOK_RECIPIENT_ID("facebook_recipient_id"),
    	DRINK_ID("drink_id"),
    	ORDER_TIME("order_time"),
    	STATUS("status");
    	
    	private final String name;
    	
    	private OrdersFields(String columnName) { 
    		name = columnName;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Enum#toString()
    	 */
    	@Override
    	public String toString() {
    		return name;
    	}
    	
		/* (non-Javadoc)
		 * @see com.upto.android.core.sqlite.DatabaseHelper.QualifiedFields#qual(java.lang.String, java.lang.String)
		 */
		@Override
		public String qual() {
			return Tables.DRINKS + "." + name;
		}	
    }

    public enum FavoritesFields implements QualifiedFields {
    	ID("_id"),
    	USER_ID("user_id"),
    	DRINK_ID("drink_id");
    	
    	private final String name;
    	
    	private FavoritesFields(String columnName) { 
    		name = columnName;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Enum#toString()
    	 */
    	@Override
    	public String toString() {
    		return name;
    	}
    	
		/* (non-Javadoc)
		 * @see com.upto.android.core.sqlite.DatabaseHelper.QualifiedFields#qual(java.lang.String, java.lang.String)
		 */
		@Override
		public String qual() {
			return Tables.FAVORITES + "." + name;
		}   	
    }
    
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + Tables.USERS + " ("
        + UserFields.ID.name + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + UserFields.FB_ID.name  + " INTEGER NOT NULL DEFAULT 0,"
        + UserFields.FB_TOKEN.name + " TEXT,"
        + UserFields.NAME.name + " TEXT,"
        + UserFields.AVATAR_URL.name + " TEXT,"
        + UserFields.AUTH_TOKEN.name + " TEXT,"       
        + "UNIQUE (" + UserFields.FB_ID.name + ") ON CONFLICT IGNORE)";

    private static final String CREATE_FRIENDS_TABLE = "CREATE TABLE " + Tables.FRIENDS + "("
		+ FriendsFields.ID.name + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ FriendsFields.USER_ID_1.name + " INTEGER REFERENCES " + Tables.USERS + ","
		+ FriendsFields.USER_ID_2.name + " INTEGER REFERENCES " + Tables.USERS + ","
		+ "UNIQUE (" + FriendsFields.USER_ID_1.name + ", " + FriendsFields.USER_ID_2.name + ") ON CONFLICT IGNORE,"
    	+ "UNIQUE (" + FriendsFields.USER_ID_2.name + ", " + FriendsFields.USER_ID_1.name + ") ON CONFLICT IGNORE)";
    
    private static final String CREATE_DRINKS_TABLE = "CREATE TABLE " + Tables.DRINKS + " ("
        + DrinksFields.ID.name + " INTEGER PRIMARY KEY AUTOINCREMENT," 
        + DrinksFields.DRINK_ID.name + " INTEGER NOT NULL DEFAULT 0,"
        + DrinksFields.NAME.name + " TEXT," 
        + DrinksFields.INGREDIENTS.name + " TEXT,"
        + "UNIQUE (" + DrinksFields.DRINK_ID.name + ") ON CONFLICT REPLACE)";

    private static final String CREATE_ORDERS_TABLE = "CREATE TABLE " + Tables.ORDERS + " ("
        + OrdersFields.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + OrdersFields.ORDER_ID + " INTEGER NOT NULL DEFAULT 0,"
        + OrdersFields.FACEBOOK_ID + " INTEGER NOT NULL DEFAULT 0 REFERENCES " + Tables.USERS + ","
        + OrdersFields.FACEBOOK_RECIPIENT_ID + " INTEGER NOT NULL DEFAULT 0 REFERENCES " + Tables.USERS + ","
        + OrdersFields.DRINK_ID + " INTEGER NOT NULL DEFAULT 0 REFERENCES " + Tables.DRINKS + ","
        + OrdersFields.ORDER_TIME + " INTEGER NOT NULL DEFAULT 0," 
        + OrdersFields.STATUS + " TEXT," 
        + "UNIQUE (" + OrdersFields.ORDER_ID + ") ON CONFLICT REPLACE)";
	
    private static final String CREATE_FAVORITES_TABLE = "CREATE TABLE " + Tables.FAVORITES + " ("
        + FavoritesFields.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + FavoritesFields.USER_ID + " INTEGER NOT NULL DEFAULT 0 REFERENCES " + Tables.USERS + ","
        + FavoritesFields.DRINK_ID + " INTEGER NOT NULL DEFAULT 0 REFERENCES " + Tables.DRINKS  + ","
        + "UNIQUE (" + FavoritesFields.USER_ID + ", " + FavoritesFields.DRINK_ID + ") ON CONFLICT IGNORE)";

    /*
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate() with version: " + DATABASE_VERSION);
        
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_DRINKS_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
        db.execSQL(CREATE_FAVORITES_TABLE);
        
    }
    
    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
    	Log.d(TAG, "onOpen()");
    	super.onOpen(db);
    }

    /*
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOTE: carefully update onUpgrade() when bumping database versions to make
        // sure user data is saved.
        Log.w(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

}
