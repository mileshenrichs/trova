package models;

import edu.stanford.nlp.simple.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import twitter4j.Twitter;
import twitter4j.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static models.Tweet.getTwitterInstance;

import static util.URLUtil.getURLBody;

/**
 * Represents a single person. Contains data such as unique id, name, and social media handles.
 */
public class Person {
    private long id;
    private String name;
    private String disambiguation;
    private String profileImgUrl;
    private String wikiExcerpt;
    private String wikiHandle;
    private String instaHandle;
    private String twitterHandle;
    private String youtubeHandle;
    private YOUTUBE_ID_TYPE youtubeIdType;

    public Person(long id, String name, String imgUrl, String excerpt, HashMap<String, String> handles) {
        this.id = id;
        this.name = name;
        this.profileImgUrl = imgUrl;
        this.wikiExcerpt = excerpt;
        this.wikiHandle = handles.get("wiki");
        this.instaHandle = handles.get("insta");
        this.twitterHandle = handles.get("twitter");
        this.youtubeHandle = handles.get("youtube");
        if(handles.containsKey("youtubeIdType")) {
            this.youtubeIdType = YOUTUBE_ID_TYPE.valueOf(handles.get("youtubeIdType"));
        }
    }

    /**
     * Type of YouTube identifier associated with Person's channel
     */
    public enum YOUTUBE_ID_TYPE {
        ID,
        USERNAME
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

    public YOUTUBE_ID_TYPE getYoutubeIdType() {
        return youtubeIdType;
    }

