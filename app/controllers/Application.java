package controllers;

import java.time.LocalDateTime;
import play.*;
import play.mvc.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void feed() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("t3Hs7gFXcPG6IOAhqtX0rlbF1")
                .setOAuthConsumerSecret("b91yBAOgBVI0QGGbh6rvUkCg5oN8oRWlBAc2ali4zfXUlay8xh")
                .setOAuthAccessToken("758838713287712769-syai0cnNb7gglwaLeOFChvGv4onWVqS")
                .setOAuthAccessTokenSecret("fgmnfbYdR3p9rtaFPKtLMB5Udh3ve2bKcyHoO3jkyBwAq");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        List<Status> statuses = new ArrayList<>();
        List<LocalDateTime> twitterDates = new ArrayList<>();
        try {
            statuses = twitter.getUserTimeline("liluzivert");

            for (Status s : statuses) {
                Date statusDate = s.getCreatedAt();
                twitterDates.add(statusDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        List<String> twitterDateStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US);
        for (LocalDateTime ldt : twitterDates) {
            twitterDateStrings.add(ldt.format(formatter));
        }
        render(statuses, twitterDateStrings);
    }

}