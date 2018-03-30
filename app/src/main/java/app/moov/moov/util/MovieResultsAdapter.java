package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.PostActivity;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Sammy on 3/21/2018.
 */

public class MovieResultsAdapter extends RecyclerView.Adapter<MovieResultsAdapter.ResultsViewHolder> {
    private List<MovieDb> movieResults;
    private Context c;

    /**
     * Construct the Adapter using a list of MovieDb objects
     * @param movieResults
     */
    public MovieResultsAdapter(Context c, List<MovieDb> movieResults) {
        this.movieResults = movieResults;
        this.c = c;
    }

    /**
     * Declare card variables by inflating a new
     * ResultsViewHOlder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MovieResultsAdapter.ResultsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {

        View ourView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movie_results_layout, parent, false);
        return new ResultsViewHolder(ourView);

    }

    /**
     * Initialize the layout variables using information pulled
     * from the objects in movieResults
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MovieResultsAdapter.ResultsViewHolder holder, int position) {
        holder.setTitle(movieResults.get(position).getTitle());
        holder.setSummary(movieResults.get(position).getOverview());
        holder.setID(movieResults.get(position).getId());

        holder.getBtnChoose().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, PostActivity.class);
                intent.putExtra("movieID", holder.getID());
                intent.putExtra("movieTitle", holder.getTitle());
                c.startActivity(intent);
            }
        });
    }

    /**
     * Get int size of List
     * @return
     */
    @Override
    public int getItemCount() {
        return movieResults.size();
    }

    /**
     * ViewHolder static class
     */
    public static class ResultsViewHolder extends RecyclerView.ViewHolder{

        private int movieID;
        private View itemView;
        private Button btnChoose;

        private String title;

        public ResultsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.btnChoose = (Button) itemView.findViewById(R.id.chooseBtn);
        }

        public void setTitle(String title) {
            this.title = title;
            TextView movieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            movieTitle.setText(title);
        }

        public void setSummary(String summary) {
            TextView movieRating = (TextView) itemView.findViewById(R.id.tvSummary);
            movieRating.setText(summary);
        }

        public void setID(int movieID) {
            this.movieID = movieID;
        }

        public int getID() { return movieID; }

        public Button getBtnChoose() { return btnChoose; }

        public String getTitle() { return title; }

    }
}
