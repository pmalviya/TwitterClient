package com.codepath.apps.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.codepath.apps.twitter.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.twitter.fragments.FollowersFragment;
import com.codepath.apps.twitter.fragments.TweetsListFragment;
import com.codepath.apps.twitter.fragments.UsersListFragment;
import com.codepath.apps.twitter.fragments.UsersTimelineFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends FragmentActivity implements TweetsListFragment.OnItemSelectedListener{
	private  User user;
	private TextView profName;
	private TextView profHandle;
	private ImageView profImg;
	private RadioButton btn1;
	private RadioButton btn2;
	private RadioButton btn3;
	
	public User getUser(){
		return user;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		// About StrictMode Learn More at => http://stackoverflow.com/questions/8258725/strict-mode-in-android-2-2
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();      
	    StrictMode.setThreadPolicy(policy);

		user = (User) getIntent().getParcelableExtra("user");
		getActionBar().setTitle("@" + user.getScreenName());
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 64, 153, 255)));
		profName = (TextView) findViewById(R.id.tvProfileName);
		profHandle = (TextView) findViewById(R.id.tvProfileHandle);
		profImg = (ImageView) findViewById(R.id.ivProfileImg);
		btn1 = (RadioButton) findViewById(R.id.btn1);
		btn2 = (RadioButton) findViewById(R.id.btn2);
		btn3 = (RadioButton) findViewById(R.id.btn3);
		profName.setText(user.getName());
		profHandle.setText("@"+user.getScreenName());
		ImageLoader imageLoader = ImageLoader.getInstance();
	    // Populate the data into the template view using the data object
	    imageLoader.displayImage(user.getProfileImgURL(), profImg);
	    
	    btn1.setText(user.getTweetsCount()+"\n TWEETS");
	    btn2.setText(user.getFollowingCount()+"\n FOLLOWING");
	    btn2.setTag(user.getScreenName());
	    btn3.setText(user.getFollowerCount()+"\n FOLLOWERS");
	    btn3.setTag(user.getScreenName());
	    Bitmap btImg = getBitmapFromURL(user.getProfileBackgroundURL());
	    RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.rlHeader);
	    Drawable drawable = new BitmapDrawable(btImg);
	    drawable = resize(drawable);
	    rLayout.setBackground(drawable);
	 // Begin the transaction
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    // Replace the container with the new fragment
	    ft.replace(R.id.flTweets, new UsersTimelineFragment(user.getUid()));
	    // or ft.add(R.id.your_placeholder, new FooFragment());
	    // Execute the changes specified
	    ft.commit();

	    btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			    // Replace the container with the new fragment
				UsersListFragment frag = (UsersListFragment) FollowersFragment.newInstance(v.getTag().toString());
			    ft.replace(R.id.flTweets, frag);
			    // or ft.add(R.id.your_placeholder, new FooFragment());
			    // Execute the changes specified
			    ft.commit();
				
			}
		});
	}
	private Drawable resize(Drawable image) {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		
	    Bitmap b = ((BitmapDrawable)image).getBitmap();
	    float bWidth = b.getWidth();
	    float bHeight = b.getHeight();
	    float aspectRatio = bHeight/bWidth;
	    int newHeight = (int) Math.floor(aspectRatio * width);
	    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, 400, false);
	    return new BitmapDrawable(getResources(), bitmapResized);
	}
	public Bitmap getBitmapFromURL(String imageUrl) {
	    try {
	        URL url = new URL(imageUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	public void onTweetSelected(Tweet tweet){
		Intent i = new Intent(this, DetailedTweetActivity.class);
		i.putExtra("tweet", tweet);
		startActivity(i);
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
	public void onReplyClick(View v){

		FragmentManager fm = getFragmentManager();
		Long tweet_id = (Long) v.getTag();
		String userName = v.getTag(R.id.TAG_USER_NAME).toString();
		ComposeDialog composeDialog = ComposeDialog.newInstance(user, tweet_id, userName);
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
}
