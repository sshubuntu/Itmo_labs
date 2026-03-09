package Authentication;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Serializable {
    private final String login;
    private String passwordHash;
    private String type;

    public User(String login, String password) {
        this.login = login;
        passwordHash = password == null ? null : hashPassword(password);
    }

    public User(String login, String password, String type) {
        this.login = login;
        passwordHash = password == null ? null : hashPassword(password);
        this.type = type;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
    }

    public boolean validatePassword(String password) {
        return this.passwordHash.equals(password);
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getType() {
        return type;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }
}