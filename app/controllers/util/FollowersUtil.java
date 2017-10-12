package controllers.util;

/**
 * Counts followers a person has on each platform and processes each into a condensed String.
 */
public class FollowersUtil {

    public static String processFollowerCount(int followers) {
        String followersStr = String.valueOf(followers);
        if(followers > 1000000) {
            if(followersStr.substring(2, 3).equals("0")) followersStr = followersStr.charAt(0) + "." + followersStr.charAt(1) + "M";
            else followersStr = followersStr.charAt(0) + "." + followersStr.substring(1, 3) + "M";
        }
        return followersStr;
    }

}
