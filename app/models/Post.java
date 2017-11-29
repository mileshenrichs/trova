package models;

import java.time.LocalDateTime;

/**
 * A single social media post.
 */

public class Post implements Comparable<Post> {
    public LocalDateTime date;
    public MEDIA_TYPE mediaType;
    public Tweet tweet;
    public YoutubeVideo youtubeVideo;
    public InstaPost instaPost;
    public FacebookPost fbPost;

    public Post(Tweet tweet) {
        this.mediaType = MEDIA_TYPE.TWITTER;
        this.date = tweet.getTweetDate();
        this.tweet = tweet;
    }

    public Post(YoutubeVideo video) {
        this.mediaType = MEDIA_TYPE.YOUTUBE;
        this.date = video.getVideoDate();
        this.youtubeVideo = video;
    }

    public Post(InstaPost post) {
        this.mediaType = MEDIA_TYPE.INSTAGRAM;
        this.date = post.getPostDate();
        this.instaPost = post;
    }

    public Post(FacebookPost post) {
        this.mediaType = MEDIA_TYPE.FACEBOOK;
        this.date = post.getPostDate();
        this.fbPost = post;
    }

    public MEDIA_TYPE getType() {
        return this.mediaType;
    }

    public Tweet getTweet() {
        return this.tweet;
    }

    public YoutubeVideo getVideo() {
        return this.youtubeVideo;
    }

    public InstaPost getInstaPost() {
        return this.instaPost;
    }

    public FacebookPost getFbPost() {
        return fbPost;
    }

    @Override
    public int compareTo(Post post) {
        if(this.date.isBefore(post.date)) return 1;
        else if(this.date.isAfter(post.date)) return -1;
        else return 0;
    }

    /**
     * Enum containing available social media sources from which posts are drawn.
     */
    public enum MEDIA_TYPE {
        TWITTER,
        YOUTUBE,
        INSTAGRAM,
        FACEBOOK
    }
}
