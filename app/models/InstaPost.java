package models;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * An instagram post.
 */

public class InstaPost {

    public String instaEmbedHTML;

    public InstaPost(String html) {
        this.instaEmbedHTML = html;
    }

    public String getHTML() {
        return this.instaEmbedHTML;
    }

    public static List<InstaPost> getInstaPosts(String username) {
        List<InstaPost> instaPosts = new ArrayList<>();

        String instaBody = "";
        URL instaURL = null;

        try {
            instaURL = new URL("https://www.instagram.com/" + username + "/media/");
            URLConnection urlConnection = instaURL.openConnection();
            String encoding = urlConnection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            instaBody = IOUtils.toString(instaURL, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject instaObj = new JSONObject(instaBody);
        JSONArray instaArr = instaObj.getJSONArray("items");

        for(int i = 0; i < instaArr.length(); i++) {
            String postID = instaArr.getJSONObject(i).getString("code");

            try {
                instaURL = new URL("https://api.instagram.com/oembed/?url=http://instagr.am/p/" + postID + "/");
                URLConnection urlConnection = instaURL.openConnection();
                String encoding = urlConnection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                instaBody = IOUtils.toString(instaURL, encoding);
            } catch (Exception e) {
                e.printStackTrace();
            }

            instaObj = new JSONObject(instaBody);
            String embedHTML = instaObj.getString("html");

            embedHTML = embedHTML.substring(0, embedHTML.length() - 76);
            embedHTML = embedHTML.replace("max-width:658px;", "max-width:350px;");
            instaPosts.add(new InstaPost(embedHTML));
        }

        return instaPosts;
    }

}
