package controllers.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

            Pattern p = Pattern.compile("https://[a-z]{2}\\.wikipedia\\.org/wiki/");
            Matcher m = p.matcher(link);
            if(m.find() && !handles.keySet().contains("wiki")) { // wiki article
                String wikiContent = getURLBody(link);
                String title = wikiContent.substring(wikiContent.indexOf("<h1 id=\"firstHeading\"") + 53, wikiContent.indexOf("</h1>"));
                if(!title.contains("<i>")) { // <i> indicates album name or similar content, exclude this result
                    handles.put("wiki", link.substring(30));
                }
            }

            else if(link.contains("https://www.instagram.com/") && !handles.keySet().contains("insta")) { // Instagram page
                String insta = link.substring(26);
                if(insta.contains("?hl=")) insta = insta.substring(0, insta.indexOf("?hl=") - 1);
                handles.put("insta", insta);
            }

            else if(link.contains("https://twitter.com/") && !handles.keySet().contains("twitter")) { // twitter page
                String twitter = link.substring(20);
                if(twitter.contains("?lang=")) twitter = twitter.substring(0, twitter.indexOf("?lang="));
                handles.put("twitter", twitter);
            }

            else if (link.contains("https://www.youtube.com/") && !handles.keySet().contains("youtube")) { // YouTube channel
                if (link.substring(24, 29).equals("user/")) {
                    handles.put("youtube", link.substring(29));
                    handles.put("youtubeIdType", "USERNAME");
                } else if (link.substring(24, 32).equals("channel/")) {
                    handles.put("youtube", link.substring(32));
                    handles.put("youtubeIdType", "ID");
                }
            }
        }
        // TODO: handle case where some handles weren't found/handles require disambiguation
        if(handles.keySet().size() < 5) {
            List<String> missingHandles = new ArrayList<>(Arrays.asList("wiki", "insta", "twitter", "youtube"));
            for(int h = 0; h < missingHandles.size(); h++) {
                if(handles.keySet().contains(missingHandles.get(h))) {
                    missingHandles.remove(h);
                    h--;
                }
            }

            for(String missingHandle : missingHandles) {
                boolean foundHandle = false;
                String query = encodedName + "+" + missingHandle;
                searchJSON = getURLBody("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&q=" + query + "&cx=" + SEARCH_ENGINE_ID + "&alt=json");
                resultsObj = new JSONObject(searchJSON);
                items = resultsObj.getJSONArray("items");
                int i = 0;
                while(i < 5 && !foundHandle) {
                    String link = items.getJSONObject(i).getString("link");
                    switch(missingHandle) {
                        case "wiki":
                            Pattern wikiPattern = Pattern.compile("https://[a-z]{2}\\.wikipedia\\.org/wiki/");
                            Matcher wikiMatcher = wikiPattern.matcher(link);
                            if(wikiMatcher.find()) { // wiki article
                                String wikiContent = getURLBody(link);
                                String title = wikiContent.substring(wikiContent.indexOf("<h1 id=\"firstHeading\"") + 53, wikiContent.indexOf("</h1>"));
                                if(!title.contains("<i>")) { // <i> indicates album name or similar content, exclude this result
                                    handles.put("wiki", link.substring(30));
                                    foundHandle = true;
                                }
                            }
                            break;
                        case "insta":
                            if(link.contains("https://www.instagram.com/")) { // Instagram page
                                Pattern p = Pattern.compile("https://www.instagram.com/(.*)/\\?hl=");
                                Matcher m = p.matcher(link);
                                if(m.find()) handles.put("insta", m.group(1));
                                foundHandle = true;
                            }
                            break;
                        case "twitter":
                            if(link.contains("https://twitter.com/")) { // twitter page
                                Pattern p = Pattern.compile("https://twitter.com/(.*)\\?lang=");
                                Matcher m = p.matcher(link);
                                if(m.find()) handles.put("twitter", m.group(1));
                                foundHandle = true;
                            }
                            break;
                        default:
                            if (link.contains("https://www.youtube.com/")) { // YouTube channel
                                if (link.substring(24, 29).equals("user/")) {
                                    handles.put("youtube", link.substring(29));
                                    handles.put("youtubeIdType", "USERNAME");
                                    foundHandle = true;
                                } else if (link.substring(24, 32).equals("channel/")) {
                                    handles.put("youtube", link.substring(32));
                                    handles.put("youtubeIdType", "ID");
                                    foundHandle = true;
                                }
                            }
                    }
                    i++;
                }
            }
        }

        return handles;
    }

}
