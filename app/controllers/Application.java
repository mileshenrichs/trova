package controllers;

import play.mvc.*;
import java.util.*;
import models.*;

import static models.Person.findHandles;
import static models.InstaPost.getInstaPosts;
import static models.Tweet.getTweets;
import static models.YoutubeVideo.getVideos;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed(String name) {
        Person person = new Person(1, name, "imgUrl", "excerpt", findHandles(name));
        person.retreiveWikiInfo();
        person.findProfilePic();
        List<Post> posts = new ArrayList<>();

        long startTime = System.nanoTime();
        List<InstaPost> instaPosts = getInstaPosts(person.getInstaHandle());
        long endTime = System.nanoTime();
        System.out.println("getInstaPosts(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        for(InstaPost post : instaPosts) {
            posts.add(new Post(post));
        }

        startTime = System.nanoTime();
        List<Tweet> tweets = getTweets(person.getTwitterHandle());
        endTime = System.nanoTime();
        System.out.println("getTweets(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        for(Tweet tweet : tweets) {
            posts.add(new Post(tweet));
        }

        if(person.getYoutubeHandle() != null) {
            startTime = System.nanoTime();
            List<YoutubeVideo> videos = getVideos(person.getYoutubeHandle(), person.getYoutubeIdType());
            endTime = System.nanoTime();
            System.out.println("getVideos(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
            for (YoutubeVideo video : videos) {
                posts.add(new Post(video));
            }
        }

        startTime = System.nanoTime();
        Collections.sort(posts);
        endTime = System.nanoTime();
        System.out.println("Collections.sort(posts): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        render(person, posts);
    }
}