package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.moov.moov.R;
import app.moov.moov.activity.EditPostActivity;
import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/2/2018.
 *
 * Adapter that changes ViewHolders depending on
 * whether a Post has a comment or not for the user's
 * own profile.
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
        private ImageButton ibDetail;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
            ibDetail = mView.findViewById(R.id.ibDetail);
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
        private ImageButton ibDetail;

        public FeedViewHolderWithoutReview(View itemView) {
            super(itemView);
            mView = itemView;
            btnMovieTitle = mView.findViewById(R.id.MovieTitle);
            ivPoster = mView.findViewById(R.id.ivPoster);
            ibDetail = mView.findViewById(R.id.ibDetail);
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
                    .inflate(R.layout.profile_noreview_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        }
        else {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_review_cv_layout, parent, false);

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

    /**
     * When called, creates an Intent that sends the user
     * to the EditPost activity and passes along information about
     * the given Post.
     * @param pid
     * @param review
     * @param movieTitle
     * @param rating
     */
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
    private void deletePost(String pid, String uid, int movieId) {
        final String thisPid = pid;                 // generate references for use in deleting the post
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        final DatabaseReference allUsers = database.getReference().child("Users");
        DatabaseReference userRef = allUsers.child(uid);
        DatabaseReference userPostRef = userRef.child("Posts").child(pid);
        DatabaseReference postRef = ref.child("Posts").child(pid);

        userRef.child("Feed").child(pid).removeValue();
        userPostRef.removeValue();
        postRef.removeValue();

        ref.child("PostsByMovie").child(Integer.toString(movieId)).child(pid).removeValue();

        userRef.child("Followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot follower : dataSnapshot.getChildren()) { //get all of the user's followers and delete the post from their feed
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
                            Toast.makeText(thisContext, "Unable to delete post.", Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Allows us to show a drop down method when clicked
     * @param view
     * @param position
     * @param model
     */
    private void showPopupMenu(View view, int position, Post model) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cardview_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, model));
        popup.show();
    }

    /**
     * Listener that listens for when the popup menu is clicked
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private Post thisPost;
        public MyMenuItemClickListener(int position, Post model) {
            this.position = position;
            this.thisPost = model;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.deletePost:
                    deletePost(thisPost.getPID(), thisPost.getUID(), thisPost.getMovieID());
                    return true;
                case R.id.editPost:
                    editPost(thisPost.getPID(), thisPost.getMovieReview(), thisPost.getMovieTitle(), thisPost.getMovieRating());
                    return true;
            }
            return false;
        }
    }

    /**
     * Bind a Post to a given ViewHolder
     * @param viewHolder
     * @param position
     * @param model
     */
    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position, final Post model) {

        switch(viewHolder.getItemViewType()) { //cast a different type of ViewHolder if the Post has a review or not
            case 0:
                final FeedViewHolderWithoutReview viewHolder1 = (FeedViewHolderWithoutReview) viewHolder;

                final Post thisPost = model;

                viewHolder1.setTitle(model.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(model.getMovieRating()));
                viewHolder1.setUsername(model.getUsername());

                String posterUrl = model.getPosterURL();
                Glide.with(thisContext).asBitmap().load(posterUrl).into(viewHolder1.ivPoster);

                final int movieID = model.getMovieID();

                (viewHolder1).ibDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(viewHolder1.ibDetail, position, model);
                    }
                });


                viewHolder1.getUsername().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Intent intent = new Intent(thisContext, UserProfileActivity.class);
                            thisContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(thisContext, OtherUserProfile.class);
                            intent.putExtra("thisUserID", model.getUID());
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

                viewHolderWith.ibDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(viewHolderWith.ibDetail, position, model);
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
                        Intent intent = new Intent(thisContext, MovieProfileActivity.class);
                        intent.putExtra("movieID", movieIDWith);
                        thisContext.startActivity(intent); //go to movie's profile
                    }
                });
                break;
        }
    }
}