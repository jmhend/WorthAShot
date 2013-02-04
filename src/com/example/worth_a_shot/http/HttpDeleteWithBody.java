/**
 * File:    HttpDeleteWithBody.java
 * Created: September 5, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * HttpDeleteWithBody
 * HttpDelete, but allows a body to be attached via HttpDelete#setEntity() 
 * Like a POST.
 */
class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
	
	private static final String TAG = HttpDeleteWithBody.class.getSimpleName();
	
////===============================================================================
//// Static constants.
////===============================================================================
	
    public static final String METHOD_NAME = "DELETE";
    
////===============================================================================
//// Constructors.
////===============================================================================

    /**
     * Default constructor.
     */
    public HttpDeleteWithBody() { 
    	super(); 
	}
    
    /**
     * Constructor with target path.
     */
    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }
    
    /**
     * Constructor with target path.
     */
    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }    
    
////===============================================================================
//// HTTP methods.
////===============================================================================
    
    /*
     * @see org.apache.http.client.methods.HttpRequestBase#getMethod()
     */
    public String getMethod() { 
    	return METHOD_NAME; 
	}
    
}