import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class BasicTest extends UnitTest {

    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void twitter4jTest() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("t3Hs7gFXcPG6IOAhqtX0rlbF1")
                .setOAuthConsumerSecret("b91yBAOgBVI0QGGbh6rvUkCg5oN8oRWlBAc2ali4zfXUlay8xh")
                .setOAuthAccessToken("758838713287712769-syai0cnNb7gglwaLeOFChvGv4onWVqS")
                .setOAuthAccessTokenSecret("fgmnfbYdR3p9rtaFPKtLMB5Udh3ve2bKcyHoO3jkyBwAq");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            List<Status> statuses = twitter.getUserTimeline("liluzivert");

            for (Status s : statuses) {
                System.out.println("@" + s.getUser().getScreenName() + ": " + s.getText());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        assertEquals(3+7, 10);

    }

}
