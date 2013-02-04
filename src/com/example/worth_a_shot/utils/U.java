/**
 * File:    U.java
 * Created: Feb 16, 2012
 * Author:	Viacheslav Panasenko
 */
package com.example.worth_a_shot.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.DisplayMetrics;

/**
 * U
 * Utilities: Contains commonly used functions.
 */
public class U {
	
	/**
	 * @param str String to test validity of.
	 * @return True if the String is valid as per the return statement, false otherwise.
	 * Valid:
	 * 1. Not null.
	 * 2. length() > 0;
	 * 3. Not "null".
	 */
	public static boolean strValid(String str) {
		return strNotEmpty(str) && !str.equals("null") && !str.equals("(null)");
	}
	
	/**
	 * @param str String to test emptiness of
	 * @return True if the String is valid as per the return statement, false otherwise.
	 * Not empty:
	 * 1. Not null.
	 * 2. str.length() > 0
	 */
	public static boolean strNotEmpty(String str) {
		return (str != null) && (str.length() > 0);
	}
	
	/**
	 * Converts DP measurements to pixels.
	 */
    public static float dpToPixels(float dp, Context context) {
    	Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
    
    /**
     * Converts pixel measurements to DP.
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }
    
	/**
	 * @param c The Cursor to check for validity.
	 * @return A Cursor movedToFirst, or null if it's an invalid Cursor.
	 */
	public static Cursor initCursor(Cursor c) {
		if (c == null) return null;
		if (!c.moveToFirst()) {
			c.close();
			return null;
		}
		
		return c;
	}
	
    /**
     * Returns progress dialog object.
     * @param context Parent context.
     * @param titleResourceId Resource id for title text.
     * @param messageResourceId Resource id for message text.
     * @return Progress dialog object.
     */
    public static ProgressDialog showWaitingDialog(Context context, int titleResourceId, int messageResourceId, boolean isCancelable) {
        String title = titleResourceId > 0 ? context.getString(titleResourceId) : ""; 
        String message = messageResourceId > 0 ? context.getString(messageResourceId) : ""; 
        return ProgressDialog.show(context, title, message, false, isCancelable);
    }
    
    public static ProgressDialog showWaitingDialog(Context context, String message) {
        String title = "";
        if (message == null) message = "";
        return ProgressDialog.show(context, title, message, false, false);
    }
    
    public static ProgressDialog showWaitingDialog(Context context, int titleResourceId, int messageResourceId) {
    	return showWaitingDialog(context, titleResourceId, messageResourceId, false);
    }
    
    public static ProgressDialog showNonModalWaitingDialog(Context context, int titleResourceId, int messageResourceId) {
    	return showWaitingDialog(context, titleResourceId, messageResourceId, true);
    }
}