package controllers;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import models.Keys;
import play.mvc.Controller;

/**
 * Created by Henrichs on 12/18/2017.
 * Controller for external authorization (Twitter, Facebook, Instagram)
 */
public class Auth extends Controller {

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

}
