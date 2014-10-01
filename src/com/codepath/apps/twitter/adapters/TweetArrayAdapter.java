package com.codepath.apps.twitter.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.codepath.apps.listeners.CustomLinkMovementMethod;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.R.id;
import com.codepath.apps.twitter.R.layout;
import com.codepath.apps.twitter.models.Tweet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;

import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 // Get the data item for this position
	       Tweet tweet = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	       TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
	       TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
	       TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody); 
	       ImageView ivProfile =(ImageView) convertView.findViewById(R.id.ivProfileImage);
	       ImageView ivMedia =(ImageView) convertView.findViewById(R.id.ivMedia);
	       ivProfile.setImageResource(android.R.color.transparent);
	       ivMedia.setImageResource(android.R.color.transparent);
	       
//	       DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().build();
//
//	       
//	       .build();
//	       defaultOptions.
	       ImageLoader imageLoader = ImageLoader.getInstance();
	       // Populate the data into the template view using the data object
	       imageLoader.displayImage(tweet.getUser().getProfileImgURL(), ivProfile);
	       tvName.setText(tweet.getUser().getName());
	       tvScreenName.setText("@" +tweet.getUser().getScreenName());
	       tvCreatedAt.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
	       tvBody.setText(Html.fromHtml(tweet.getBody()));
	       
	       tvBody.setMovementMethod(null);
	       DisplayImageOptions options = new DisplayImageOptions.Builder()
	       .cacheInMemory()
	       .cacheOnDisc()
	       .build();
//	   imageLoader = ImageLoader.getInstance();
//	   imageLoader.init(imageLoaderConfiguration);
//	       
	       
	       ImageLoader mediaLoader = ImageLoader.getInstance();
	       if(tweet.getMediaURL().length() !=0){
	    	   mediaLoader.displayImage(tweet.getMediaURL(), ivMedia, options);
	       }else{
	    	   ivMedia.setVisibility(View.GONE);
	       }
	       // Return the completed view to render on screen
	       return convertView;
	}
	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	 
		return relativeDate;
	}
}
