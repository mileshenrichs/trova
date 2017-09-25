package controllers;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import play.*;
import play.mvc.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed() {

        // ======================= TWITTER ===========================
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("t3Hs7gFXcPG6IOAhqtX0rlbF1")
                .setOAuthConsumerSecret("b91yBAOgBVI0QGGbh6rvUkCg5oN8oRWlBAc2ali4zfXUlay8xh")
                .setOAuthAccessToken("758838713287712769-syai0cnNb7gglwaLeOFChvGv4onWVqS")
                .setOAuthAccessTokenSecret("fgmnfbYdR3p9rtaFPKtLMB5Udh3ve2bKcyHoO3jkyBwAq");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        List<Status> statuses = new ArrayList<>();
        List<LocalDateTime> twitterDates = new ArrayList<>();
        try {
            statuses = twitter.getUserTimeline("liluzivert");

            for (Status s : statuses) {
                Date statusDate = s.getCreatedAt();
                twitterDates.add(statusDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        List<String> twitterDateStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US);
        for (LocalDateTime ldt : twitterDates) {
            twitterDateStrings.add(ldt.format(formatter));
        }

        // ======================= YOUTUBE ===========================
        final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";
        String youtubeUser = "LILUZIVERT";

        String body = "";
        URL url = null;
        try {
            url = new URL("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + youtubeUser + "&key=" + YOUTUBE_KEY);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(url, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("items");
        String playlistID = arr.getJSONObject(0).getJSONObject("contentDetails")
                .getJSONObject("relatedPlaylists").getString("uploads");

        try {
            url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&playlistId=" + playlistID + "&key=" + YOUTUBE_KEY);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(url, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(body);

        obj = new JSONObject(body);
        arr = obj.getJSONArray("items");
        String videoID = arr.getJSONObject(0).getJSONObject("contentDetails").getString("videoId");
        String videoDate = arr.getJSONObject(0).getJSONObject("contentDetails").getString("videoPublishedAt");

        YoutubeVideo firstVideo = new YoutubeVideo(videoID, videoDate);

        // ======================= INSTAGRAM ===========================

        String instaUser = "liluzivert";

        String instaBody = "";
        URL instaURL = null;

        try {
            instaURL = new URL("https://www.instagram.com/" + instaUser + "/media/");
            URLConnection urlConnection = instaURL.openConnection();
            InputStream in = urlConnection.getInputStream();
            String encoding = urlConnection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            instaBody = IOUtils.toString(instaURL, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject instaObj = new JSONObject(instaBody);
        JSONArray instaArr = instaObj.getJSONArray("items");
        String postID = instaArr.getJSONObject(0).getString("code");

        try {
            instaURL = new URL("https://api.instagram.com/oembed/?url=http://instagr.am/p/" + postID + "/");
            URLConnection urlConnection = instaURL.openConnection();
            InputStream in = urlConnection.getInputStream();
            String encoding = urlConnection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            instaBody = IOUtils.toString(instaURL, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instaObj = new JSONObject(instaBody);
        String embedHTML = instaObj.getString("html");

        embedHTML = embedHTML.substring(0, embedHTML.length() - 76);
        embedHTML = embedHTML.replace("max-width:658px;", "max-width:350px;");
        embedHTML = embedHTML.substring(0, 91) + "height: 510px; " + embedHTML.substring(92);

        String firstInstaPost = embedHTML;

        // ======================= (RENDER) ===========================
        render(statuses, twitterDateStrings, firstVideo, firstInstaPost);
    }

}