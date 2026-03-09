package Managers;

import Authentication.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Managers.DbManager.connection;

/**
 * Manages database operations for user data, including loading, adding, and finding users.
 *
 * @author sh_ub
 */
public class DbUsersManager {
    private static final Logger logger = LoggerFactory.getLogger(DbUsersManager.class);

    /**
     * Adds a new user to the database.
     *
     * @param user the {@link User} to add
     * @throws SQLException if an error occurs while adding the user to the database
     * @author sh_ub
     */
    public void addUser(User user) throws SQLException {
        if (user == null || user.getLogin() == null || user.getPasswordHash() == null) {
            logger.error("Attempted to add invalid user: user or credentials are null");
            throw new SQLException("User or credentials cannot be null");
        }

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPasswordHash());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User {} successfully added to the database", user.getLogin());
            } else {
                logger.warn("Failed to add user {} to the database: no rows affected", user.getLogin());
                throw new SQLException("Failed to add user: no rows affected");
            }
        } catch (SQLException e) {
            logger.error("Failed to add user {}: {}", user.getLogin(), e.getMessage());
            throw e;
        }
    }

    /**
     * Finds a user by their username in the database.
     *
     * @param username the username to search for
     * @return the {@link User} object if found, or {@code null} if not found or an error occurs
     * @author sh_ub
     */
    public User findUser(String username) {
        if (username == null || username.isEmpty()) {
            logger.error("Username is null or empty for user search");
            return null;
        }

        String query = "SELECT username, password FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Found user with username {}", username);
                    User user = new User(rs.getString("username"), null);
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find user with username {}: {}", username, e.getMessage());
        }
        logger.debug("User with username {} not found", username);
        return null;
    }
}