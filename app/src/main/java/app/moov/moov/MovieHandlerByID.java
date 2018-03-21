package app.moov.moov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sammy on 3/21/2018.
 */

public class MovieHandlerByID implements MovieHandler {

    private int movieID;
    private URL searchURL;

    //Instantiate a handler based on a specific movie's ID
    public MovieHandlerByID(int movieID) {
        this.movieID = movieID;

        try {
            searchURL = new URL("http://api.themoviedb.org/3/movie/" + movieID + "?api_key=3744632a440f06514578b01d1b6e9d27");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPathFromID() {
        String path = "";

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

        return path;
    }

}
