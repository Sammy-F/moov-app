package app.moov.moov;

/**
 * Created by Lisa on 27/02/18.
 */

public class Post {
    private String movieTitle, movieRating, movieReview;

    public Post() {

    }

    public Post(String title, String rating, String review) {
        this.movieTitle = title;
        this.movieRating = rating;
        this.movieReview = review;
    }

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
}
