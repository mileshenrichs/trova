package controllers;

import play.mvc.*;
import java.util.*;
import models.*;

import static models.Person.findHandles;
import static models.InstaPost.getInstaPosts;
import static models.Tweet.getTweets;
import static models.YoutubeVideo.getVideos;
import static models.FacebookPost.getFacebookPosts;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void viewPerson(String name) {
        HashMap<String, String> uziHandles = new HashMap<>();
        uziHandles.put("wiki", "Lil_Uzi_Vert");
        uziHandles.put("insta", "liluzivert");
        uziHandles.put("twitter", "liluzivert");
        uziHandles.put("youtube", "LILUZIVERT");
        uziHandles.put("youtubeIdType", "USERNAME");
        uziHandles.put("facebook", "LilUziVert");
        String excerpt = "Symere Woods (born July 31, 1994), known professionally as Lil Uzi Vert, is an American hip hop recording artist. Based in Philadelphia, he gained recognition after releasing his debut single, \"Money Longer\", and several mixtapes, including Luv Is Rage, Lil Uzi Vert vs. the World, and The Perfect Luv Tape. Lil Uzi Vert has also collaborated with Migos on \"Bad and Boujee\", which became Lil Uzi Vert's first US Billboard Hot 100 number-one single. In August 2017, Lil Uzi Vert released his debut studio album, Luv Is Rage 2, where it reached number one on the Billboard 200 Albums Chart. ";
        Person person = new Person(1, "Lil Uzi Vert", "http://s2.evcdn.com/images/edpborder500/I0-001/031/479/413-3.jpeg_/lil-uzi-vert-13.jpeg", excerpt, uziHandles);
//        Person person = new Person(1, name, "imgUrl", "excerpt", findHandles(name));
//        person.retreiveWikiInfo();
//        person.findProfilePic();
        List<Post> posts = new ArrayList<>();

        long startTime = System.nanoTime();
        List<InstaPost> instaPosts = getInstaPosts(person.getInstaHandle());
        long endTime = System.nanoTime();
        System.out.println("getInstaPosts(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        for(InstaPost instaPost : instaPosts) {
            posts.add(new Post(instaPost));
        }
//
//        startTime = System.nanoTime();
//        List<Tweet> tweets = getTweets(person.getTwitterHandle());
//        endTime = System.nanoTime();
//        System.out.println("getTweets(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
//        for(Tweet tweet : tweets) {
//            posts.add(new Post(tweet));
//        }
//
//        if(person.getYoutubeHandle() != null) {
//            startTime = System.nanoTime();
//            List<YoutubeVideo> videos = getVideos(person.getYoutubeHandle(), person.getYoutubeIdType());
//            endTime = System.nanoTime();
//            System.out.println("getVideos(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
//            for (YoutubeVideo video : videos) {
//                posts.add(new Post(video));
//            }
//        }
//
//        startTime = System.nanoTime();
//        List<FacebookPost> fbPosts = getFacebookPosts("LilUziVert");
//        endTime = System.nanoTime();
//        System.out.println("getFacebookPosts(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
//        for(FacebookPost fbPost : fbPosts) {
//            posts.add(new Post(fbPost));
//        }

//        startTime = System.nanoTime();
        Collections.sort(posts);
//        endTime = System.nanoTime();
//        System.out.println("Collections.sort(posts): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        render(person, posts);
    }
}