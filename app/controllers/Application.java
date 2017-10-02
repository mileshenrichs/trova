package controllers;

import play.mvc.*;

import java.util.*;

import models.*;

import static models.Tweet.getTweets;
import static models.YoutubeVideo.getVideos;
import static models.InstaPost.getInstaPosts;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed() {

        List<Post> posts = new ArrayList<>();

        List<Tweet> tweets = getTweets("liluzivert");
        Tweet tweet1 = tweets.get(0);
        posts.add(new Post(tweet1));

        List<YoutubeVideo> videos = getVideos("LILUZIVERT");
        YoutubeVideo video1 = videos.get(0);
        posts.add(new Post(video1));

        List<InstaPost> instaPosts = getInstaPosts("liluzivert");
        InstaPost post1 = instaPosts.get(0);
        posts.add(new Post(post1));


        // ======================= (RENDER) ===========================
        render(posts);
    }

}