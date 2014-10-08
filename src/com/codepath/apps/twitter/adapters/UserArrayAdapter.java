package com.codepath.apps.twitter.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.codepath.apps.listeners.CustomLinkMovementMethod;
import com.codepath.apps.twitter.ComposeDialog;
import com.codepath.apps.twitter.ProfileActivity;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TimeLineActivity;
import com.codepath.apps.twitter.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.twitter.R.id;
import com.codepath.apps.twitter.R.layout;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;

import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<User> {

	public UserArrayAdapter(Context context, List<User> users) {
		super(context, 0, users);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 // Get the data item for this position
	       User user = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.tvUserName);
	       TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
	       
	       TextView tvTagline = (TextView) convertView.findViewById(R.id.tvTagline); 
	       ImageView ivProfile =(ImageView) convertView.findViewById(R.id.ivUserProf);
	     
	    	   ivProfile.setImageResource(android.R.color.transparent);
	    	  

//	       DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory().build();
//
//	       
//	       .build();
//	       defaultOptions.
	       try{
	    	   ImageLoader imageLoader = ImageLoader.getInstance();
	    	   // Populate the data into the template view using the data object
	    	   imageLoader.displayImage(user.getProfileImgURL(), ivProfile);
	       }catch(NullPointerException e){
	    	   
	       }
	       if(user != null){
	    	   tvName.setText(user.getName());
	    	   tvScreenName.setText("@" +user.getScreenName());
	       }
	       tvTagline.setText(Html.fromHtml(user.getDescription()));
	       
	       tvTagline.setMovementMethod(null);
	       DisplayImageOptions options = new DisplayImageOptions.Builder()
	       .cacheInMemory()
	       .cacheOnDisc()
	       .build();

	       ivProfile.setTag(user);
	       ivProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(), ProfileActivity.class);
				i.putExtra("user",(User) v.getTag() );
				getContext().startActivity(i);
				
			}
		});
	       // Return the completed view to render on screen
	       return convertView;
	}
	
}
