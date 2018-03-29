package app.moov.moov;

/**
 * Created by Lisa on 24/03/18.
 */

public class User {
    private String Username;
    public User() {
    }

    public User(String Username, String UID) {
        this.Username = Username;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }
}
