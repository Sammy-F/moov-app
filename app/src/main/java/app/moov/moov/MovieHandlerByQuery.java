package app.moov.moov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.movito.themoviedbapi.TmdbApi;

/**
 * Created by Sammy on 3/19/2018.
 * Credit to Paymon Wang-Lotfi for
 * his tutorial and code snippets.
 */

public class MovieHandlerByQuery implements MovieHandler {
    private String searchQuery;
    URL searchURL;

    //Instantiate a handler based on a search query
    public MovieHandlerByQuery(String searchQuery) {
        this.searchQuery = searchQuery;

        try {
            searchURL = new URL("http://api.themoviedb.org/3/search/movie?api_key=3744632a440f06514578b01d1b6e9d27&query=" + searchQuery);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getPathsFromSearchQuery() {
        String[] array = new String[15];

        boolean running = true;

        while(running) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) searchURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input steam into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                String JSONResult = buffer.toString();
            } catch (IOException e) {

            }
        }

        return array;
    }
}
