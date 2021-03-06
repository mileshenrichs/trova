package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.Person.*;

import static util.URLUtil.getURLBody;

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

    public static List<YoutubeVideo> getVideos(String username, YOUTUBE_ID_TYPE idType) {
        List<YoutubeVideo> videos = new ArrayList<>();

        final String YOUTUBE_KEY = Keys.youtube();

        String body;
        if(idType == YOUTUBE_ID_TYPE.USERNAME) {
            body = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + username + "&key=" + YOUTUBE_KEY);
        } else {
            body = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id=" + username + "&key=" + YOUTUBE_KEY);
        }

        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("items");
        String playlistID = arr.getJSONObject(0).getJSONObject("contentDetails")
                .getJSONObject("relatedPlaylists").getString("uploads");
        body = getURLBody("https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=20&playlistId=" + playlistID + "&key=" + YOUTUBE_KEY);

        obj = new JSONObject(body);
        int totalResults = obj.getJSONObject("pageInfo").getInt("totalResults");
        arr = obj.getJSONArray("items");

        for(int i = 0; i < Math.min(10, totalResults); i++) {
            String videoID = arr.getJSONObject(i).getJSONObject("contentDetails").getString("videoId");
            String videoDate = arr.getJSONObject(i).getJSONObject("contentDetails").getString("videoPublishedAt");
            videos.add(new YoutubeVideo(videoID, videoDate));
        }

        return videos;
    }
}
