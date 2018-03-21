package app.moov.moov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Sammy on 3/19/2018.
 * Credit to Paymon Wang-Lotfi for
 * his tutorial and code snippets.
 */

public class MovieHandlerByQuery implements MovieHandler {
    private String searchQuery;
    private TmdbSearch movieSearch;
    private MovieResultsPage results;
    private List<MovieDb> listResults;

    private int searchYear;
    private boolean includeAdult;

    private int page;

    //Instantiate a handler based on search query only, not including 18+
    public MovieHandlerByQuery(String searchQuery) {

        this.searchQuery = searchQuery;
        movieSearch = new TmdbSearch(API_ACCESS);

        searchYear = 0;
        includeAdult = false;
        page = 0;

        results = movieSearch.searchMovie(searchQuery, searchYear, "en", false, 0);

        listResults = results.getResults();

    }


}
