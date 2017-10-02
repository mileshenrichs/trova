package models;

import java.time.LocalDateTime;

/**
 * A single social media post.
 */

public class Post {
    public LocalDateTime date;
    public MEDIA_TYPE mediaType;
    public Tweet tweet;
    public YoutubeVideo youtubeVideo;
    public InstaPost instaPost;

    public Post(LocalDateTime date, Tweet tweet) {
        this.mediaType = MEDIA_TYPE.TWITTER;
        this.date = date;
        this.tweet = tweet;
    }

    public Post(LocalDateTime date, YoutubeVideo video) {
        this.mediaType = MEDIA_TYPE.YOUTUBE;
        this.date = date;
        this.youtubeVideo = video;
    }

    public Post(LocalDateTime date, InstaPost post) {
        this.mediaType = MEDIA_TYPE.INSTAGRAM;
        this.date = date;
        this.instaPost = post;
    }

}
