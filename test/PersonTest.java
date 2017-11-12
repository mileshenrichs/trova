import models.Person;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Henrichs on 10/27/2017.
 * Tests constructor and operations of Person class
 */
public class PersonTest extends UnitTest {

    Person person;
    Person person2;
    String excerpt;

    @Before
    public void createPerson() {
        HashMap<String, String> handles = new HashMap<>();
        handles.put("wiki", "Lil_Uzi_Vert");
        handles.put("insta", "liluzivert");
        handles.put("twitter", "liluzivert");
        handles.put("youtube", "LILUZIVERT");
        HashMap<String, String> handles2 = new HashMap<>();
        handles2.put("wiki", "Future_(rapper)");
        handles2.put("insta", "liluzivert");
        handles2.put("twitter", "liluzivert");
        handles2.put("youtube", "LILUZIVERT");
        person = new Person(1, "Lil Uzi Vert", "imgUrl", "excerpt", handles);
        person2 = new Person(2, "future", "imgUrl", "excerpt", handles2);
        excerpt = "";
    }

    @Test
    public void retreiveWikiInfoTest() {
        System.out.println("Before: " + person2.getWikiExcerpt());
        person2.retreiveWikiInfo();
        System.out.println("\n");
        System.out.println("After: " + person2.getWikiExcerpt());
        excerpt = person2.getWikiExcerpt();
    }

//    @Test
//    public void findProfilePicTest() {
//        HashMap<String, String> handles = new HashMap<>();
//        handles.put("wiki", "Lil_Uzi_Vert");
//        handles.put("insta", "liluzivert");
//        handles.put("twitter", "liluzivert");
//        handles.put("youtube", "LILUZIVERT");
//        person = new Person(1, "Lil Uzi Vert", "imgUrl", "excerpt", handles);
//
//        final String SUBSCRIPTION_KEY = "7c05d6c68ffb4213bde88bb1b8aca677";
//
//        String urlStr = "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q=" + URLEncoder.encode(person.name, "UTF-8");
//        URL url = null;
//        try {
//            url = new URL(urlStr);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpsURLConnection connection = null;
//        try {
//            connection = (HttpsURLConnection) url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        connection.setRequestProperty("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
//
//        // receive JSON body
//        InputStream stream = null;
//        try {
//            stream = connection.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String response = new Scanner(stream).useDelimiter("\\A").next();
//        response = response.substring(0, response.indexOf("queryExpansions") - 3) + "}";
//
//        List<String> possibleImgUrls = new ArrayList<>();
//
//        JSONObject resultsObj = new JSONObject(response);
//        JSONArray imageResults = resultsObj.getJSONArray("value");
//        for(int i = 0; i < imageResults.length(); i++) {
//            int width = imageResults.getJSONObject(i).getInt("width");
//            int height = imageResults.getJSONObject(i).getInt("height");
//            double difference = Math.abs(width - height);
//            double avg = (width + height) / 2;
//            double percentDifference = difference / avg;
//            if(percentDifference <= .1) {
//                possibleImgUrls.add(imageResults.getJSONObject(i).getString("contentUrl"));
//            }
//            if(possibleImgUrls.size() == 4) break;
//        }
//        person.profileImgUrl = possibleImgUrls.get((int) Math.floor(Math.random() * possibleImgUrls.size()));
//    }

}
