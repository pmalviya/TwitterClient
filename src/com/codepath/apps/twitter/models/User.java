package com.codepath.apps.twitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name="Users")
public class User extends Model implements Parcelable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="name")
	private String name;
	
	@Column(name="uid", unique=true)
	private long uid;
	
	@Column(name="screenName")
	private String screenName;
	
	@Column(name="profileImgURL")
	private String profileImgURL;
	
	@Column(name="profileBackgroundURL")
	private String profileBackgroundURL;
	
	@Column(name="description")
	private String description;
	
	@Column(name="followerCount")
	private Integer followerCount;
	
	@Column(name="followingCount")
	private Integer followingCount;
	
	@Column(name="tweetsCount")
	private Integer tweetsCount;
	
	public static ArrayList<User> fromJSONArray(JSONArray jsonArray) {
		ArrayList<User> users = new ArrayList<User>(jsonArray.length());
	      // Process each result in json array, decode and convert to business object
	      for (int i=0; i < jsonArray.length(); i++) {
	          JSONObject userJson = null;
	          try {
	          	userJson = jsonArray.getJSONObject(i);
	          } catch (Exception e) {
	              e.printStackTrace();
	              continue;
	          }

	          User user = User.fromJSON(userJson);
	          if (user != null) {
	          	users.add(user);
	          }
	      }

	      return users;
	}

	public static User fromJSON(JSONObject json) {
		User u = new User();
		try {
			u.name = json.getString("name");

			u.uid = json.getLong("id");
			u.screenName = json.getString("screen_name");
			u.profileImgURL = json.getString("profile_image_url");
			u.profileBackgroundURL = json.getString("profile_background_image_url");
			u.description = json.getString("description");
			u.followerCount =  json.getInt("followers_count");
			u.followingCount = json.getInt("friends_count");
			u.tweetsCount = json.getInt("statuses_count");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return u;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImgURL() {
		return profileImgURL;
	}
	
	public String getProfileBackgroundURL() {
		return profileBackgroundURL;
	}
	public String getDescription() {
		return description;
	}
	public Integer getFollowingCount(){
		return followingCount;
	}
	public Integer getFollowerCount(){
		return followerCount;
	}
	public Integer getTweetsCount(){
		return tweetsCount;
	}
	public User(){
		super();
	}
	
	public static User insertUser(User user){
		List<User> qResults = new Select().from(User.class)
				.where("uid=" + Long.toString(user.getUid()))
				.execute();
		if(qResults.size()==0){
			user.save();
			return user;
		}else{
			return qResults.get(0);
		}
		
	}
	protected User(Parcel in) {
        name = in.readString();
        uid = in.readLong();
        screenName = in.readString();
        profileImgURL = in.readString();
        profileBackgroundURL = in.readString();
        description= in.readString();
        followingCount = in.readInt();
        followerCount = in.readInt();
        tweetsCount = in.readInt();
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(uid);
        dest.writeString(screenName);
        dest.writeString(profileImgURL);
        dest.writeString(profileBackgroundURL);
        dest.writeString(description);
        dest.writeInt(followingCount);
        dest.writeInt(followerCount);
        dest.writeInt(tweetsCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
