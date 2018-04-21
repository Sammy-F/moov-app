package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/20/2018.
 *
 * Paginating Adapter for use on MovieProfiles. When creating this
 * adapter, you should also pass in the movieID variable that is stored by
 * the Intent that goes to the MovieProfile
 */

public class MovieProfilePaginatingRecyclerAdapter extends PaginationAdapter {

    private Context thisContext;
    private List<Post> postList;
    private long lastTimestamp;
    private int movieID;

    private Post placeholderPost;

    private final int PLACEHOLDER_TYPE = 2;
    private final int NO_REVIEW_TYPE = 0;
    private final int WITH_REVIEW_TYPE = 1;

    public MovieProfilePaginatingRecyclerAdapter(Context thisContext, int movieID) {
        super();
        this.thisContext = thisContext;
        this.movieID = movieID;
        postList = new ArrayList<Post>();
        placeholderPost = new Post();

        postList.add(placeholderPost); //We'll add the placeholder to the List
    }

    public static class MovieProfileViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private ImageView ivMoviePoster;
        private TextView tvMovieTitle;
        private TextView tvMovieYear;
        private TextView tvMovieRuntime;
        private TextView tvMovieSummary;

        private int movieID;

        private Context thisContext;

        public MovieProfileViewHolder(View itemView, Context thisContext) {
            super(itemView);
            mView = itemView;

            ivMoviePoster = mView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = mView.findViewById(R.id.tvMovieTitle);
            tvMovieYear = mView.findViewById(R.id.tvReleaseYear);
            tvMovieRuntime = mView.findViewById(R.id.tvRuntime);
            tvMovieSummary = mView.findViewById(R.id.tvMovieSummary);

            this.thisContext = thisContext;

        }

        public void setMovieID(int movieID) {
            this.movieID=movieID;
            runSetup();
        }

