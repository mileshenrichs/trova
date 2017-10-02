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
        for(Tweet tweet : tweets) {
            posts.add(new Post(tweet));
        }

        List<YoutubeVideo> videos = getVideos("LILUZIVERT");
        for(YoutubeVideo video : videos) {
            posts.add(new Post(video));
        }

        List<InstaPost> instaPosts = getInstaPosts("liluzivert");
        for(InstaPost post : instaPosts) {
            posts.add(new Post(post));
        }

        Collections.sort(posts);
        render(posts);
    }

}