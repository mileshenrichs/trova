package models;

import java.time.LocalDateTime;

/**
 * A single social media post.
 */

public class Post {

    public Post(LocalDateTime date, Tweet tweet) {
        this.date = date;
        this.mediaType = MEDIA_TYPE.TWITTER;
        this.tweet = tweet;
    }

    public Post(LocalDateTime date, YoutubeVideo video) {
        this.date = date;
        this.mediaType = MEDIA_TYPE.YOUTUBE;
        this.youtubeVideo = video;
    }

    public Post(LocalDateTime date, InstaPost post) {
        this.date = date;
        this.mediaType = MEDIA_TYPE.INSTAGRAM;
        this.instaPost = post;
    }

    public LocalDateTime date;
    public MEDIA_TYPE mediaType;

    public Tweet tweet;
    public YoutubeVideo youtubeVideo;
    public InstaPost instaPost;

}
