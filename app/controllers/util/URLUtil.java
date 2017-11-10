package controllers.util;

import org.apache.commons.io.IOUtils;
import java.net.URL;
import java.net.URLConnection;

/**
 * Helper method to parse web URL contents to a String
 */
public class URLUtil {

    public static String getURLBody(String webUrl) {
        String body = "";
        URL url = null;
        try {
            url = new URL(webUrl);
            URLConnection connection = url.openConnection();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(url, encoding);
        } catch (Exception e) {
            System.out.println("Probably FileNotFound.");
        }
        return body;
    }

    /**
     * Type of webpage https://t.co/ link points to
     */
    public enum TCO_TYPE {
        PAGE_WITH_CARD,
        PAGE,
        IMAGE,
        VIDEO,
        YOUTUBE,
        TWEET
    }

    /**
     * Returns TCO_TYPE based on content which t.co link(s) in a Status point to
     */
    public static TCO_TYPE checkTco(String tco) {
        String lastTco = tco.substring(tco.lastIndexOf("https://t.co"));
        String body = getURLBody(tco);

        if(body.contains("property=\"twitter:card\" content=\"summary\"")) return TCO_TYPE.PAGE_WITH_CARD;
        else if(body.contains("<div id=\"player-api\" class=\"player-width player-height off-screen-target player-api\"></div>")) return TCO_TYPE.YOUTUBE;
        else if(!body.contains("<noscript><meta http-equiv=\"refresh\" content=\"0; URL=https://mobile.twitter.com")) return TCO_TYPE.PAGE;
        else if(body.contains("property =\"og:video:width\"")) return TCO_TYPE.VIDEO;
        else if(body.contains("property=\"og:image:user_generated\" content=\"true\"")) return TCO_TYPE.IMAGE;
        else return TCO_TYPE.TWEET;
        }
    }