        private void runSetup() {
            String url = "https://api.themoviedb.org/3/movie/"+ Integer.toString(movieID) + "?api_key=3744632a440f06514578b01d1b6e9d27";
            RequestQueue queue = VolleySingleton.getInstance(thisContext.getApplicationContext()).getRequestQueue();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONObject movieDetail = response;
                        String title = (String) response.get("title");
                        if (title != null) {
                            tvMovieTitle.setText("Movie Title: " + title);
                        }

                        String releaseDate = (String) response.get("release_date");
                        if (releaseDate!=null) {
                            String subString = releaseDate.substring(0, 4);
                            tvMovieYear.setText("Release Year: " + subString);
                        }

                        String summary = (String) response.get("overview");
                        if (summary!=null) {
                            tvMovieSummary.setText("Summary: " + summary);
                        }

                        try {
                            Integer runtime = (Integer) response.get("runtime");
                            tvMovieRuntime.setText("Runtime: " + Integer.toString(runtime) + " minutes");
                        } catch (ClassCastException e) { //catch null runtimes
                            String unknown = "Run time unknown";
                            tvMovieRuntime.setText("Runtime: " +unknown);
                        }

                        String posterUrl = "https://image.tmdb.org/t/p/w185/" + ((String) movieDetail.get("poster_path"));

                        Glide.with(thisContext).asBitmap().load(posterUrl).into(ivMoviePoster);

                    } catch (org.json.JSONException e) {
                        Toast.makeText(thisContext, "Sorry", Toast.LENGTH_LONG).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(thisContext,"An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("JSON Error", "Unable to get JSON Object Array");
                }
            });

            queue.add(jsonObjectRequest);
        }

    }

    /**
     * Internal ViewHolder class used
     * for the Feed's RecyclerView
     */
    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        private TextView btnMovieTitle;
        private TextView movieReview;
        private ImageView ivPoster;
        private View mView;
        private TextView userName;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
        }

        public void setTitle(String title) {
            btnMovieTitle.setText(title);
        }

        public void setRating(float rating) {
            RatingBar movieRating = (RatingBar) mView.findViewById(R.id.ratingBar);
            movieRating.setIsIndicator(true);
            movieRating.setRating(rating);
        }

        public void setReview(String review) {
            movieReview = (TextView) mView.findViewById(R.id.MovieReview);
            movieReview.setText(review);
        }

        public TextView getReviewView() { return movieReview; }

        public void setUsername(String username) {
            userName = (TextView) mView.findViewById(R.id.Username);
            userName.setText(username);
        }

        public TextView getUserName() { return userName; }

        public TextView getBtnMovieTitle() { return btnMovieTitle; }

        public ImageView getIvPoster() {
            return ivPoster;
        }

    }

    /**
     * Internal ViewHolder class used when the Post has no review.
     */
    public static class FeedViewHolderWithoutReview extends RecyclerView.ViewHolder{

        private TextView btnMovieTitle;
        private ImageView ivPoster;
        private View mView;
        private TextView userName;

        public FeedViewHolderWithoutReview(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
        }

        public void setTitle(String title) {
            btnMovieTitle.setText(title);
        }

        public void setRating(float rating) {
            RatingBar movieRating = (RatingBar) mView.findViewById(R.id.ratingBar);
            movieRating.setIsIndicator(true);
            movieRating.setRating(rating);
        }

        public void setUsername(String username) {
            userName = (TextView) mView.findViewById(R.id.Username);
            userName.setText(username);
        }

        public TextView getUsername() { return userName; }

        public TextView getBtnMovieTitle() { return btnMovieTitle; }

        public ImageView getIvPoster() {
            return ivPoster;
        }

    }

    private Post getItem(int position) {
        return postList.get(position);
    }

    /**
     * Assign the ViewType for the item based on whether a
     * review exists or not
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (postList.get(position) == placeholderPost) {
            return PLACEHOLDER_TYPE;
        } else if (!postList.get(position).getMovieReview().equals("")) {
            return WITH_REVIEW_TYPE;
        } else {
            return NO_REVIEW_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NO_REVIEW_TYPE) {
            View viewWithoutReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_noreivew_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        } else if (viewType == WITH_REVIEW_TYPE) {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_review_cv_layout, parent, false);

            return new FeedViewHolder(viewWithReview);
        } else {
            View movieProfile = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cv_movie_profile, parent, false);
            return new MovieProfileViewHolder(movieProfile, thisContext);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        //Decide which ViewType the Post has (i.e. with review or without review)
        // and sets up the item depending on this ViewType.
        switch(viewHolder.getItemViewType()) {
            case NO_REVIEW_TYPE:
                final MovieProfilePaginatingRecyclerAdapter.FeedViewHolderWithoutReview viewHolder1 = (MovieProfilePaginatingRecyclerAdapter.FeedViewHolderWithoutReview) viewHolder;

                final Post thisPost = postList.get(position);

                viewHolder1.setTitle(thisPost.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(thisPost.getMovieRating()));
                viewHolder1.setUsername(thisPost.getUsername());

                String posterUrl = thisPost.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder1.getIvPoster());

                final int movieID = thisPost.getMovieID();

                final int thisPos = position;

                viewHolder1.getUsername().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (thisPost.getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Intent intent = new Intent(thisContext, UserProfileActivity.class);
                            thisContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(thisContext, OtherUserProfile.class);
                            intent.putExtra("thisUserID", thisPost.getUID());
                            thisContext.startActivity(intent);
                        }
                    }
                });

                viewHolder1.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(thisContext, MovieProfileActivity.class);
                        intent.putExtra("movieID", movieID);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
            case WITH_REVIEW_TYPE:
                final MovieProfilePaginatingRecyclerAdapter.FeedViewHolder viewHolderWith = (MovieProfilePaginatingRecyclerAdapter.FeedViewHolder) viewHolder;

                final Post reviewedThisPost = postList.get(position);

                viewHolderWith.setTitle(reviewedThisPost.getMovieTitle());
                viewHolderWith.setRating(Float.parseFloat(reviewedThisPost.getMovieRating()));
                viewHolderWith.setReview(reviewedThisPost.getMovieReview());
                viewHolderWith.setUsername(reviewedThisPost.getUsername());

                String posterUrlWith = reviewedThisPost.getPosterURL();

                // Load and place the movie poster in the ImageView
                Glide.with(thisContext).asBitmap().load(posterUrlWith).into(viewHolderWith.getIvPoster());

                final int movieIDWith = reviewedThisPost.getMovieID();

                viewHolderWith.getUserName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(thisContext, OtherUserProfile.class);
                        intent.putExtra("thisUserID", reviewedThisPost.getUID());
                        thisContext.startActivity(intent);
                    }
                });

                viewHolderWith.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(thisContext, MovieProfileActivity.class);
                        intent.putExtra("movieID", movieIDWith);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
            case PLACEHOLDER_TYPE:
                final MovieProfilePaginatingRecyclerAdapter.MovieProfileViewHolder profileHolder = (MovieProfilePaginatingRecyclerAdapter.MovieProfileViewHolder) viewHolder;
                profileHolder.setMovieID(this.movieID);
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
//        return postList.size();
    }

    @Override
    public void setLastTimeStamp(long newTime) {

        this.lastTimestamp = newTime;

    }

    @Override
    public Long getLastTimestamp() { return this.lastTimestamp; }

    public Long getLastItemTimestamp() {
        Log.e("timestamp post", Long.toString(postList.get(postList.size() -1).getTime()));
        return postList.get(postList.size() - 1).getTime();
    }

    public void addAll(List<Post> newPosts) {
        int initSize = postList.size();
        postList.addAll(newPosts);
        notifyItemRangeInserted(initSize, postList.size());
    }

    @Override
    public void addItem(Post newPost) {
        int initSize = postList.size();
        postList.add(newPost);
        notifyItemInserted(initSize);
    }

    @Override
    public List<Post> getPostList() { return postList; }

}
