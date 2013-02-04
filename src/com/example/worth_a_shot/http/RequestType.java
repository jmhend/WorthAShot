/**
 * File:    RequestType.java
 * Created: Nov 13, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.http;

/**
 * RequestType
 * Describes which type HTTP request type the given ApiRequest should use.
 */
public enum RequestType {
	GET,
	POST,
	DELETE,
	DELETE_WITH_BODY;
	
	public static RequestType ofValue(int value) {
		switch(value) {
		case 0: return GET;
		case 1: return POST;
		case 2: return DELETE;
		case 3: return DELETE_WITH_BODY;
		default:
			throw new IllegalArgumentException("No RequestType for value " + value);
		}
	}
}