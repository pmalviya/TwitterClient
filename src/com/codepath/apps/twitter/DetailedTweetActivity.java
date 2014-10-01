package com.codepath.apps.twitter;

import org.json.JSONObject;

import com.activeandroid.util.Log;
import com.codepath.apps.twitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedTweetActivity extends Activity {
	private Tweet tweet;
	private TwitterClient client;
	private TextView tvRt;
	private TextView tvRetweets;
	private TextView tvFavorites;
	private TextView tvDateTime;
	private TextView tvAuthor;
	private TextView tvHandle;
	private ImageView ivAuthor;
	private ImageView ivImage;
	private TextView tvText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_tweet);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 64, 153, 255)));
		tvRt = (TextView) findViewById(R.id.tvrt);
		tvRetweets = (TextView) findViewById(R.id.tvRetweets);
		tvText = (TextView) findViewById(R.id.tvText);
		tvFavorites = (TextView) findViewById(R.id.tvFavorites);
		tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		tvAuthor = (TextView) findViewById(R.id.tvAuthor);
		tvHandle = (TextView) findViewById(R.id.tvHandle);
		ivAuthor = (ImageView) findViewById(R.id.ivAuthor);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		
		client = TwitterApp.getRestClient();
		tweet = (Tweet) getIntent().getParcelableExtra("tweet");
		getTweetStatus(tweet.getUid());
	}
	public void getTweetStatus(long l){
		client.getTweetStatus(new JsonHttpResponseHandler(){

			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				Tweet.detailedTweetFromJSON(tweet, arg1);
				tvRt.setText(tweet.getRtBy());
				tvRetweets.setText(tweet.getRtCount().toString());
				tvAuthor.setText(tweet.getAuthor().getName());
				tvDateTime.setText(tweet.getOriginalCreationAt());
				tvText.setText(Html.fromHtml(tweet.getBody()));
				tvFavorites.setText(tweet.getFavoritesCount().toString());
				tvHandle.setText("@"+tweet.getAuthor().getScreenName());
				ImageLoader imageLoader = ImageLoader.getInstance();
			    // Populate the data into the template view using the data object
			    imageLoader.displayImage(tweet.getAuthor().getProfileImgURL(), ivAuthor);
			    if(tweet.getMediaURL().length() == 0){
			    	ivImage.setVisibility(View.GONE);
			    }else{
			    	imageLoader.displayImage(tweet.getMediaURL(), ivImage);
			    }

			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug" + e.toString());
				Log.d("debug" + s.toString());
			}
	
		}, l);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailed_tweet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
