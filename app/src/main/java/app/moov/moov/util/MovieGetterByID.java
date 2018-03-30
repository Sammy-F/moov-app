package app.moov.moov.util;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Sammy on 3/23/2018.
 *
 * Class allows us to get a MovieDb object
 * from the Tmdb API in a separate thread.
 *
 * DEPRECATED
 */

public class MovieGetterByID {

    Context activityContext; //the context in which the MovieGetter is called
    int ID;
    private MovieDb thisMovie;

    public MovieGetterByID(Context activityContext, int ID) {
        this.activityContext = activityContext;
        this.ID = ID;

        APIMovieGetter movieGetter = new APIMovieGetter(ID); //initialize a new APIMovieGetter (internal class)
        movieGetter.execute(); //run doInBackground
        try {
            thisMovie = movieGetter.get(); //get the output from doInBackground
        } catch (InterruptedException e) {
            thisMovie = null;
        } catch (ExecutionException f) {
            thisMovie = null;
        }

        movieGetter.cancel(true); //terminate the thread
    }

    /**
     * Internal class handles getting movie in separate thread.
     */
    static class APIMovieGetter extends AsyncTask<Integer, Void, MovieDb> {
        private MovieDb movie;
        private int movieID;
        Exception exception;

        public APIMovieGetter(int ID) {
            super();
            this.movieID = ID;
        }

        /**
         * Processes run when execute() is called
         * @param ID
         * @return
         */
        protected MovieDb doInBackground(Integer[] ID) {
            try {
                TmdbMovies movies = new TmdbApi("3744632a440f06514578b01d1b6e9d27").getMovies();
                return movies.getMovie(movieID, "en");
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

    public MovieDb getMovie() { return thisMovie; }
}
