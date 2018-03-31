package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.PostActivity;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Lisa on 29/03/18.
 * NOTE: THIS IS THE ADAPTER FOR SEARCHING FOR MOVIES REGULARLY, NOT ON POST ACTIVITY
 */

public class MovieSearchResultAdapter extends RecyclerView.Adapter<MovieSearchResultAdapter.MyViewHolder> {

    private Context c;
    private List<JSONObject> movieResults;

    public MovieSearchResultAdapter(Context c, List<JSONObject> movieResults) {
        this.c = c;
        this.movieResults = movieResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(c);
        view = mInflater.inflate(R.layout.movie_search_results2_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieSearchResultAdapter.MyViewHolder holder, int position) {

        try {
            String title = (String) movieResults.get(position).get("title");
            int id = (Integer) movieResults.get(position).get("id");

            holder.setTitle(title);
            holder.setID(id);

            String posterUrl = "https://image.tmdb.org/t/p/w400" + movieResults.get(position).get("poster_path");

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    holder.setMoviePoster(bitmap);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    Log.e("Bitmap Error", e.getMessage());

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.get();

            //old way of getting image
//            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
//                @Override
//                public void onResponse(Bitmap response) {
//                    holder.ivMoviePoster.setImageBitmap(response);
//                }
//            }, 0, 0, null, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //TODO: Handle error
//                }
//            });

        } catch (JSONException e) {
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
        }

//        holder.tvMovieTitle.setText(movieResults.get(position).getTitle());
//        holder.ivMoviePoster.setImageResource(movieResults.get(position).getImages(ArtworkType.POSTER));

    }

    @Override
    public int getItemCount() {
        return movieResults.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle;
        ImageView ivMoviePoster;
        private int movieID;
        private View itemView;
        private String movieTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);

        }

        public void setTitle(String title) {
            this.movieTitle = title;
            TextView movieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            movieTitle.setText(title);
        }

        public void setID(int movieID) {
            this.movieID = movieID;
        }

        public int getID() { return movieID; }

        public String getTitle() { return movieTitle; }

        public void setMoviePoster(Bitmap bitmap) {
            ivMoviePoster.setImageBitmap(bitmap);
        }


    }
}









