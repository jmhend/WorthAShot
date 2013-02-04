/**
 * File:    HttpEngine.java
 * Created: Jan 17, 2012
 * Author:  Viacheslav Panasenko
 */
package com.example.worth_a_shot.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.example.worth_a_shot.http.ApiRequest.FileBodyPair;

/**
 * HttpEngine
 * This is a singleton class representing set of functions for communicating
 * with the back-end server. 
 * Implements GET, POST, DELETE features for all the application requests.
 */
public class HttpEngine {

    private static final String TAG = HttpEngine.class.getSimpleName();
    
////==================================================================================
//// Static constants.
////==================================================================================
    
    private static final String AVATAR_FILE_TOKEN = "avatar_file";
    
    private static final String AGENT_TITLE = "UpTo Android ";
    private static final String APP_VERSION_KEY = "appVersion";
    private static final String APP_OS_KEY = "appOS";
    private static final String APP_OS_ANDROID = "android";
    private static final String AUTH_TOKEN_KEY = "token";
    
    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 443;
    private static final int TIMEOUT = 30000;
    
////==================================================================================
//// Member variables.
////==================================================================================

    private static HttpEngine sInstance;
    
    private Context mContext;
    
    private HttpClient mHttpClient;
    private BasicResponseHandler mResponseHandler;
    
    private String mAuthToken;

////==================================================================================
//// Constructors/initializers.
////==================================================================================    

    /**
     * Default constructor.
     * Construct the HttpEngine for performing server requests.
     */
    public HttpEngine(Context context) {

    	mContext = context.getApplicationContext();
    	
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        // Determine the release version from packaged version info
        String userAgent = AGENT_TITLE;
        try {
            userAgent += mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName
                    + " (" + Build.MODEL 
                    + ", Android " 
                    + Build.VERSION.RELEASE 
                    + ")";           
        } catch (Exception e) {
            userAgent = "Android Unknown";
        }
        
        HttpProtocolParams.setUserAgent(params, userAgent);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), HTTP_PORT));
        
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        ConnManagerParams.setMaxTotalConnections(params, 10);

        mHttpClient = new DefaultHttpClient(cm, params);
        mResponseHandler = new BasicResponseHandler();
    }
    
    /**
     * Returns instance of the class if exists or creates it.
     * @param context Parent context.
     * @return Instance of the class.
     */
    public static HttpEngine getInstance(Context context) {
        if (sInstance == null) sInstance = new HttpEngine(context);
        return sInstance;
    }
////==================================================================================
//// Getters/setters.
////==================================================================================
    
    /**
     * @param authToken The current User's UpTo authorization token.
     */
    public void setAuthToken(String authToken) {
    	mAuthToken = authToken;
    	Log.i(TAG, "Setting AuthToken to: " + authToken);
    }
    
////==================================================================================
//// HTTP methods.
////==================================================================================

    /**
     * Sends a request to server and collects its response.
     * @param request Request callback object that has parameters and handles response.
     * @return Response string.
     * @throws UnsupportedEncodingException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws NotSendingRequestException 
     */
    public String performRequest(ApiRequest request) throws UnsupportedEncodingException, ClientProtocolException, IOException, NotSendingRequestException {
    	
        String requestUrl = request.getRequestUrl();
        RequestType requestType = request.getRequestType();   
        
    	HttpEntity httpEntity = null;
    	List<NameValuePair> requestParams = request.getRequestParameters();
    	
    	// Check if there are any FileBodies attached to this ApiRequest.
    	// If so, add it to the HttpEntity, otherwise only use the requestParams.
    	List<FileBodyPair> pairs = request.getFileBodyPairs();
    	if (pairs != null) {
    		httpEntity = createHttpEntityFromParamsWithFileBody(requestParams, pairs);
    	} else {
    		httpEntity = createHttpEntityFromParams(requestParams);
    	}
    	
    	if (!request.shouldSendRequest()) {
    		Log.w(TAG, "Not sending ApiRequest [" + request.getAction() + "]");
    		throw new NotSendingRequestException();
    	}
        
        // Monitor request execution time and target.
        String serverResponse = "";        
        Log.i(TAG + "Sending HTTP " + requestType + ".", requestUrl);
        long startTime = System.currentTimeMillis();
        
        switch (requestType) {
        case GET:
        	serverResponse = performHttpGet(requestUrl);
        	break;
        case DELETE:
        	serverResponse = performHttpDelete(requestUrl);
        	break;
        case POST:
        	serverResponse = performHttpPost(requestUrl, httpEntity, pairs == null);  
        	break;
        case DELETE_WITH_BODY:
        	serverResponse = performHttpDeleteWithBody(requestUrl, httpEntity);
        	break;
    	default:
    		throw new IllegalArgumentException("Invalid RequestType: " + requestType.toString());
        }
        
        long endTime = System.currentTimeMillis();
        Log.i(TAG, requestType + " took " + (endTime - startTime) + " milliseconds to execute.");
        Log.i(TAG + " " + requestType + " RESPONSE", serverResponse);
        
        return serverResponse;
    }

