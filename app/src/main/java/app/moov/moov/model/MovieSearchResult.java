package app.moov.moov.model;

/**
 * Created by Lisa on 29/03/18.
 */

public class MovieSearchResult {
    private String MovieTitle;
    private String Year;
    private int Thumbnail;

    public MovieSearchResult() {
    }

    public MovieSearchResult(String movieTitle, String year, int thumbnail) {
        this.MovieTitle = movieTitle;
        this.Year = year;
        this.Thumbnail = thumbnail;
    }

    public String getMovieTitle() {
        return MovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        MovieTitle = movieTitle;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
