package controllers.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static controllers.util.URLUtil.getURLBody;

/**
 * Counts followers a person has on each platform and processes each into a condensed String.
 */
public class PersonInfoUtil {

    public static String processFollowerCount(int followers) {
        String followersStr = String.valueOf(followers);
        if(followers > 1000000) {
            if(followersStr.substring(2, 3).equals("0")) followersStr = followersStr.charAt(0) + "." + followersStr.charAt(1) + "M";
            else followersStr = followersStr.charAt(0) + "." + followersStr.substring(1, 3) + "M";
        }
        return followersStr;
    }

    /**
     * Returns String array of handles for a Person (wiki, instagram, twitter, youtube)
     */
    public static HashMap<String, String> findHandles(String name) {

        final String API_KEY = "AIzaSyCcwKvKOFc-feF-CHKnI24M6XY545AywE0";
        final String SEARCH_ENGINE_ID = "015057633729182102453:nujiwevepbo";

        HashMap<String, String> handles = new HashMap<>();

        String encodedName = name.toLowerCase();
        try {
            encodedName = URLEncoder.encode(encodedName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String searchJSON = getURLBody("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&q=" + encodedName + "&cx=" + SEARCH_ENGINE_ID + "&alt=json");

        JSONObject resultsObj = new JSONObject(searchJSON);
        JSONArray items = resultsObj.getJSONArray("items");
        // loop through first 10 search results to extract social media handles for given Person name
        for(int i = 0; i < 10; i++) {
            String link = items.getJSONObject(i).getString("link");
            if(link.contains("https://en.wikipedia.org/wiki/")) { // wiki article
                handles.put("wiki", link.substring(30));
            } else if(link.contains("https://www.instagram.com/")) { // Instagram page
                Pattern p = Pattern.compile("https://www.instagram.com/(.*)/\\?hl=");
                Matcher m = p.matcher(link);
                if(m.find()) handles.put("insta", m.group(1));
            } else if(link.contains("https://twitter.com/")) { // twitter page
                Pattern p = Pattern.compile("https://twitter.com/(.*)\\?lang=");
                Matcher m = p.matcher(link);
                if(m.find()) handles.put("twitter", m.group(1));
            } else if(link.contains("https://www.youtube.com/user/")) { // YouTube page
                handles.put("youtube", link.substring(29));
            }
        }
        // TODO: handle case where some handles weren't found/handles require disambiguation

        return handles;
    }

}
