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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/7/2018.
 */

public class PaginationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context thisContext;
    private List<Post> postList;

    public PaginationRecyclerAdapter(Context thisContext) {
        super();
        this.thisContext = thisContext;
        postList = new ArrayList<Post>();
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
        if (getItem(position).getMovieReview().equals("")) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View viewWithoutReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nr_new_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        }
        else {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_cv_layout, parent, false);

            return new FeedViewHolder(viewWithReview);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        //Decide which ViewType the Post has (i.e. with review or without review)
        // and sets up the item depending on this ViewType.
        switch(viewHolder.getItemViewType()) {
            case 0:
                final PaginationRecyclerAdapter.FeedViewHolderWithoutReview viewHolder1 = (PaginationRecyclerAdapter.FeedViewHolderWithoutReview) viewHolder;

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
                        Intent intent = new Intent(thisContext, OtherUserProfile.class);
                        intent.putExtra("thisUserID", thisPost.getUID());
                        thisContext.startActivity(intent);
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
            case 1:
                final PaginationRecyclerAdapter.FeedViewHolder viewHolderWith = (PaginationRecyclerAdapter.FeedViewHolder) viewHolder;

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
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public Long getLastItemTimestamp() {
        Log.e("timestamp post", Long.toString(postList.get(postList.size() -1).getTime()));
        return postList.get(postList.size() - 1).getTime();
    }

    public void addAll(List<Post> newPosts) {
        int initSize = postList.size();
        postList.addAll(newPosts);
        notifyItemRangeInserted(initSize, postList.size());
    }

    public void addItem(Post newPost) {
        int initSize = postList.size();
        postList.add(newPost);
        notifyItemInserted(initSize);
    }

    public List<Post> getPostList() { return postList; }

}
