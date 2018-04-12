package app.moov.moov.model;

/**
 * Created by Lisa on 24/03/18.
 */

public class User {
    private String Username;
    private String lowername;
    private String FirstName;
    private String LastName;
    public User() {
    }

    public User(String lowername, String Username, String UID, String FirstName, String LastName) {
        this.lowername = lowername;
        this.Username = Username;
        this.FirstName = FirstName;
        this.LastName = LastName;
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

    public String getFirstName() { return FirstName; }

    public void setFirstName(String FirstName) { this.FirstName = FirstName; }

    public String getLastName() { return LastName; }

    public void setLastName(String LastName) { this.LastName = LastName; }
}
