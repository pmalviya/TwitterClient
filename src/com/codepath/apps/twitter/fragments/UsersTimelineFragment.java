package com.codepath.apps.twitter.fragments;

import org.json.JSONArray;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.codepath.apps.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class UsersTimelineFragment extends TweetsListFragment {
	private Long id;
	public void customLoadMoreDataFromApi() {
		populateTimeline(id, tweets.get(tweets.size()-1).getUid()-1);

	}
	public void populateTimeline( Long id, Long l){
		if(!isNetworkAvailable()){
			
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
			aTweets.addAll(Tweet.getAll());
			aTweets.notifyDataSetChanged();
			return;
		}
		client.getUserTimeline(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				
				aTweets.addAll(Tweet.fromJSONArray(arg1));
				aTweets.notifyDataSetChanged();
				ActiveAndroid.beginTransaction();
				try{
					for(int i=0;i<tweets.size();i++){
						tweets.get(i).save();
					}
					ActiveAndroid.setTransactionSuccessful();
				}finally{
					ActiveAndroid.endTransaction();
				}
					//Tweet firstTweet = Tweet.load(Tweet.class, 1);

			}

		}, id, l);
	}
	
	public UsersTimelineFragment(Long id){
		this.id = id;
	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		populateTimeline(this.id, (long) 0);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
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
				fetchTimelineAsync(tweets.get(0).getUid());
			}});

		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);
		return v;
	}
}