    /**
     * Returns String array of handles for a Person (wiki, instagram, twitter, youtube)
     */
    public static HashMap<String, String> findHandles(String name) {
        long startTime = System.nanoTime();

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
            if(m.find() && !link.substring(30).contains("Template") && !handles.keySet().contains("wiki")) { // wiki article
                String wikiContent = getURLBody(link);
                String title = wikiContent.substring(wikiContent.indexOf("<h1 id=\"firstHeading\"") + 53, wikiContent.indexOf("</h1>"));
                if(!title.contains("<i>")) { // <i> indicates album name or similar content, exclude this result
                    handles.put("wiki", link.substring(30));
                }
            }

            else if(link.contains("https://www.instagram.com/") && !handles.keySet().contains("insta")) { // Instagram page
                String insta = link.substring(26);
                if(insta.contains("?hl=")) insta = insta.substring(0, insta.indexOf("?hl=") - 1);
                if(insta.contains("/")) {
                    insta = insta.replace("/", "");
                }
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
                if (missingHandle.equals("youtube")) {
                    final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";
                    String youtubeJSON = getURLBody("https://www.googleapis.com/youtube/v3/search"
                            + "?part=id"
                            + "&q=" + encodedName
                            + "&type=channel"
                            + "&maxResults=3"
                            + "&key=" + YOUTUBE_KEY);
                    JSONObject channelObj = new JSONObject(youtubeJSON);
                    JSONArray arr = channelObj.getJSONArray("items");
                    for(int i = 0; i < arr.length(); i++) {
                        String channelId = arr.getJSONObject(i).getJSONObject("id").getString("channelId");
                        String thisChannel = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + channelId + "&key=" + YOUTUBE_KEY);
                        int subscriberCount = Integer.valueOf(new JSONObject(thisChannel).getJSONArray("items")
                                .getJSONObject(0).getJSONObject("statistics").getString("subscriberCount"));
                        if(subscriberCount >= 50000) {
                            handles.put("youtube", channelId);
                            handles.put("youtubeIdType", "ID");
                        }
                    }
                } else {
                    boolean foundHandle = false;
                    String query = encodedName + "+" + missingHandle;
                    searchJSON = getURLBody("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&q=" + query + "&cx=" + SEARCH_ENGINE_ID + "&alt=json");
                    resultsObj = new JSONObject(searchJSON);
                    items = resultsObj.getJSONArray("items");
                    int i = 0;
                    while (i < 5 && !foundHandle) {
                        String link = items.getJSONObject(i).getString("link");
                        switch (missingHandle) {
                            case "wiki":
                                Pattern wikiPattern = Pattern.compile("https://[a-z]{2}\\.wikipedia\\.org/wiki/");
                                Matcher wikiMatcher = wikiPattern.matcher(link);
                                if (wikiMatcher.find() && !link.substring(30).contains("Template")) { // wiki article
                                    String wikiContent = getURLBody(link);
                                    String title = wikiContent.substring(wikiContent.indexOf("<h1 id=\"firstHeading\"") + 53, wikiContent.indexOf("</h1>"));
                                    if (!title.contains("<i>")) { // <i> indicates album name or similar content, exclude this result
                                        handles.put("wiki", link.substring(30));
                                        foundHandle = true;
                                    }
                                }
                                break;
                            case "insta":
                                if (link.contains("https://www.instagram.com/")) { // Instagram page
                                    String insta = link.substring(26);
                                    if (insta.contains("?hl=")) insta = insta.substring(0, insta.indexOf("?hl=") - 1);
                                    if (insta.contains("/")) {
                                        insta = insta.replace("/", "");
                                    }
                                    handles.put("insta", insta);
                                    foundHandle = true;
                                }
                                break;
                            default:
                                if (link.contains("https://twitter.com/")) { // twitter page
                                    Pattern p = Pattern.compile("https://twitter.com/(.*)\\?lang=");
                                    Matcher m = p.matcher(link);
                                    if (m.find()) handles.put("twitter", m.group(1));
                                    foundHandle = true;
                                }
                        }
                        i++;
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        System.out.println("findHandles(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        return handles;
    }

    public String getInstaFollowers() {
        long startTime = System.nanoTime();
        String instaBody = getURLBody("https://www.instagram.com/" + instaHandle + "/?__a=1");
        JSONObject instaObj = new JSONObject(instaBody);
        int instaFollowers = instaObj.getJSONObject("user").getJSONObject("followed_by").getInt("count");

        long endTime = System.nanoTime();
        System.out.println("getInstaFollowers(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        return processFollowerCount(instaFollowers, 0);
    }

    public String getTwitterFollowers() {
        long startTime = System.nanoTime();
        Twitter twitter = getTwitterInstance();
        User twitterUser = null;
        try {
            twitterUser = twitter.showUser(twitterHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        System.out.println("getTwitterFollowers(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        return processFollowerCount(twitterUser.getFollowersCount(), 0);
    }

    public String getYoutubeFollowers() {
        long startTime = System.nanoTime();
        final String YOUTUBE_KEY = "AIzaSyCMHwtenY0WUR2V5fZGonSYye9g6SoJ0wo";
        String youtubeBody = "";
        if (this.youtubeIdType == YOUTUBE_ID_TYPE.USERNAME) {
            youtubeBody = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&forUsername=" + youtubeHandle + "&key=" + YOUTUBE_KEY);
        } else {
            youtubeBody = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id=" + youtubeHandle + "&key=" + YOUTUBE_KEY);
        }
        JSONObject obj = new JSONObject(youtubeBody);
        JSONArray arr = obj.getJSONArray("items");
        String userID = arr.getJSONObject(0).getString("id");

        youtubeBody = getURLBody("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + userID + "&key=" + YOUTUBE_KEY);
        obj = new JSONObject(youtubeBody);
        arr = obj.getJSONArray("items");
        int youtubeFollowers = arr.getJSONObject(0).getJSONObject("statistics").getInt("subscriberCount");

        long endTime = System.nanoTime();
        System.out.println("getYoutubeFollowers(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
        return processFollowerCount(youtubeFollowers, 0);
    }

    public static String processFollowerCount(int n, int iteration) {
        char[] c = new char[]{'K', 'M'};
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;
        return (d < 1000 ?
                ((d > 99.9 || isRound || (!isRound && d > 9.99)?
                        (int) d * 10 / 10 : d + ""
                ) + "" + c[iteration])
                : processFollowerCount((int) d, iteration + 1));
    }

    /**
     * Sets Person name and wiki excerpt fields by parsing Wikipedia record
     */
    public void retreiveWikiInfo() {
        long startTime = System.nanoTime();
        String wikiBody = getURLBody("https://en.wikipedia.org/wiki/" + wikiHandle);

        // set Person name based on wiki page title (ensures proper spelling & punctuation)
        String title = wikiBody.substring(wikiBody.indexOf("<h1 id=\"firstHeading\"") + 53, wikiBody.indexOf("</h1>"));
        if (title.contains("(")) {
            this.disambiguation = title.substring(title.indexOf("(") + 1, title.indexOf(")"));
            title = title.substring(0, title.indexOf(" ("));
        }
        this.name = title;

        // set wiki excerpt text
        String excerpt = "";
        Pattern p = Pattern.compile("vcard(.*?)</table>(.*?)<div id=\"toc\" class=\"toc\">", Pattern.DOTALL); // DOTALL matches line terminators
        Matcher m = p.matcher(wikiBody);
        if (m.find()) {
            // search again in case of multiple vcards
            String bodyAfterVcard = wikiBody.substring(m.start() + 7);
            Pattern secondP = Pattern.compile("vcard(.*?)</table>(.*?)</table>(.*?)<div id=\"toc\" class=\"toc\">", Pattern.DOTALL);
            Matcher secondM = secondP.matcher(bodyAfterVcard);
            if(secondM.find()) {
                excerpt = secondM.group(3).substring(1);
            } else {
                excerpt = m.group(2).substring(1); // substring removes '\n' from start
            }
        }
        excerpt = Jsoup.parse(excerpt).text().replaceAll("\\[(\\d+)]", ""); // remove HTML tags using Jsoup
        this.wikiExcerpt = trimExcerpt(excerpt);
        long endTime = System.nanoTime();
        System.out.println("retreiveWikiInfo(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
    }

    /**
     * Sets Person profile pic URL by performing Bing image search
     */
    public void findProfilePic() {
        long startTime = System.nanoTime();
        String encodedName = "";
        String urlStr;
        URL url = null;
        try {
            encodedName = URLEncoder.encode(this.name, "UTF-8");
            urlStr = "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q=" + encodedName;
            if(this.disambiguation != null) urlStr += "+" + URLEncoder.encode(this.disambiguation, "UTF-8");
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<String> possibleImgUrls = getImageUrls(url);

        if(possibleImgUrls.size() == 0) {
            urlStr = "https://api.cognitive.microsoft.com/bing/v7.0/images/search"
                    + "?aspect=Square" + "&q=" + encodedName;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            possibleImgUrls = getImageUrls(url);
        }
        if(possibleImgUrls.size() == 1) {
            this.profileImgUrl = possibleImgUrls.get(0);
        } else {
            // select random profile image option from possible choices
            this.profileImgUrl = possibleImgUrls.get((int) Math.floor(Math.random() * (possibleImgUrls.size() - 1) + 1));
        }
        long endTime = System.nanoTime();
        System.out.println("findProfilePic(): " + ((endTime - startTime) / 1000000000.0) + "seconds");
    }

    public static List<String> getImageUrls(URL url) {
        final String SUBSCRIPTION_KEY = "7c05d6c68ffb4213bde88bb1b8aca677";
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

        // receive JSON body
        InputStream stream = null;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = new Scanner(stream).useDelimiter("\\A").next();
        if(response.contains("queryExpansions")) {
            response = response.substring(0, response.indexOf("queryExpansions") - 3) + "}";
        }

        List<String> possibleImgUrls = new ArrayList<>();

        JSONObject resultsObj = new JSONObject(response);
        JSONArray imageResults = resultsObj.getJSONArray("value");

        for(int i = 0; i < imageResults.length(); i++) {
            int width = imageResults.getJSONObject(i).getInt("width");
            int height = imageResults.getJSONObject(i).getInt("height");
            double difference = Math.abs(width - height);
            double avg = (width + height) / 2;
            double percentDifference = difference / avg;
            // consider image if is square within 10% margin and is large enough
            if(percentDifference <= .1 && Math.min(width, height) > 230) {
                String imgUrl = imageResults.getJSONObject(i).getString("contentUrl");

                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con = (HttpURLConnection) new URL(imgUrl).openConnection();
                    con.setRequestMethod("HEAD");
                    // ensure image still exists
                    if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        possibleImgUrls.add(imgUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(possibleImgUrls.size() == 4) break;
        }
        return possibleImgUrls;
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
