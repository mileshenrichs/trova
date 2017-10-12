package controllers;

import play.mvc.*;

import java.util.*;

import models.*;

import static models.InstaPost.getInstaPosts;
import static models.Tweet.getTweets;
import static models.YoutubeVideo.getVideos;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed() {
        Person person = new Person(1, "Lil Uzi Vert", "imgUrl", "excerpt", new String[]{"liluzivert", "liluzivert", "LILUZIVERT"});
        List<Post> posts = new ArrayList<>();

        List<InstaPost> instaPosts = getInstaPosts(person.getInstaHandle());
        for(InstaPost post : instaPosts) {
            posts.add(new Post(post));
        }

        List<Tweet> tweets = getTweets(person.getTwitterHandle());
        for(Tweet tweet : tweets) {
            posts.add(new Post(tweet));
        }

        List<YoutubeVideo> videos = getVideos(person.getYoutubeHandle());
        for(YoutubeVideo video : videos) {
            posts.add(new Post(video));
        }

        Collections.sort(posts);
        render(person, posts);
    }

}