////==================================================================================
//// GET/POST/DELETE methods.
////================================================================================== 
  
    /**
     * Sends GET request to the given URL.
     * @param requestUrl URL to send GET request.
     * @return String with server response.
     */
    private String performHttpGet(String requestUrl) throws UnsupportedEncodingException, IOException {   
    	HttpGet httpGet = new HttpGet(requestUrl);
    	setHttpHeader(httpGet);
    	
        return mHttpClient.execute(httpGet, mResponseHandler);
    }
    
    /**
     * Sends a POST request to the given URL, along with the supplied postEntity.
     * @param requestUrl URL to send POST request.
     * @param postEntity HttpEntity containing the POST parameters.
     * @return String with server response.
     */
    private String performHttpPost(String requestUrl, HttpEntity postEntity, boolean printPostParams) throws IOException {
    	if (postEntity != null) {
    		if (printPostParams) Log.i(TAG + " POST Parameters", EntityUtils.toString(postEntity));
    	} else {
    		Log.i(TAG + " POST Parameters", "NULL");
    	}

    	HttpPost httpPost = new HttpPost(requestUrl);
    	httpPost.setEntity(postEntity);
    	setHttpHeader(httpPost);
    	
    	
    	return mHttpClient.execute(httpPost, mResponseHandler);
    }
    
    /**
     * Sends DELETE request to URL.
     * @param requestUrl The URL to send DELETE request to.
     * @return String with server response.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private String performHttpDelete(String requestUrl) throws UnsupportedEncodingException, IOException {
    	HttpDelete httpDelete = new HttpDelete(requestUrl);
    	setHttpHeader(httpDelete);
    	
        return mHttpClient.execute(httpDelete, mResponseHandler);
    }
    
    
    /**
     * Send DELETE request to URL, with attached entity.
     * @param requestUrl The URL to send DELETE request to.
     * @param deleteEntity The http body to attach with this request
     * @throws ParseException
     * @throws IOException
     */
    private String performHttpDeleteWithBody(String requestUrl, HttpEntity deleteEntity) throws ParseException, IOException {
    	if (deleteEntity != null) Log.i(TAG + " DELETE Parameters", EntityUtils.toString(deleteEntity));
    	else Log.i(TAG + " DELETE Parameters", "NULL");

    	HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(requestUrl);
    	httpDelete.setEntity(deleteEntity);
    	setHttpHeader(httpDelete);
    	
    	return mHttpClient.execute(httpDelete, mResponseHandler);
    }
    
////==================================================================================
//// Utility methods.
////================================================================================== 
    
    /**
     * Sets the Http Header for the given request.
     * @param httpRequest
     */
    private void setHttpHeader(HttpRequestBase httpRequest) {
    	httpRequest.addHeader("fbid", "" + 69);
    	httpRequest.addHeader("auth", "yourMomIsAnAuth");
    }
    
    
    /**
     * Creates an HttpEntity suitable for a POST/DELETE request from NameValuePairs.
     * @throws UnsupportedEncodingException 
     */
    private static HttpEntity createHttpEntityFromParams(List<NameValuePair> params) throws UnsupportedEncodingException {
    	if (params == null) return null;
    	return new UrlEncodedFormEntity(params);
    }
    
    /**
     * Creates an HttpEntity suitable for a POST/DELETE request from NameValuePairs and a FileBody.
     */
    private static HttpEntity createHttpEntityFromParamsWithFileBody(List<NameValuePair> params, List<FileBodyPair> pairs) throws UnsupportedEncodingException {
    	MultipartEntity multipartEntity = new MultipartEntity();
    	
    	// Add the contents of the FileBody to the HttpEntity.
    	if (pairs != null) {
    		for (FileBodyPair pair : pairs) {
    			try {
    				File file = new File(pair.getPath());
    				FileBody fileBody = new FileBody(file);
    				
    				if (fileBody != null) {
    					multipartEntity.addPart(pair.getPostParam(), fileBody);
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
    	// Add the other POST parameters to the HttpEntity.
    	if (params != null) {
    		for (NameValuePair pair : params) {
    			String pairString = pair.getValue();
    			if (pairString != null) {
    				multipartEntity.addPart(pair.getName(), new StringBody(pairString));
    			}
    		}
    	}
    	
    	return multipartEntity;
    }
    
    /**
     * Checks if there is the Internet connection available.
     * @param context Parent context.
     * @return True if there is a connection available, false otherwise.
     */
    public static boolean isInternetConnectionAvailable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()
                || (activeNetworkInfo.getState() != NetworkInfo.State.CONNECTED)) {
            return false;
        }
        return true;
    }
    
////==================================================================================
//// HttpCodes
////================================================================================== 
    
    /**
     * HttpCodes
     * Common HTTP codes.
     */
    public static class HttpCodes {
    	private HttpCodes() { }
        
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int TIMEOUT = 408;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
