package com.codepath.apps.twitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;


@Table(name="Tweets")
public class Tweet extends Model implements Parcelable{
	
	private static final long serialVersionUID = 1L;
	@Column(name="uid",unique=true, onUniqueConflict= Column.ConflictAction.REPLACE)
	private long uid;
	@Column(name="body")
	private String body;
	
	@Column(name="createdAt")
	private String createdAt;
	
	@Column(name="user", onUpdate=ForeignKeyAction.CASCADE, onDelete=ForeignKeyAction.CASCADE)
	private User user;
	
	@Column(name="mediaURL")
	private String mediaURL="";
	
	// needed for detailed tweet
	@Column(name="originalCreationAt")
	private String originalCreationAt;
	
	@Column(name="rtCount")
	private Integer rtCount;
	
	@Column(name="rtBy")
	private String rtBy;
	
	@Column(name="author")
	private User author;
	
	@Column(name="favoritesCount")
	private Integer favoritesCount;
	
	
	public String getOriginalCreationAt() {
		return originalCreationAt;
	}

	public Integer getRtCount() {
		return rtCount;
	}

	public String getRtBy() {
		return rtBy;
	}

	public User getAuthor() {
		return author;
	}

	public Integer getFavoritesCount() {
		return favoritesCount;
	}

	/**
	 * 
	 */

	
	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = (jsonObject.getString("text"));
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			if(jsonObject.getJSONObject("entities").has("media")){
				JSONArray mediaArray = jsonObject.getJSONObject("entities").getJSONArray("media");
				if(mediaArray != null &&  mediaArray.getJSONObject(0) != null ){
					JSONObject mediaJSON = mediaArray.getJSONObject(0) ; 
					if(mediaJSON.getString("type").equalsIgnoreCase("photo")){
						tweet.mediaURL = mediaArray.getJSONObject(0).getString("media_url");
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;

	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public String getBody() {
		return body;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
	      // Process each result in json array, decode and convert to business object
	      for (int i=0; i < jsonArray.length(); i++) {
	          JSONObject tweetJson = null;
	          try {
	          	tweetJson = jsonArray.getJSONObject(i);
	          } catch (Exception e) {
	              e.printStackTrace();
	              continue;
	          }

	          Tweet tweet = Tweet.fromJSON(tweetJson);
	          if (tweet != null) {
	          	tweets.add(tweet);
	          }
	      }

	      return tweets;
	}

	public String getMediaURL() {
		return mediaURL;
	}

	public static Tweet detailedTweetFromJSON(Tweet tweet, JSONObject json){
		try {
			tweet.rtCount = json.getInt("retweet_count");
			tweet.favoritesCount = json.getInt("favorite_count");
			tweet.author = User.fromJSON(json.getJSONObject("user"));
			tweet.originalCreationAt = json.getString("created_at");
			//tweet.rtBy = json.get
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return tweet;
	}

	public static List<Tweet> getAll(User user){
		return new Select()
        .from(Tweet.class)
        .where("User = ?", user.getId())
        .orderBy("Name ASC")
        .execute();
	}
	
	public static List<Tweet> deleteAll(){
		return new Delete()
		.from(Tweet.class)
		.execute();
	}
	
	public static List<Tweet> getAll(){
		return new Select()
        .from(Tweet.class)
        .execute();
	}
	
	public static void insertTweet(Tweet tweet){
		List<Tweet> qResults = new Select().from(Tweet.class)
				.where("uid=" + Long.toString(tweet.getUid()))
				.execute();
		if(qResults.size()==0){
			User user = User.insertUser(tweet.getUser());
			tweet.save();
		}
	}
	
	public Tweet(){
		super();
	}
	protected Tweet(Parcel in) {
        uid = in.readLong();
        body = in.readString();
        createdAt = in.readString();
        user = (User) in.readValue(User.class.getClassLoader());
        mediaURL = in.readString();
        originalCreationAt = in.readString();
        rtCount = in.readByte() == 0x00 ? null : in.readInt();
        rtBy = in.readString();
        author = (User) in.readValue(User.class.getClassLoader());
        favoritesCount = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(body);
        dest.writeString(createdAt);
        dest.writeValue(user);
        dest.writeString(mediaURL);
        dest.writeString(originalCreationAt);
        if (rtCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(rtCount);
        }
        dest.writeString(rtBy);
        dest.writeValue(author);
        if (favoritesCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(favoritesCount);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
