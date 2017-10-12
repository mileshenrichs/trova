package controllers.util;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLConnection;

/**
 * Helper method to parse web URL contents to a String
 */
public class URLUtil {

    public static String getURLBody(URL webUrl) {
        String body = "";
        URL url = null;
        try {
            url = webUrl;
            URLConnection connection = url.openConnection();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            body = IOUtils.toString(url, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

}
