package com.codepath.apps.twitter;

import org.json.JSONObject;

import com.activeandroid.util.Log;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;



import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ComposeDialog extends DialogFragment {
	private TextView tvMyName;
	private TextView tvMyHandle;
	private EditText etTweet;
	private ImageView ivMyProfileURL;
	private TwitterClient client;
	private Button btnTweet;
	private TextView tvCount;
	
	private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
           //This sets a textview to the current length
        	tvCount.setText(String.valueOf(140-s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
	};
	
	
	@Override
	public void onStart() {
		super.onStart();
		 // safety check
        if (getDialog() == null) {
                return;
        }
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_compose, container);
		tvMyName = (TextView) view.findViewById(R.id.tvMyName);
		tvMyHandle = (TextView) view.findViewById(R.id.tvMyHandle);
		etTweet = (EditText) view.findViewById(R.id.etTweet);
		ivMyProfileURL = (ImageView) view.findViewById(R.id.ivMyImage);
		btnTweet = (Button) view.findViewById(R.id.btnTweet);
		tvCount = (TextView) view.findViewById(R.id.tvCount);
		tvCount.setText("140");
		client = TwitterApp.getRestClient();
		etTweet.addTextChangedListener(mTextEditorWatcher);
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		imageLoader.displayImage(getArguments().getString("myProfileURL"), ivMyProfileURL);
		tvMyName.setText(getArguments().getString("myName"));
		tvMyHandle.setText("@" +getArguments().getString("myHandle"));
		Long statusId = getArguments().getLong("status_id");
		String username = getArguments().getString("user_name");
		if(statusId!= 0){
			etTweet.setText("@" +username);
		}
		etTweet.setTag(statusId);
		Window window = getDialog().getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		btnTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				client.sendTweet(new JsonHttpResponseHandler(){

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug" + e.toString());
						Log.d("debug" + s.toString());
					}

					@Override
					public void onSuccess(JSONObject arg1) {
						Tweet newTweet = Tweet.fromJSON(arg1);
						dListener.onDialogDone(newTweet);
						getDialog().dismiss();
					}
					
				},etTweet.getText().toString() , (Long)etTweet.getTag());
					
			}
		});
		return view;
	}
	
	
	public interface ComposeDialogListener {
		void onDialogDone(Tweet tweet);
	}

	public ComposeDialogListener dListener;

	public void setDialogListener( ComposeDialogListener composeDialogListener) {
		dListener = composeDialogListener;
	}

	public static ComposeDialog newInstance( User user) {
		ComposeDialog frag = new ComposeDialog();
		Bundle args = new Bundle();
		args.putString("myName", user.getName());
		args.putString("myHandle", user.getScreenName());
		args.putString("myProfileURL",user.getProfileImgURL() );
		args.putLong("status_id", 0);
		args.putString("user_name", "");
		frag.setArguments(args);
		return frag;
	}
	public static ComposeDialog newInstance( User user, Long statusId, String username) {
		ComposeDialog frag = new ComposeDialog();
		Bundle args = new Bundle();
		args.putString("myName", user.getName());
		args.putString("myHandle", user.getScreenName());
		args.putString("myProfileURL",user.getProfileImgURL() );
		args.putLong("status_id", statusId);
		args.putString("user_name", username);
		frag.setArguments(args);
		return frag;
	}
	

}
