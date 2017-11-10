package models;

import edu.stanford.nlp.simple.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import twitter4j.Twitter;
import twitter4j.User;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static models.Tweet.getTwitterInstance;

import static controllers.util.URLUtil.getURLBody;
import static controllers.util.PersonInfoUtil.processFollowerCount;

/**
 * Represents a single person. Contains data such as unique id, name, and social media handles.
 */
public class Person {
    private long id;
    private String name;
    private String profileImgUrl;
    private String wikiExcerpt;
    private String wikiHandle;
    private String instaHandle;
    private String twitterHandle;
    private String youtubeHandle;

    public Person(long id, String name, String imgUrl, String excerpt, HashMap<String, String> handles) {
        this.id = id;
        this.name = name;
        this.profileImgUrl = imgUrl;
        this.wikiExcerpt = excerpt;
        this.wikiHandle = handles.get("wiki");
        this.instaHandle = handles.get("insta");
        this.twitterHandle = handles.get("twitter");
        this.youtubeHandle = handles.get("youtube");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public String getWikiExcerpt() {
        return wikiExcerpt;
    }

    public String getWikiHandle() {
        return wikiHandle;
    }

    public String getInstaHandle() {
        return instaHandle;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public String getYoutubeHandle() {
        return youtubeHandle;
    }

    public String getInstaFollowers() {
        String instaBody = getURLBody("https://www.instagram.com/" + instaHandle + "/?__a=1");
        JSONObject instaObj = new JSONObject(instaBody);
        int instaFollowers = instaObj.getJSONObject("user").getJSONObject("followed_by").getInt("count");

        return processFollowerCount(instaFollowers);
    }

    public String getTwitterFollowers() {
        Twitter twitter = getTwitterInstance();
        User twitterUser = null;
        try {
            twitterUser = twitter.showUser(twitterHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processFollowerCount(twitterUser.getFollowersCount());
    }

    public String getYoutubeFollowers() {
        final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";
        String youtubeBody = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + youtubeHandle + "&key=" + YOUTUBE_KEY);
        JSONObject obj = new JSONObject(youtubeBody);
        JSONArray arr = obj.getJSONArray("items");
        String userID = arr.getJSONObject(0).getString("id");

        youtubeBody = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + userID + "&key=" + YOUTUBE_KEY);
        obj = new JSONObject(youtubeBody);
        arr = obj.getJSONArray("items");
        int youtubeFollowers = arr.getJSONObject(0).getJSONObject("statistics").getInt("subscriberCount");

        return processFollowerCount(youtubeFollowers);
    }

    /**
     * Sets Person name and wiki excerpt fields by parsing Wikipedia record
     */
    public void retreiveWikiInfo() {
        String wikiBody = getURLBody("https://en.wikipedia.org/wiki/" + wikiHandle);

        // set Person name based on wiki page title (ensures proper spelling & punctuation)
        String title = wikiBody.substring(wikiBody.indexOf("<h1 id=\"firstHeading\"") + 53, wikiBody.indexOf("</h1>"));
        if (title.contains("(")) title = title.substring(0, title.indexOf(" ("));
        this.name = title;

        // set wiki excerpt text
        String excerpt = "";
        Pattern p = Pattern.compile("</table>(.*?)<div id=\"toc\" class=\"toc\">", Pattern.DOTALL); // DOTALL matches line terminators
        Matcher m = p.matcher(wikiBody);
        if (m.find()) excerpt = m.group(1).substring(1); // substring removes '\n' from start
        excerpt = Jsoup.parse(excerpt).text().replaceAll("\\[(\\d+)]", ""); // remove HTML tags using Jsoup
        this.wikiExcerpt = trimExcerpt(excerpt);
    }

    /**
     * Shortens wiki excerpt to fit page template
     */
    public static String trimExcerpt(String excerpt) {
        final int MAX_CHARS = 710;
        int lastSentenceIndex = 0;
        List<Integer> sentenceEndIndeces = getSentenceEndIndices(excerpt);
        if(sentenceEndIndeces.get(sentenceEndIndeces.size() - 1) > MAX_CHARS) { // last sentence ends beyond max excerpt length
            for(int i = sentenceEndIndeces.size() - 2; i > 0; i--) { // loop backwards from 2nd last sentence end index
                if(sentenceEndIndeces.get(i) < MAX_CHARS) {
                    lastSentenceIndex = sentenceEndIndeces.get(i);
                    break;
                }
            }
        }
        if(lastSentenceIndex == 0) return excerpt;
        else return excerpt.substring(0, lastSentenceIndex);
    }

    /**
     * Returns list of integers which represent the indeces within the excerpt for
     * the ending of each sentence
     */
    public static List<Integer> getSentenceEndIndices(String excerpt) {
        List<Integer> indices = new ArrayList<>();
        Document doc = new Document(excerpt);
        for (Sentence sentence : doc.sentences()) {
            indices.add(sentence.characterOffsetEnd(sentence.length() - 1));
        }
        return indices;
    }
}