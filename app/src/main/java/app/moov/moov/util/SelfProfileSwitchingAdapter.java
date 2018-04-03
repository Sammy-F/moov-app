package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import app.moov.moov.R;
import app.moov.moov.activity.EditPostActivity;
import app.moov.moov.activity.MovieProfile;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/2/2018.
 */

public class SelfProfileSwitchingAdapter extends FirebaseRecyclerAdapter<Post, RecyclerView.ViewHolder> {

    private Context thisContext;

    public SelfProfileSwitchingAdapter(FirebaseRecyclerOptions options, Context mContext) {
        super(options);
        thisContext = mContext;
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
        private Button editBtn;
        private Button delBtn;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
            editBtn = mView.findViewById(R.id.btnEdit);
            delBtn = mView.findViewById(R.id.btnDelete);
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
     * Internal ViewHolder class
     */
    public static class FeedViewHolderWithoutReview extends RecyclerView.ViewHolder{

        private TextView btnMovieTitle;
        private ImageView ivPoster;
        private View mView;
        private TextView userName;
        Button editBtn;
        Button delBtn;

        public FeedViewHolderWithoutReview(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
            editBtn = mView.findViewById(R.id.btnEdit);
            delBtn = mView.findViewById(R.id.btnDelete);
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
                    .inflate(R.layout.nr_new_self_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        }
        else {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_self_cv_layout, parent, false);

            return new FeedViewHolder(viewWithReview);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getMovieReview().equals("")) {
            return 0;
        }
        else {
            return 1;
        }
    }

    private void editPost(String pid, String review, String movieTitle, String rating) {
        Intent intent = new Intent(thisContext, EditPostActivity.class);
        intent.putExtra("rating", rating);
        intent.putExtra("review", review);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("postID", pid);
        thisContext.startActivity(intent);
    }

    /**
     * Method to handle post deletion
     * @param pid
     * @param uid
     */
    private void deletePost(String pid, String uid) {
        final String thisPid = pid;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        final DatabaseReference allUsers = database.getReference().child("Users");
        DatabaseReference userRef = allUsers.child(uid);
        DatabaseReference userPostRef = userRef.child("Posts").child(pid);
        DatabaseReference postRef = ref.child("Posts").child(pid);

        userRef.child("Feed").child(pid).removeValue();
        userPostRef.removeValue();
        postRef.removeValue();

        userRef.child("Followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot follower : dataSnapshot.getChildren()) {
                    String UID = follower.getKey();

                    allUsers.child(UID).child("Feed").child(thisPid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                database.getReference().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, final Post model) {

        switch(viewHolder.getItemViewType()) {
            case 0:
                final FeedViewHolderWithoutReview viewHolder1 = (FeedViewHolderWithoutReview) viewHolder;

                final Post thisPost = model;

                final String pid = thisPost.getPID();
                final String uid = thisPost.getUID();

                viewHolder1.setTitle(model.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolder1.setUsername(model.getUsername());

                String posterUrl = model.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder1.ivPoster);

                final int movieID = model.getMovieID();

                viewHolder1.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPost(thisPost.getPID(), thisPost.getMovieReview(), thisPost.getMovieTitle(), thisPost.getMovieRating());
                    }
                });

                viewHolder1.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePost(pid, uid);
                    }
                });

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
//                        MovieGetterByID movieGetter = new MovieGetterByID(thisContext, movieID);
//                        MovieDb thisMovie = movieGetter.getMovie();
//                        String movieTitle = thisMovie.getTitle();
//                        Toast.makeText(FeedActivity.this,movieTitle, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 1:
                final FeedViewHolder viewHolderWith = (FeedViewHolder) viewHolder;

                final Post ourPost = model;

                final String pid2 = ourPost.getPID();
                final String uid2 = ourPost.getUID();

                viewHolderWith.setTitle(model.getMovieTitle());
                viewHolderWith.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolderWith.setReview(model.getMovieReview());
                viewHolderWith.setUsername(model.getUsername());

                String posterUrlWith = model.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrlWith).into(viewHolderWith.ivPoster);

                final int movieIDWith = model.getMovieID();

                viewHolderWith.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editPost(ourPost.getPID(), ourPost.getMovieReview(), ourPost.getMovieTitle(), ourPost.getMovieRating());
                    }
                });

                viewHolderWith.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePost(pid2, uid2);
                    }
                });

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
                        Intent intent = new Intent(thisContext, MovieProfile.class);
                        intent.putExtra("movieID", movieIDWith);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
        }
    }
}