package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static util.URLUtil.getURLBody;

/**
 * Created by Henrichs on 11/24/2017.
 * A Facebook post
 */
public class FacebookPost {
    private String postUrl;
    private String content;
    private LocalDateTime postDate;

    private FacebookPost(String post, String content, LocalDateTime date) {
        this.postUrl = post;
        this.content = content;
        this.postDate = date;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public String getDisplayDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        return postDate.format(formatter);
    }

    public static List<FacebookPost> getFacebookPosts(String username) {
        List<FacebookPost> posts = new ArrayList<>();
        final String FACEBOOK_KEY = Keys.facebook();
        final String FACEBOOK_SECRET = Keys.facebookSecret();

        String authenticate = getURLBody("https://graph.facebook.com/oauth/access_token?grant_type=client_credentials&client_id=" + FACEBOOK_KEY + "&client_secret=" + FACEBOOK_SECRET);
        String token = new JSONObject(authenticate).getString("access_token");
        String fbJSON = getURLBody("https://graph.facebook.com/" + username +
                "/feed?access_token=" + token + "&fields=permalink_url,created_time,message");

        JSONArray postsArr = new JSONObject(fbJSON).getJSONArray("data");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for(int i = 0; i < postsArr.length(); i++) {
            String postUrl = postsArr.getJSONObject(i).getString("permalink_url");
            String createdAt = postsArr.getJSONObject(i).getString("created_time")
                    .substring(0, postsArr.getJSONObject(i).getString("created_time").length() - 5);
            String content = postsArr.getJSONObject(i).getString("message");
            posts.add(new FacebookPost(postUrl, content, LocalDateTime.parse(createdAt, formatter)));
        }
        return posts;
    }
}
