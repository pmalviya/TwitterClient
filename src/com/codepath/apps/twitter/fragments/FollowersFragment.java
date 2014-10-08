package com.codepath.apps.twitter.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.codepath.apps.twitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class FollowersFragment extends UsersListFragment {

	@Override
	public void populateUsers(String screenName){
		if(!isNetworkAvailable()){
			
			Toast.makeText(getActivity(), "No Internet Available", Toast.LENGTH_SHORT).show();
			
			return;
		}
		client.getFollowersList(new JsonHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e, JSONObject s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				// TODO Auto-generated method stub
				try {
					aUsers.addAll(User.fromJSONArray(arg1.getJSONArray("users")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				aUsers.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}

			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				
				aUsers.addAll(User.fromJSONArray(arg1));
				aUsers.notifyDataSetChanged();

			}
			

		}, screenName);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		screenName = getArguments().getString("screenName", "");	
		populateUsers(screenName);

	}
	public static FollowersFragment newInstance(String screenName) {
		FollowersFragment fragmentDemo = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		return v;
	}

}