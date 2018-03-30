package app.moov.moov.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.moov.moov.R;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Lisa on 29/03/18.
 * NOTE: THIS IS THE ADAPTER FOR SEARCHING FOR MOVIES REGULARLY, NOT ON POST ACTIVITY
 */

public class MovieSearchResultAdapter extends RecyclerView.Adapter<MovieSearchResultAdapter.MyViewHolder> {

    private Context c;
    private List<MovieDb> movieResults;

    public MovieSearchResultAdapter(Context c, List<MovieDb> movieResults) {
        this.c = c;
        this.movieResults = movieResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(c);
        view = mInflater.inflate(R.layout.movie_search_result, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieSearchResultAdapter.MyViewHolder holder, int position) {
        holder.tvMovieTitle.setText(movieResults.get(position).getTitle());
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


    }
}









