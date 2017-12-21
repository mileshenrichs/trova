package controllers;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import models.Keys;
import org.json.JSONObject;
import play.mvc.Controller;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Henrichs on 12/18/2017.
 * Controller for external authorization (Twitter, Facebook, Instagram)
 */
public class Auth extends Controller {

    /**
     * Authentication flow for sign in/register with Twitter
     */
    public static void authTwitter() {
        final OAuth10aService service = new ServiceBuilder(Keys.twitterConsumer())
                .apiSecret(Keys.twitterConsumerSecret())
                .callback("http://localhost:9000/auth/authtwitter")
                .build(TwitterApi.instance());
        String oauth_token = params.get("oauth_token");
        String oauth_verifier = params.get("oauth_verifier");

        // step 1 of auth process
        if(oauth_token == null && oauth_verifier == null) {
            try {
                // Obtain the Request Token
                final OAuth1RequestToken requestToken = service.getRequestToken();
                redirect(service.getAuthorizationUrl(requestToken));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // auth process complete
        OAuth1RequestToken tokenObj = new OAuth1RequestToken(oauth_token, "");
        // trade request token for an access token
        OAuth1AccessToken accessToken = null;
        try {
            accessToken = service.getAccessToken(tokenObj, oauth_verifier);
            final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
            service.signRequest(accessToken, request);
            final Response res = service.execute(request);
            System.out.println(res.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Accounts.welcomePage();
    }

    /**
     * Authentication flow for sign in/register with Instagram
     */
    public static void authInstagram() {
        String code = params.get("code");
        boolean error = params.get("error") != null;
        if(code != null && !error) {
            // make POST request to obtain access_token
            try {
                URL obj = new URL("https://api.instagram.com/oauth/access_token");
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                String urlParameters = "client_id=" + Keys.instagramClient()
                        + "&client_secret=" + Keys.instagramSecret()
                        + "&grant_type=authorization_code"
                        + "&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fauth%2Fauthinstagram"
                        + "&code=" + code;

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder res = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                System.out.println(res.toString());
                JSONObject json = new JSONObject(res.toString());
                System.out.println(json.getString("access_token"));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        Accounts.welcomePage();
    }

}
