package com.codepath.apps.twitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "HmquyVRMSB1WsKcMOLEqDHJuO";       // Change this
	public static final String REST_CONSUMER_SECRET = "6zwNGMzaLhvkwyv75ZhR4l7ZK6Nc2qxwh96oNFwhh4vTsoKHly"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getLatestTweets(AsyncHttpResponseHandler handler, Long l) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		if( l != null){
			params.put("since_id", Long.toString(l));
			client.get(apiUrl, params, handler);
		}
	}
	
	public void getListTweets(AsyncHttpResponseHandler handler, Long l, Long id) {
		String apiUrl = getApiUrl("lists/statuses.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("slug", "statuses");
		params.put("owner_id", id.toString());
		if(l>0){
			params.put("max_id", Long.toString(l));
			client.get(apiUrl, params, handler);
		}else{
			client.get(apiUrl, params, handler);
		}
	}
	public void getMentionsTimeline(AsyncHttpResponseHandler handler, Long l){
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();

		
			if(l>0){
				params.put("max_id", Long.toString(l));
				client.get(apiUrl, params, handler);
			}else{
				client.get(apiUrl, null, handler);
			}
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(AsyncHttpResponseHandler handler, Long l) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();

		
			if(l>0){
				params.put("max_id", Long.toString(l));
				client.get(apiUrl, params, handler);
			}else{
				//params.put("count", "25");
				client.get(apiUrl, null, handler);
			}
	}
	
	public void getFollowersList(AsyncHttpResponseHandler handler, String userName) {
		String apiUrl = getApiUrl("followers/list.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("screen_name", userName);
		
		client.get(apiUrl, params, handler);
			
	}	
	public void getUserTimeline(AsyncHttpResponseHandler handler , Long id, Long l){
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id",id.toString() );

		
		if(l>0){
			params.put("max_id", Long.toString(l));
			client.get(apiUrl, params, handler);
		}else{
			//params.put("count", "25");
			client.get(apiUrl, params, handler);
		}
	}
	public void getMyInfo(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}
	
	public void sendTweet(AsyncHttpResponseHandler handler, String status, Long statusId){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",status );
		if(statusId != 0){
			params.put("in_reply_to_status_id", statusId.toString());
		}
		 client.post(apiUrl, params, handler);
	}
	public void getTweetStatus(AsyncHttpResponseHandler handler, Long l) {
		String apiUrl = getApiUrl("statuses/show.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		if(l>=0){
			params.put("id", Long.toString(l));
			//params.put("include_my_retweet", "true");
			client.get(apiUrl, params, handler);
		}
		
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}