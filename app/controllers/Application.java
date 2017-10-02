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

        // ======================= TWITTER ===========================
        List<Tweet> tweets = getTweets("liluzivert");


        // ======================= YOUTUBE ===========================
        List<YoutubeVideo> videos = getVideos("LILUZIVERT");


        // ======================= INSTAGRAM ===========================
        List<InstaPost> instaPosts = getInstaPosts("liluzivert");


        // ======================= (RENDER) ===========================
        render(tweets, videos, instaPosts);
    }

}