package app.moov.moov.model;

/**
 * Created by Lisa on 27/02/18.
 * Last Modified by Sammy on 3/3/2018.
 * Defines what goes into a post on the feed
 */

public class Post {
    private String uid, movieTitle, movieRating, movieReview, username, pid, posterURL;

    private long time;

    private int movieID;

    public Post() {

    }

    public Post(String username, String uid, String title, String rating, String review, long time, String pid, int movieID, String posterURL) {
        this.uid = uid;
        this.movieTitle = title;
        this.movieRating = rating;
        this.movieReview = review;
        this.time = time;
        this.username = username;
        this.pid = pid;
        this.movieID = movieID;
        this.posterURL = posterURL;
    }

    public String getUsername() { return username; }

    public void setUsername(String newU) { this.username = newU; }

    public long getTime() { return time; }

    public void setTime(long time) { this.time = time; }

    public String getUID() { return uid; }

    public void setUID(String uid) {this.uid = uid;}

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieReview() {
        return movieReview;
    }

    public void setMovieReview(String movieReview) {
        this.movieReview = movieReview;
    }

    public String getPID() { return pid; }

    public void setPID(String pid) { this.pid = pid; }

    public int getMovieID() { return movieID; }

    public void setMovieID(int movieID) { this.movieID = movieID; }

    public String getPosterURL() { return posterURL; }

    public void setPosterURL(String posterURL) { this.posterURL = posterURL; }
}
