package controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import models.Keys;
import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Controller;

import java.sql.*;

/**
 * Created by Henrichs on 12/2/2017.
 * Controller for user account registration, access, and configuration
 */
public class Accounts extends Controller {

    public static void signIn() {
        render();
    }

    public static void register() {
        render();
    }

    public static void welcomePage() {
        render();
    }

    public static void authenticateSignIn(String user, String pass) {

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

        System.out.println(message.getSid());

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