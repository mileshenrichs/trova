package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getVideoID() {
        return videoID;
    }

    public LocalDateTime getVideoDate() {
        return videoDate;
    }
}
