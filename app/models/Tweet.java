package models;

import org.apache.commons.lang.StringUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controllers.util.URLUtil.getURLBody;

/**
 * A twitter status.
 */

public class Tweet {
    public Status status;
    public LocalDateTime postDate;
    public ATTACHMENT_RATIO ratio;

    public Tweet(Status status, LocalDateTime date, ATTACHMENT_RATIO ratio) {
        this.status = status;
        this.postDate = date;
        this.ratio = ratio;
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

    public ATTACHMENT_RATIO getAttachmentRatio() {
        return this.ratio;
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
                tweets.add(new Tweet(s, ldt, checkHasAttachment(s)));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    /**
     * Defines whether a Tweet has an image or video attachment
     */
    public enum ATTACHMENT_RATIO {
        NONE,
        TALL,
        WIDE
    }

    public static ATTACHMENT_RATIO checkHasAttachment(Status s) {
        int width = 0, height = 0;
        if(s.getText().substring(0, 2).equals("RT")) s = s.getRetweetedStatus();

        String urlString = "";
        Pattern p = Pattern.compile("https://t.co/(.*?)\\s");
        Matcher m = p.matcher(s.getText() + " ");
        while(m.find()) {
            urlString += m.group();
        }

        if(!urlString.equals("")) {
            // links to YouTube video, always wide
            if (StringUtils.countMatches(urlString, "t.co") > 1) return ATTACHMENT_RATIO.WIDE;
            else {
                String body = null;
                try {
                    body = getURLBody(new URL(urlString));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(body != null) {
                    Pattern widthPattern = Pattern.compile("property=\"og:video:width\" content=\"(\\d*?)\"");
                    Pattern heightPattern = Pattern.compile("property=\"og:video:height\" content=\"(\\d*?)\"");
                    Matcher widthMatcher = widthPattern.matcher(body);
                    Matcher heightMatcher = heightPattern.matcher(body);

                    if(widthMatcher.find() && heightMatcher.find()) {
                        width = Integer.parseInt(widthMatcher.group(1));
                        height = Integer.parseInt(heightMatcher.group(1));
                    } else {
                        Pattern imagePattern = Pattern.compile("property=\"og:image\" content=\"https://(.*?):(.*?)\"");
                        Matcher imageMatcher = imagePattern.matcher(body);
                        if(imageMatcher.find()) {
                            URL imgURL = null;
                            try {
                                imgURL = new URL("https://" + imageMatcher.group(2));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(imgURL != null) {
                                Image img = new ImageIcon(imgURL).getImage();
                                width = img.getWidth(null);
                                height = img.getHeight(null);
                            }
                        }
                    }
                }
            }
        }

        if (width != 0 && height != 0) {
            if (width > height) return ATTACHMENT_RATIO.WIDE;
            else return ATTACHMENT_RATIO.TALL;
        }
        return ATTACHMENT_RATIO.NONE;
    }

}
