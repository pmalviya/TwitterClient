package com.codepath.apps.twitter;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.util.Log;
import com.codepath.apps.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.twitter.adapters.TweetArrayAdapter;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;



import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TimeLineActivity extends FragmentActivity {
	private TwitterClient  client;
	private LinkedList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	private User myself; 
	private SwipeRefreshLayout swipeContainer;
	
	/**
	 * This method checks presence of internet connectivity
	 * @return network status
	 */
	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	// Look at the last tweet uid and pull older tweets
	public void customLoadMoreDataFromApi() {
		populateTimeline(tweets.get(tweets.size()-1).getUid());

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_line);
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 64, 153, 255)));
		client = TwitterApp.getRestClient();

		setupViews();
		tweets = new LinkedList<Tweet>();
		aTweets =  new TweetArrayAdapter(this, tweets);
		aTweets.clear();
		// Fetch all the tweets 
		populateTimeline(0);

		lvTweets.setAdapter(aTweets);

		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				customLoadMoreDataFromApi(); 
				// or customLoadMoreDataFromApi(totalItemsCount); 
			}


		});
		// Setup refresh listener which triggers new data loading
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list here.
				// Make sure you call swipeContainer.setRefreshing(false)
				// once the network request has completed successfully.
				fetchTimelineAsync(tweets.get(0).getUid());
			}});

		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);
		getMyInfo();
	}
	public void fetchTimelineAsync(long page) {
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		client.getLatestTweets(new JsonHttpResponseHandler() {
			public void onSuccess(JSONArray json) {
				// ...the data has come back, finish populating listview...
				
				// Now we call setRefreshing(false) to signal refresh has finished
				swipeContainer.setRefreshing(false);
			}

			public void onFailure(Throwable e) {
				Log.d("DEBUG", "Fetch timeline error: " + e.toString());
			}
		}, page);
	}
	public void setupViews(){
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(TimeLineActivity.this, DetailedTweetActivity.class);
				Tweet tweet = tweets.get(position);
				i.putExtra("tweet", tweet);
				startActivity(i);
			}
		});
	}


	public void getMyInfo(){
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		client.getMyInfo(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
				Toast.makeText(TimeLineActivity.this, "fialed", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(JSONObject arg1) {
				myself =  User.fromJSON(arg1);
				Toast.makeText(TimeLineActivity.this, "success", Toast.LENGTH_LONG).show();
			}

		});
	}
	public void onComposeClick(MenuItem mi ){
		FragmentManager fm = getFragmentManager();

		ComposeDialog composeDialog = ComposeDialog.newInstance(myself);
		composeDialog.show(fm, "fragment_compose");
		composeDialog.setDialogListener(new ComposeDialogListener() {

			@Override
			public void onDialogDone(Tweet tweet) {
				getLatestTweets(tweets.get(0).getUid());
				tweets.addFirst(tweet);
				aTweets.notifyDataSetChanged();
			}
		}); 
	}
	public void getLatestTweets(long l){
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		client.getLatestTweets(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				tweets.addAll(0, Tweet.fromJSONArray(arg1));
				//aTweets.addAll(Tweet.fromJSONArray(arg1));
				aTweets.notifyDataSetChanged();
				//					for(int i=0;i<tweets.size();i++){
				//						tweets.get(i).save();
				//					}
				//	Tweet firstTweet = Tweet.load(Tweet.class, 1);

			}

		}, l);
	}
	public void populateTimeline(long l){
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		client.getHomeTimeline(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				
				aTweets.addAll(Tweet.fromJSONArray(arg1));
				aTweets.notifyDataSetChanged();
				//					for(int i=0;i<tweets.size();i++){
				//						tweets.get(i).save();
				//					}
				//	Tweet firstTweet = Tweet.load(Tweet.class, 1);

			}

		}, l);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
