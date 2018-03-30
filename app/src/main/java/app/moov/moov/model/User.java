package app.moov.moov.model;

/**
 * Created by Lisa on 24/03/18.
 */

public class User {
    private String Username;
    private String lowername;
    public User() {
    }

    public User(String lowername, String Username, String UID) {
        this.lowername = lowername;
        this.Username = Username;
    }

    public String getlowername() {
        return lowername;
    }

    public void setlowername(String lowername) {
        this.lowername = lowername;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }
}
