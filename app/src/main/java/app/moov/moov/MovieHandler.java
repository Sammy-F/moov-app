package app.moov.moov;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alonaria on 3/19/2018.
 */

public class MovieHandler {
    private String searchQuery;
    private int movieID;
    private final String apiKey = "3744632a440f06514578b01d1b6e9d27";
    URL searchURL;

    //Instantiate a handler based on a search query
    public MovieHandler(String searchQuery) {
        this.searchQuery = searchQuery;

        try {
            searchURL = new URL("https://api.themoviedb.org/3/search/movie?api_key=3744632a440f06514578b01d1b6e9d27&query=" + searchQuery);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    //Instantiate a handler based on a specific movie's ID
    public MovieHandler(int movieID) {
        this.movieID = movieID;

        try {
            searchURL = new URL("https://api.themoviedb.org/3/movie/" + movieID + "?api_key=3744632a440f06514578b01d1b6e9d27");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
