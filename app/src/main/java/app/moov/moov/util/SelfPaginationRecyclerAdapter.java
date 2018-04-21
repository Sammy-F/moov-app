package app.moov.moov.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.EditPostActivity;
import app.moov.moov.activity.FeedActivity;
import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/7/2018.
 */

public class SelfPaginationRecyclerAdapter extends PaginationAdapter {

    private UserProfileActivity mActivity;
    private List<Post> postList;
    private long lastTimestamp;

    public SelfPaginationRecyclerAdapter(UserProfileActivity mActivity) {
        super();
        this.mActivity = mActivity;
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

        public ImageButton getIbDetail() { return ibDetail; }

    }

    /**
     * Internal ViewHolder class used when the Post has no review.
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

        public ImageButton getIbDetail() { return ibDetail; }

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
        if (!(postList.get(position).getMovieReview() == null) || !postList.get(position).getMovieReview().equals("")) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View viewWithoutReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_noreview_cv_layout, parent, false);
            return new FeedViewHolderWithoutReview(viewWithoutReview);
        } else {
            View viewWithReview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_review_cv_layout, parent, false);

            return new FeedViewHolder(viewWithReview);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        //Decide which ViewType the Post has (i.e. with review or without review)
        // and sets up the item depending on this ViewType.
        switch(viewHolder.getItemViewType()) {
            case 0:
                final SelfPaginationRecyclerAdapter.FeedViewHolderWithoutReview viewHolder1 = (SelfPaginationRecyclerAdapter.FeedViewHolderWithoutReview) viewHolder;

                final Post thisPost = postList.get(position);

                viewHolder1.setTitle(thisPost.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(thisPost.getMovieRating()));
                viewHolder1.setUsername(thisPost.getUsername());

                String posterUrl = thisPost.getPosterURL();
                Glide.with(mActivity).asBitmap().load(posterUrl).into(viewHolder1.getIvPoster());

                final int movieID = thisPost.getMovieID();

                final int thisPos = position;
//
//                viewHolder1.getIbDetail().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        showPopupMenu(viewHolder1.getIbDetail(), viewHolder1.getAdapterPosition(), thisPost);
//                    }
//                });

                viewHolder1.getUsername().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (thisPost.getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Intent intent = new Intent(mActivity, UserProfileActivity.class);
                            mActivity.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mActivity, OtherUserProfile.class);
                            intent.putExtra("thisUserID", thisPost.getUID());
                            mActivity.startActivity(intent);
                        }
                    }
                });

                viewHolder1.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(mActivity, MovieProfileActivity.class);
                        intent.putExtra("movieID", movieID);
                        mActivity.startActivity(intent); //go to movie's profile
                    }
                });
                break;
            case 1:
                final SelfPaginationRecyclerAdapter.FeedViewHolder viewHolderWith = (SelfPaginationRecyclerAdapter.FeedViewHolder) viewHolder;

                final Post reviewedThisPost = postList.get(position);

                viewHolderWith.setTitle(reviewedThisPost.getMovieTitle());
                viewHolderWith.setRating(Float.parseFloat(reviewedThisPost.getMovieRating()));
                viewHolderWith.setReview(reviewedThisPost.getMovieReview());
                viewHolderWith.setUsername(reviewedThisPost.getUsername());

                String posterUrlWith = reviewedThisPost.getPosterURL();

                // Load and place the movie poster in the ImageView
                Glide.with(mActivity).asBitmap().load(posterUrlWith).into(viewHolderWith.getIvPoster());

                final int movieIDWith = reviewedThisPost.getMovieID();

                viewHolderWith.getIbDetail().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(viewHolderWith.getIbDetail(), viewHolderWith.getAdapterPosition(), reviewedThisPost);
                    }
                });

                viewHolderWith.getUserName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity, OtherUserProfile.class);
                        intent.putExtra("thisUserID", reviewedThisPost.getUID());
                        mActivity.startActivity(intent);
                    }
                });

                viewHolderWith.getIvPoster().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //temporary button response
                        Intent intent = new Intent(mActivity, MovieProfileActivity.class);
                        intent.putExtra("movieID", movieIDWith);
                        mActivity.startActivity(intent); //go to movie's profile
                    }
                });
                break;
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

    private void editPost(String pid, String review, String movieTitle, String rating) {
        Intent intent = new Intent(mActivity, EditPostActivity.class);
        intent.putExtra("rating", rating);
        intent.putExtra("review", review);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("postID", pid);
        mActivity.startActivity(intent);
    }

    /**
     * Method to handle post deletion
     * @param pid
     * @param uid
     */
    private void deletePost(String pid, String uid, int movieId) {
        final String thisPid = pid;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        final DatabaseReference allUsers = database.getReference().child("Users");
        DatabaseReference userRef = allUsers.child(uid);
        DatabaseReference postRef = ref.child("Posts").child(pid);

        userRef.child("Feed").child(pid).removeValue();
        userRef.child("Posts").child(pid).removeValue();
        postRef.removeValue();

        ref.child("PostsByMovie").child(Integer.toString(movieId)).child(pid).removeValue();

        userRef.child("Followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot follower : dataSnapshot.getChildren()) {
                    final String UID = follower.getKey();
                    Log.e("this uid", UID);

                    allUsers.child(UID).child("Feed").child(thisPid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("this uid", UID);
                            if (dataSnapshot.exists()) {
                                allUsers.child(UID).child("Feed").child(thisPid).removeValue();
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

        mActivity.finish();
        mActivity.startActivity(new Intent(mActivity, FeedActivity.class));

    }

    private void showPopupMenu(View view, int position, Post model) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cardview_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new SelfPaginationRecyclerAdapter.MyMenuItemClickListener(position, model));
        popup.show();
    }

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

}
