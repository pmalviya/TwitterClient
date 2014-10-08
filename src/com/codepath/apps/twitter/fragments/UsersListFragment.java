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
import com.codepath.apps.twitter.adapters.UserArrayAdapter;
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

public class UsersListFragment extends Fragment {
	protected LinkedList<User> users;
	protected ArrayAdapter<User> aUsers;
	protected ListView lvUsers;
	protected View v;
	protected TwitterClient  client;
	protected String screenName;
	
	 // Define the listener of the interface type
	  // listener is the activity itself
//	private OnItemSelectedListener listener;
//	// Look at the last tweet uid and pull older tweets
//
//	  // Define the events that the fragment will use to communicate
//	  public interface OnItemSelectedListener {
//		  public void onTweetSelected(Tweet Tweet);
//	  }
	  
	  
	// Store the listener (activity) that will have events fired once the fragment is attached
//	  @Override
//	  public void onAttach(Activity activity) {
//	    super.onAttach(activity);
//	      if (activity instanceof OnItemSelectedListener) {
//	        listener = (OnItemSelectedListener) activity;
//	      } else {
//	        throw new ClassCastException(activity.toString()
//	            + " must implement TweetsListFragment.OnItemSelectedListener");
//	      }
//	  }
	/**
	 * This method checks presence of internet connectivity
	 * @return network status
	 */
	protected Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		client = TwitterApp.getRestClient();
		super.onCreate(savedInstanceState);
		
		
		users = new LinkedList<User>();
		users.clear();
		aUsers =  new UserArrayAdapter(getActivity(), users);
		aUsers.clear();
		

	}

	public void populateUsers(String screenName){
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_users_list, container, false);
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
		
		lvUsers.setAdapter(aUsers);
//		lvUsers.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				listener.onTweetSelected(.get(position));
//				
//			}
	//	});
		

		return v;
	}




	

}

