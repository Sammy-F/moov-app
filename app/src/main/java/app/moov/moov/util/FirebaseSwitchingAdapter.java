package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;

import app.moov.moov.R;
import app.moov.moov.activity.MovieProfile;
import app.moov.moov.activity.MovieProfileActivity2;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/2/2018.
 *
 * Adapter that changes ViewHolders depending on
 * whether a Post has a comment or not.
 */

public class FirebaseSwitchingAdapter extends FirebaseRecyclerAdapter<Post, RecyclerView.ViewHolder> {

    private Context thisContext;

    public FirebaseSwitchingAdapter(FirebaseRecyclerOptions options, Context mContext) {
        super(options);
        thisContext = mContext;

        options.getSnapshots();
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View viewWithoutReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.noreview_new_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        }
        else {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_cv_layout, parent, false);

            return new FeedViewHolder(viewWithReview);
        }

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
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, final Post model) {

        //Decide which ViewType the Post has (i.e. with review or without review)
        // and sets up the item depending on this ViewType.
        switch(viewHolder.getItemViewType()) {
            case 0:
                final FeedViewHolderWithoutReview viewHolder1 = (FeedViewHolderWithoutReview) viewHolder;

                viewHolder1.setTitle(model.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolder1.setUsername(model.getUsername());

                String posterUrl = model.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder1.ivPoster);

                final int movieID = model.getMovieID();

                viewHolder1.getUsername().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(thisContext, OtherUserProfile.class);
                        intent.putExtra("thisUserID", model.getUID());
                        thisContext.startActivity(intent);
                    }
                });

                viewHolder1.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(thisContext, MovieProfile.class);
                        intent.putExtra("movieID", movieID);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
            case 1:
                final FeedViewHolder viewHolderWith = (FeedViewHolder) viewHolder;

                viewHolderWith.setTitle(model.getMovieTitle());
                viewHolderWith.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolderWith.setReview(model.getMovieReview());
                viewHolderWith.setUsername(model.getUsername());

                String posterUrlWith = model.getPosterURL();

                // Load and place the movie poster in the ImageView
                Glide.with(thisContext).asBitmap().load(posterUrlWith).into(viewHolderWith.ivPoster);

                final int movieIDWith = model.getMovieID();

                viewHolderWith.getUserName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(thisContext, OtherUserProfile.class);
                        intent.putExtra("thisUserID", model.getUID());
                        thisContext.startActivity(intent);
                    }
                });

                viewHolderWith.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(thisContext, MovieProfileActivity2.class);
                        intent.putExtra("movieID", movieIDWith);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
        }
    }
}
