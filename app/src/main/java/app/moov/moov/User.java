package app.moov.moov;

/**
 * Created by Lisa on 24/03/18.
 */

public class User {
    private String Username;
    private String UID;
    public User() {
    }

    public User(String Username) {
        this.Username = Username;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
