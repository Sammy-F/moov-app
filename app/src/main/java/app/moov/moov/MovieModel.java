package app.moov.moov;

/**
 * Created by Sammy on 3/29/2018.
 */

public class MovieModel {
    String movieTitle;
    String movieID;
    String movieSummary;
    String posterURL;

    public MovieModel() {}

    public MovieModel(String movieTitle, String movieID, String movieSummary, String posterURL) {

        this.movieTitle = movieTitle;
        this.movieID = movieID;
        this.movieSummary = movieSummary;
        this.posterURL = posterURL;

    }

    public String getMovieTitle() { return movieTitle; }
    public String getMovieID() { return movieID; }
    public String getMovieSummary() { return movieSummary; }
    public  String getPosterURL() { return posterURL; }

    public void setMovieTitle(String newMovieTitle) { movieTitle = newMovieTitle; }
    public void setMovieID(String newMovieID) { movieID = newMovieID; }
    public void setMovieSummary(String newMovieSummary) { movieSummary = newMovieSummary; }
    public void setPosterURL(String newPosterURl) { posterURL = newPosterURl; }
}
