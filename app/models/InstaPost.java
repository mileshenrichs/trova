package models;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static controllers.util.URLUtil.getURLBody;

/**
 * An instagram post.
 */

public class InstaPost {

    public String instaEmbedHTML;
    public LocalDateTime instaPostDate;

    public InstaPost(String html, LocalDateTime date) {
        this.instaEmbedHTML = html;
        this.instaPostDate = date;
    }

    public String getHTML() {
        return this.instaEmbedHTML;
    }

    public LocalDateTime getPostDate() {
        return this.instaPostDate;
    }

    public static List<InstaPost> getInstaPosts(String username) {
        List<InstaPost> instaPosts = new ArrayList<>();

        String instaBody = getURLBody("https://www.instagram.com/" + username + "/media");

        JSONObject instaObj = new JSONObject(instaBody);
        JSONArray instaArr = instaObj.getJSONArray("items");

        for(int i = 0; i < instaArr.length(); i++) {
            String postID = instaArr.getJSONObject(i).getString("code");
            instaBody = getURLBody("https://api.instagram.com/oembed/?url=http://instagr.am/p/" + postID + "/");

            instaObj = new JSONObject(instaBody);
            String embedHTML = instaObj.getString("html");

            int dateIndex = embedHTML.indexOf("datetime");
            String dateString = embedHTML.substring(dateIndex + 10, dateIndex + 35);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            embedHTML = embedHTML.substring(0, embedHTML.length() - 76);
            embedHTML = embedHTML.replace("max-width:658px;", "max-width:350px;");
            instaPosts.add(new InstaPost(embedHTML, LocalDateTime.parse(dateString, formatter)));
        }

        return instaPosts;
    }

}
