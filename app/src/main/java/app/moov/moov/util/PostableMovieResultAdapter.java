package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.PostActivity;

/**
 * Created by Lisa on 29/03/18.
 * NOTE: THIS IS THE ADAPTER FOR SEARCHING FOR MOVIES REGULARLY, NOT ON POST ACTIVITY
 *
 * Modified by Sammy 4/5/2018
 */

public class PostableMovieResultAdapter extends RecyclerView.Adapter<PostableMovieResultAdapter.MyViewHolder> {

    private Context c;
    private List<JSONObject> movieResults;

    public PostableMovieResultAdapter(Context c, List<JSONObject> movieResults) {
        this.c = c;
        this.movieResults = movieResults;
    }

    /**
     * Return a ViewHolder with the correct inflated layout based on viewType
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(c);
        view = mInflater.inflate(R.layout.movie_results_layout, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Bind data to given ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final PostableMovieResultAdapter.MyViewHolder holder, int position) {

        try {
            final String title = (String) movieResults.get(position).get("title");
            final int id = (Integer) movieResults.get(position).get("id");
            final String releaseDate = (String) movieResults.get(position).get("release_date");

            holder.setTitle(title);
            holder.setID(id);
            holder.setReleaseYear(releaseDate);


            final String posterUrl = "https://image.tmdb.org/t/p/w185" + movieResults.get(position).get("poster_path");

            // Load the movie poster into the ImageView
            Glide.with(c).asBitmap().load(posterUrl).into(holder.ivMoviePoster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(c, PostActivity.class);
                    intent.putExtra("movieID", id);
                    intent.putExtra("movieTitle", title);
                    intent.putExtra("posterURL", posterUrl);
                    c.startActivity(intent);
                }
            });

        } catch (JSONException e) {
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return movieResults.size();
    }

    /**
     * Internal class representing a ViewHolder
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle;
        ImageView ivMoviePoster;
        TextView tvReleaseYear;
        private int movieID;
        private View itemView;
        private String movieTitle;
        private String releaseDate;
        private String releaseYear;

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

        public void setReleaseYear(String releaseDate) {
            this.releaseDate = releaseDate;
            if (this.releaseDate.length() == 0 || this.releaseDate == null ) {
                TextView tvReleaseYear = (TextView) itemView.findViewById(R.id.tvReleaseYear);
                tvReleaseYear.setText("");
            }
            else {
                releaseYear = releaseDate.substring(0, 4);
                TextView tvReleaseYear = (TextView) itemView.findViewById(R.id.tvReleaseYear);
                tvReleaseYear.setText(releaseYear);
            }
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









