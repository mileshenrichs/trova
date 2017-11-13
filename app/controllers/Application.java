package controllers;

import play.mvc.*;
import java.util.*;
import models.*;

import static controllers.util.PersonInfoUtil.findHandles;
import static models.InstaPost.getInstaPosts;
import static models.Tweet.getTweets;
import static models.YoutubeVideo.getVideos;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed() {
        String name = "lil uzi";
        Person person = new Person(1, name, "imgUrl", "excerpt", findHandles(name));
        person.retreiveWikiInfo();
        person.findProfilePic();
        List<Post> posts = new ArrayList<>();

        List<InstaPost> instaPosts = getInstaPosts(person.getInstaHandle());
        for(InstaPost post : instaPosts) {
            posts.add(new Post(post));
        }

        List<Tweet> tweets = getTweets(person.getTwitterHandle());
        for(Tweet tweet : tweets) {
            posts.add(new Post(tweet));
        }

        List<YoutubeVideo> videos = getVideos(person.getYoutubeHandle(), person.getYoutubeIdType());
        for(YoutubeVideo video : videos) {
            posts.add(new Post(video));
        }

        Collections.sort(posts);
        render(person, posts);
    }
}