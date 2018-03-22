package app.moov.moov;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Sammy on 3/21/2018.
 */

public class MovieResultsAdapter extends RecyclerView.Adapter<MovieResultsAdapter.ResultsViewHolder> {
    private List<MovieDb> movieResults;

    /**
     * Construct the Adapter using a list of MovieDb objects
     * @param movieResults
     */
    public MovieResultsAdapter(List<MovieDb> movieResults) {
        this.movieResults = movieResults;
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
    public void onBindViewHolder(MovieResultsAdapter.ResultsViewHolder holder, int position) {
        holder.setTitle(movieResults.get(position).getTitle());
        holder.setSummary(movieResults.get(position).getOverview());
        holder.setID(movieResults.get(position).getId());
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

        public ResultsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setTitle(String title) {
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

    }
}
