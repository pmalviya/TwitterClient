package com.codepath.apps.twitter.fragments;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.codepath.apps.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.DetailedTweetActivity;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetsListFragment {

	@Override
	public void populateTimeline(long l){
		if(!isNetworkAvailable()){
			
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
			
			aTweets.addAll(Tweet.getAll());
			aTweets.notifyDataSetChanged();
			return;
		}
		//Tweet.deleteAll();
		client.getHomeTimeline(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				//Tweet.deleteAll();
				aTweets.addAll(Tweet.fromJSONArray(arg1));
				aTweets.notifyDataSetChanged();
				ActiveAndroid.beginTransaction();
				try{
					for(int i=0;i<tweets.size();i++){
						Tweet.insertTweet(tweets.get(i));
					}
					ActiveAndroid.setTransactionSuccessful();
				}finally{
					ActiveAndroid.endTransaction();
				}
					//Tweet firstTweet = Tweet.load(Tweet.class, 1);

			}

		}, l);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		populateTimeline(0);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		return v;
	}

}