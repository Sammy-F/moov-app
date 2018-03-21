package app.moov.moov;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;

/**
 * Created by Alonaria on 3/21/2018.
 */

public interface MovieHandler {
    String API_KEY = "3744632a440f06514578b01d1b6e9d27";
    TmdbApi API_ACCESS = new TmdbApi(API_KEY);
    TmdbMovies movies = new TmdbApi(API_KEY).getMovies();
}
