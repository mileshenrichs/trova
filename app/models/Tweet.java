package models;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A twitter post.
 */

public class Tweet {
    public String twitterPostId;
    public LocalDateTime postDate;

    public Tweet(String id, LocalDateTime date) {
        this.twitterPostId = id;
        this.postDate = date;
    }

    public String getTweetId() {
        return this.twitterPostId;
    }

    public LocalDateTime getTweetDate() {
        return this.postDate;
    }

    public static List<Tweet> getTweets(String username) {
        List<Tweet> tweets = new ArrayList<>();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("t3Hs7gFXcPG6IOAhqtX0rlbF1")
                .setOAuthConsumerSecret("b91yBAOgBVI0QGGbh6rvUkCg5oN8oRWlBAc2ali4zfXUlay8xh")
                .setOAuthAccessToken("758838713287712769-syai0cnNb7gglwaLeOFChvGv4onWVqS")
                .setOAuthAccessTokenSecret("fgmnfbYdR3p9rtaFPKtLMB5Udh3ve2bKcyHoO3jkyBwAq");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        List<Status> statuses;
        try {
            statuses = twitter.getUserTimeline(username);

            for (Status s : statuses) {
                Date statusDate = s.getCreatedAt();
                LocalDateTime ldt = statusDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                tweets.add(new Tweet(String.valueOf(s.getId()), ldt));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return tweets;
    }

}
