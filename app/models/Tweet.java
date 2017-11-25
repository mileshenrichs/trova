package models;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                .setOAuthConsumerKey(Keys.twitterConsumer())
                .setOAuthConsumerSecret(Keys.twitterConsumerSecret())
                .setOAuthAccessToken(Keys.twitterAccess())
                .setOAuthAccessTokenSecret(Keys.twitterAccessSecret());

        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

}
