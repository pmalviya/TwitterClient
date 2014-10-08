package com.codepath.apps.twitter.fragments;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.codepath.apps.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.ComposeDialog;
import com.codepath.apps.twitter.DetailedTweetActivity;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TimeLineActivity;
import com.codepath.apps.twitter.TwitterApp;
import com.codepath.apps.twitter.TwitterClient;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TweetsListFragment extends Fragment {
	protected LinkedList<Tweet> tweets;
	protected ArrayAdapter<Tweet> aTweets;
	protected ListView lvTweets;
	protected View v;
	protected TwitterClient  client;
	protected SwipeRefreshLayout swipeContainer;
	protected ImageButton replyButton;
	
	 // Define the listener of the interface type
	  // listener is the activity itself
	private OnItemSelectedListener listener;
	// Look at the last tweet uid and pull older tweets

	  // Define the events that the fragment will use to communicate
	  public interface OnItemSelectedListener {
		  public void onTweetSelected(Tweet tweet);
	  }
	  
	  
	// Store the listener (activity) that will have events fired once the fragment is attached
	  @Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	      if (activity instanceof OnItemSelectedListener) {
	        listener = (OnItemSelectedListener) activity;
	      } else {
	        throw new ClassCastException(activity.toString()
	            + " must implement TweetsListFragment.OnItemSelectedListener");
	      }
	  }
	/**
	 * This method checks presence of internet connectivity
	 * @return network status
	 */
	protected Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	

	public void fetchTimelineAsync(long page) {
		if(!isNetworkAvailable()){
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
			swipeContainer.setRefreshing(false);
			return;
		}
		client.getLatestTweets(new JsonHttpResponseHandler() {
			public void onSuccess(JSONArray json) {
				// ...the data has come back, finish populating listview...
				
				// Now we call setRefreshing(false) to signal refresh has finished
				swipeContainer.setRefreshing(false);
			}

			public void onFailure(Throwable e) {
				swipeContainer.setRefreshing(false);
				Log.d("DEBUG", "Fetch timeline error: " + e.toString());
			}
		}, page);
	}
	public void getLatestTweets(long l){
		if(!isNetworkAvailable()){
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
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


	@Override
	public void onCreate(Bundle savedInstanceState) {
		client = TwitterApp.getRestClient();
		super.onCreate(savedInstanceState);
		
		
		tweets = new LinkedList<Tweet>();
		tweets.clear();
		aTweets =  new TweetArrayAdapter(getActivity(), tweets);
		aTweets.clear();

	}
	public void customLoadMoreDataFromApi() {
		populateTimeline(tweets.get(tweets.size()-1).getUid()-1 );

	}
	public void populateTimeline(long l){
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		lvTweets = (ListView) v.findViewById(R.id.lvTweets);
		
		lvTweets.setAdapter(aTweets);
		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listener.onTweetSelected(tweets.get(position));
				
			}
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				customLoadMoreDataFromApi(); 
				// or customLoadMoreDataFromApi(totalItemsCount); 
			}
		
			
		});
		

		swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
		// Setup refresh listener which triggers new data loading
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list here.
				// Make sure you call swipeContainer.setRefreshing(false)
				// once the network request has completed successfully.
				if(tweets.size()>0){
					fetchTimelineAsync(tweets.get(0).getUid());
				}else{
				swipeContainer.setRefreshing(false);
				}
			}});

		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);

		
		return v;
	}

	public void add(Tweet tweet) {
        tweets.addFirst(tweet);
		aTweets.notifyDataSetChanged();
	}


	

}
