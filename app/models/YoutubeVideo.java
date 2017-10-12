package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static controllers.util.URLUtil.getURLBody;

/**
 * Represents a single YouTube Video with fields videoID and videoDate.
 */
public class YoutubeVideo {
    private String videoID;
    private LocalDateTime videoDate;

    public YoutubeVideo(String id, String date) {
        this.videoID = id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.videoDate = LocalDateTime.parse(date, formatter);
    }

    public String getVideoId() {
        return this.videoID;
    }

    public LocalDateTime getVideoDate() {
        return this.videoDate;
    }

    public static List<YoutubeVideo> getVideos(String username) {
        List<YoutubeVideo> videos = new ArrayList<>();

        final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";

        String body = null;
        try {
            body = getURLBody(new URL("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + username + "&key=" + YOUTUBE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("items");
        String playlistID = arr.getJSONObject(0).getJSONObject("contentDetails")
                .getJSONObject("relatedPlaylists").getString("uploads");

        try {
            body = getURLBody(new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=20&playlistId=" + playlistID + "&key=" + YOUTUBE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        obj = new JSONObject(body);
        int totalResults = obj.getJSONObject("pageInfo").getInt("totalResults");
        arr = obj.getJSONArray("items");

        for(int i = 0; i < Math.min(20, totalResults); i++) {
            String videoID = arr.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
            String videoDate = arr.getJSONObject(i).getJSONObject("contentDetails").getString("videoPublishedAt");
            videos.add(new YoutubeVideo(videoID, videoDate));
        }

        return videos;
    }
}
