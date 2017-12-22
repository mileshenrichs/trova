package controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import models.Keys;
import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Controller;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Henrichs on 12/2/2017.
 * Controller for user account registration, access, and configuration
 */
public class Accounts extends Controller {

    public static void signIn() {
        boolean fromRegister = params.get("fromRegister") == null;
        render(fromRegister);
    }

    /**
     * Full-screen login page displayed when incorrect login credentials are provided
     * @param err String describing type of error (username, email, or pass)
     * @param u String containing valid username/email if applicable
     */
    public static void invalidSignIn(String err, String u) {
        render(err, u);
    }

    public static void register() {
        render();
    }

    public static void welcomePage() {
        render();
    }

    public static void authenticateSignIn(String user, String pass) {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(user);
        String idType = emailMatcher.find() ? "email_address" : "username";

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trova?verifyServerCertificate=false&useSSL=true", Keys.dbUsername(), Keys.dbPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE " + idType + " = ? LIMIT 1");
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            boolean validLogin = false;
            // username or email exists in DB
            if(results.next()) {
                validLogin = BCrypt.checkpw(pass, results.getString("password"));
                if(validLogin) {
                    // user is authenticated
                    welcomePage();
                } else {
                    invalidSignIn("pass", user);
                }
            } else {
                // username or email non-existent
                invalidSignIn(idType, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createAccount(String emailAddress, String username, String password) {
        // Hash password
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Store new user in DB
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trova?verifyServerCertificate=false&useSSL=true", Keys.dbUsername(), Keys.dbPassword());
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, email_address, password) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, emailAddress);
            statement.setString(3, hash);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Twilio.init(Keys.twilioAcct(), Keys.twilioAuth());
        Message message = Message
                .creator(new PhoneNumber(Keys.myPhone()), new PhoneNumber(Keys.twilioNumber()),
                        "New trova user: " + username + "!").create();
        welcomePage();
    }

    /**
     * API endpoint to check if given email address/username already exists in users DB
     */
    public static void checkUsersDB() {
        boolean entryExists = false;
        String value = params.get("value");
        String type = params.get("type");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trova?verifyServerCertificate=false&useSSL=true", Keys.dbUsername(), Keys.dbPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE " + type + " = ?");
            statement.setString(1, value);
            ResultSet results = statement.executeQuery();
            if(results.next()) {
                entryExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        renderJSON("{\"entryExists\": " + entryExists + "}");
    }

}