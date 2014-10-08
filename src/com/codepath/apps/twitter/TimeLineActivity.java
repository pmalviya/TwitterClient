package com.codepath.apps.twitter;


import org.json.JSONObject;


import com.activeandroid.util.Log;

import com.codepath.apps.listeners.FragmentTabListener;
import com.codepath.apps.twitter.ComposeDialog.ComposeDialogListener;

import com.codepath.apps.twitter.fragments.HomeTimelineFragment;
import com.codepath.apps.twitter.fragments.TweetsListFragment;
import com.codepath.apps.twitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;



import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.widget.ProgressBar;
import android.widget.Toast;

public class TimeLineActivity extends FragmentActivity implements TweetsListFragment.OnItemSelectedListener {
	private TwitterClient  client;
	public User myself; 
	
	/**
	 * This method checks presence of internet connectivity
	 * @return network status
	 */
	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	// Should be called manually when an async task has started
    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true); 
    }
    
    // Should be called when an async task has finished
    public void hideProgressBar() {
    	setProgressBarIndeterminateVisibility(false); 
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_time_line);
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 64, 153, 255)));
		client = TwitterApp.getRestClient();
		getMyInfo();
		setupTabs();
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
		pb.setVisibility(ProgressBar.VISIBLE);
		// run a background job and once complete
		pb.setVisibility(ProgressBar.INVISIBLE);
	}

	public void onProfileClick(MenuItem mi){
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("user", myself);
		startActivity(i);
		
	}
	public void onTweetSelected(Tweet tweet){
		Intent i = new Intent(this, DetailedTweetActivity.class);
		i.putExtra("tweet", tweet);
		startActivity(i);
	}
	public void onComposeClick(MenuItem mi ){
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		FragmentManager fm = getFragmentManager();

		ComposeDialog composeDialog = ComposeDialog.newInstance(myself);
		composeDialog.show(fm, "fragment_compose");
		composeDialog.setDialogListener(new ComposeDialogListener() {

			@Override
			public void onDialogDone(Tweet tweet) {
				
				TweetsListFragment fragment = (TweetsListFragment) 
			        	getSupportFragmentManager().findFragmentById(R.id.flContainer);
					fragment.add(tweet);
			}
		}); 
	}

	public void onReplyClick(View v){
		if(!isNetworkAvailable()){
			Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
			return;
		}
		FragmentManager fm = getFragmentManager();
		Long tweet_id = (Long) v.getTag();
		String userName = v.getTag(R.id.TAG_USER_NAME).toString();
		ComposeDialog composeDialog = ComposeDialog.newInstance(myself, tweet_id, userName);
		composeDialog.show(fm, "fragment_compose");
		composeDialog.setDialogListener(new ComposeDialogListener() {

			@Override
			public void onDialogDone(Tweet tweet) {
				TweetsListFragment fragment = (TweetsListFragment) 
			        	getSupportFragmentManager().findFragmentById(R.id.flContainer);
					fragment.add(tweet);
				//tweets.addFirst(tweet);
				//aTweets.notifyDataSetChanged();
			}
		}); 
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline_menu, menu);
		return super.onCreateOptionsMenu(menu);
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
				Toast.makeText(TimeLineActivity.this, "failed", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(JSONObject arg1) {
				myself =  User.fromJSON(arg1);
				Toast.makeText(TimeLineActivity.this, "success", Toast.LENGTH_LONG).show();
			}

		});
	}
	
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
			.newTab()
			.setText("HOME")
			.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home",
								HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("MENTIONS")
			.setIcon(R.drawable.ic_mention)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions",
			    		MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}
	
}
