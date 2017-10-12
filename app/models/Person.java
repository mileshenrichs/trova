package models;

import org.json.JSONArray;
import org.json.JSONObject;
import twitter4j.Twitter;
import twitter4j.User;

import java.net.URL;

import static models.Tweet.getTwitterInstance;

import static controllers.util.URLUtil.getURLBody;
import static controllers.util.FollowersUtil.processFollowerCount;

/**
 * Represents a single person. Contains data such as unique id, name, and social media handles.
 */
public class Person {
    public long id;
    public String name;
    public String profileImgUrl;
    public String wikiExcerpt;
    public String instaHandle;
    public String twitterHandle;
    public String youtubeHandle;

    public Person(long id, String name, String imgUrl, String excerpt, String[] handles) {
        this.id = id;
        this.name = name;
        this.profileImgUrl = imgUrl;
        this.wikiExcerpt = excerpt;
        this.instaHandle = handles[0];
        this.twitterHandle = handles[1];
        this.youtubeHandle = handles[2];
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public String getWikiExcerpt() {
        return wikiExcerpt;
    }

    public String getInstaHandle() {
        return instaHandle;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public String getYoutubeHandle() {
        return youtubeHandle;
    }

    public String getInstaFollowers() {
        URL instaURL = null;
        try {
            instaURL = new URL("https://www.instagram.com/" + instaHandle + "/?__a=1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String instaBody = getURLBody(instaURL);
        JSONObject instaObj = new JSONObject(instaBody);
        int instaFollowers = instaObj.getJSONObject("user").getJSONObject("followed_by").getInt("count");

        return processFollowerCount(instaFollowers);
    }

    public String getTwitterFollowers() {
        Twitter twitter = getTwitterInstance();
        User twitterUser = null;
        try {
            twitterUser = twitter.showUser(twitterHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processFollowerCount(twitterUser.getFollowersCount());
    }

    public String getYoutubeFollowers() {
        final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";
        URL youtubeURL = null;
        try {
            youtubeURL = new URL("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + youtubeHandle + "&key=" + YOUTUBE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String youtubeBody = getURLBody(youtubeURL);
        JSONObject obj = new JSONObject(youtubeBody);
        JSONArray arr = obj.getJSONArray("items");
        String userID = arr.getJSONObject(0).getString("id");

        try {
            youtubeURL = new URL("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + userID + "&key=" + YOUTUBE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        youtubeBody = getURLBody(youtubeURL);
        obj = new JSONObject(youtubeBody);
        arr = obj.getJSONArray("items");
        int youtubeFollowers = arr.getJSONObject(0).getJSONObject("statistics").getInt("subscriberCount");

        return processFollowerCount(youtubeFollowers);
    }
}
