package models;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A twitter status.
 */
public class Tweet {
    public Status status;
    public LocalDateTime postDate;

    public Tweet(Status status, LocalDateTime date) {
        this.status = status;
        this.postDate = date;
    }

    public Status getStatus() {
        return this.status;
    }

    public LocalDateTime getTweetDate() {
        return this.postDate;
    }

    public String getDisplayDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return postDate.format(formatter);
    }

    public static List<Tweet> getTweets(String username) {
        List<Tweet> tweets = new ArrayList<>();

        Twitter twitter = getTwitterInstance();

        List<Status> statuses;
        try {
            statuses = twitter.getUserTimeline(username);

            for (Status s : statuses) {
                Date statusDate = s.getCreatedAt();
                LocalDateTime ldt = statusDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                tweets.add(new Tweet(s, ldt));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    public static Twitter getTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("t3Hs7gFXcPG6IOAhqtX0rlbF1")
                .setOAuthConsumerSecret("b91yBAOgBVI0QGGbh6rvUkCg5oN8oRWlBAc2ali4zfXUlay8xh")
                .setOAuthAccessToken("758838713287712769-syai0cnNb7gglwaLeOFChvGv4onWVqS")
                .setOAuthAccessTokenSecret("fgmnfbYdR3p9rtaFPKtLMB5Udh3ve2bKcyHoO3jkyBwAq");

        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    /**
     * Return any https://t.co links contained in a Status
     */
    public static String getTco(String t) {
        String urlString = "";
        Pattern p = Pattern.compile("https://t.co/(.*?)\\s");
        Matcher m = p.matcher(t + " ");
        while(m.find()) {
            urlString += m.group();
        }
        return urlString;
    }

    /**
     * Returns base (original) Tweet if tweet is a retweet
     */
    public Tweet getBaseTweet() {
        if(status.getText().substring(0, 2).equals("RT")) {
            Status s = status.getRetweetedStatus();
            Date statusDate = s.getCreatedAt();
            LocalDateTime ldt = statusDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return(new Tweet(s, ldt));
        }
        return this;
    }

}
