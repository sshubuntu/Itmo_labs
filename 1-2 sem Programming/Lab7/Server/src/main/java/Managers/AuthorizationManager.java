package Managers;

import Authentication.User;
import Utility.ExecutionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Manages user authorization and registration processes, interacting with the database to validate and store user credentials.
 * @author sh_ub
 */
public class AuthorizationManager {
    public static final Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);
    private static final DbUsersManager dbUsersManager = new DbUsersManager();

    /**
     * Registers a new user with the provided credentials.
     *
     * @param user the user to register
     * @return an {@link ExecutionResponse} indicating success or failure of the registration
     * @author sh_ub
     */
    public static ExecutionResponse register(User user) {
        if (user == null) {
            logger.error("Attempted to register null user");
            return new ExecutionResponse("User cannot be null", false);
        }

        String login = user.getLogin();

        if (isAuthorize(login)) {
            logger.info("User {} already exists, attempting login", login);
            return login(user);
        }

        try {
            dbUsersManager.addUser(user);
            logger.info("User {} successfully registered", login);
            return new ExecutionResponse(String.format("User %s successfully registered", login), true);
        } catch (SQLException e) {
            logger.error("Failed to register user {}: {}", login, e.getMessage());
            return new ExecutionResponse(String.format("Failed to register user %s. Please retry later.", login), false);
        }
    }

    /**
     * Authenticates a user by verifying their login and password.
     *
     * @param user user, that must be log in
     * @return an {@link ExecutionResponse} indicating success or failure of the login attempt
     * @author sh_ub
     */
    public static ExecutionResponse login(User user) {
        if (dbUsersManager.findUser(user.getLogin()).validatePassword(user.getPasswordHash())) {
            logger.info("User {} successfully logged in", user.getLogin());
            return new ExecutionResponse(String.format("User %s successfully logged in", user.getLogin()), true);
        } else {
            logger.warn("Invalid password for user {}", user.getLogin());
            return new ExecutionResponse("Invalid password for user "+user.getLogin(), false);
        }
    }

    /**
     * Checks if a user with the specified login exists.
     *
     * @param login the login to check
     * @return {@code true} if the user exists, {@code false} otherwise
     * @author sh_ub
     */
    public static boolean isAuthorize(String login) {
        boolean existingUser = dbUsersManager.findUser(login) != null;
        logger.debug(existingUser ? "User {} exists" : "User {} not found", login);
        return existingUser;
    }
}