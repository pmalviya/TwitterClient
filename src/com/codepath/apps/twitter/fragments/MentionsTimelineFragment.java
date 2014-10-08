package com.codepath.apps.twitter.fragments;
import org.json.JSONArray;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.Log;
import com.codepath.apps.listeners.EndlessScrollListener;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;


public class MentionsTimelineFragment extends TweetsListFragment {

	public void populateTimeline(long l){
		if(!isNetworkAvailable()){
			
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
			aTweets.addAll(Tweet.getAll());
			aTweets.notifyDataSetChanged();
			return;
		}
		client.getMentionsTimeline(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				
				aTweets.addAll(Tweet.fromJSONArray(arg1));
				aTweets.notifyDataSetChanged();
//				ActiveAndroid.beginTransaction();
//				try{
//					for(int i=0;i<tweets.size();i++){
//						tweets.get(i).save();
//					}
//					ActiveAndroid.setTransactionSuccessful();
//				}finally{
//					ActiveAndroid.endTransaction();
//				}
//					//Tweet firstTweet = Tweet.load(Tweet.class, 1);

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
