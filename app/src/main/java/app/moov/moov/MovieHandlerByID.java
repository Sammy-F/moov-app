package app.moov.moov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Sammy on 3/21/2018.
 */

public class MovieHandlerByID implements MovieHandler {

    private int movieID;
    private URL searchURL;
    MovieDb movie;

    //Instantiate a handler based on a specific movie's ID
    public MovieHandlerByID(int movieID) {

        movie = movies.getMovie(movieID, "en");

//        this.movieID = movieID;
//
//        try {
//            searchURL = new URL("http://api.themoviedb.org/3/movie/" + movieID + "?api_key=3744632a440f06514578b01d1b6e9d27");
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
    }

}
