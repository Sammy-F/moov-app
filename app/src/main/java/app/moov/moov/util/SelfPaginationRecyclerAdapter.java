package app.moov.moov.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import app.moov.moov.R;
import app.moov.moov.activity.EditPostActivity;
import app.moov.moov.activity.FeedActivity;
import app.moov.moov.activity.FollowersFollowingActivity;
import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sammy on 4/7/2018.
 */

public class SelfPaginationRecyclerAdapter extends PaginationAdapter {

    private UserProfileActivity mActivity;
    private List<Post> postList;
    private long lastTimestamp;

    private String uid;

    private Post placeholderPost;

    private final int PLACEHOLDER_TYPE = 2;
    private final int NO_REVIEW_TYPE = 0;
    private final int WITH_REVIEW_TYPE = 1;

    public SelfPaginationRecyclerAdapter(UserProfileActivity mActivity) {
        super();
        this.mActivity = mActivity;
        postList = new ArrayList<Post>();
        placeholderPost = new Post();
        postList.add(placeholderPost);
    }

    public static class ProfileCardHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvNumFollowing;
        private TextView tvNumFollowers;
        private TextView tvFullName;

        private LinearLayout llFollowers;
        private LinearLayout llFollowing;

        private String userID;

        private String username;

        private Context thisContext;

        public ProfileCardHolder(View itemView, Context thisContext) {
            super(itemView);

            ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvNumFollowing = (TextView) itemView.findViewById(R.id.tvNumFollowing);
            tvNumFollowers = (TextView) itemView.findViewById(R.id.tvNumFollowers);
            tvFullName = (TextView) itemView.findViewById(R.id.tvFullname);
            llFollowers = (LinearLayout) itemView.findViewById(R.id.llFollowers);
            llFollowing = (LinearLayout) itemView.findViewById(R.id.llFollowing);

            this.thisContext = thisContext;
        }

        public void runSetup() {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            final String uid = user.getUid();
            userID = user.getUid();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference avatarRef = firebaseStorage.getReference().child("images").child("avatars").child(userID + ".png");

//        setUIViews();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference baseRef = database.getReference();
            DatabaseReference userRef = database.getReference().child("Users").child(uid);
            DatabaseReference postsRef = database.getReference().child("Users").child(uid).child("Posts");
            DatabaseReference allUsers = baseRef.child("Users");

            avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(thisContext).asBitmap().load(uri.toString()).into(ivAvatar);
                }
            });

//        avatarRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap currentAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                ivAvatar.setImageBitmap(currentAvatar);
//            }
//        });

            llFollowers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(thisContext, FollowersFollowingActivity.class);
                    intent.putExtra("type", "followers");
                    intent.putExtra("uid", userID);
                    thisContext.startActivity(intent);
                }
            });

            llFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(thisContext, FollowersFollowingActivity.class);
                    intent.putExtra("type", "following");
                    intent.putExtra("uid", userID);
                    thisContext.startActivity(intent);
                }
            });

            // Set text for number of Followers user has
            userRef.child("Followers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvNumFollowers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Set next for number of users the user Follows
            userRef.child("Following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvNumFollowing.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /**
             * Inside gets current user's username
             */
            userRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    username = dataSnapshot.getValue(String.class);

                    tvUsername.setText(username);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(thisContext,"Getting username failed.", Toast.LENGTH_SHORT).show();

                }
            });

            userRef.child("FirstName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.getValue() + " ";
                    tvFullName.setText(firstName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userRef.child("LastName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fullName = tvFullName.getText().toString() + dataSnapshot.getValue(String.class);
                    tvFullName.setText(fullName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    thisContext.startActivity(new Intent(thisContext, app.moov.moov.activity.ChangeAvatarActivity.class));
                }
            });

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
        if (postList.get(position) == placeholderPost) {
            return PLACEHOLDER_TYPE;
        } else if (!(postList.get(position).getMovieReview() == null) && !postList.get(position).getMovieReview().equals("")) {
            return WITH_REVIEW_TYPE;
        }
        else {
            return NO_REVIEW_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PLACEHOLDER_TYPE) {
            View placeholderView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cv_self_user_profile, parent, false);
            return new ProfileCardHolder(placeholderView, mActivity);
        } else if (viewType == NO_REVIEW_TYPE) {
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
            case NO_REVIEW_TYPE:
                final SelfPaginationRecyclerAdapter.FeedViewHolderWithoutReview viewHolder1 = (SelfPaginationRecyclerAdapter.FeedViewHolderWithoutReview) viewHolder;

                final Post thisPost = postList.get(position);

                viewHolder1.setTitle(thisPost.getMovieTitle());
                viewHolder1.setRating(Float.parseFloat(thisPost.getMovieRating()));
                viewHolder1.setUsername(thisPost.getUsername());

                String posterUrl = thisPost.getPosterURL();
                Glide.with(mActivity).asBitmap().load(posterUrl).into(viewHolder1.getIvPoster());

                final int movieID = thisPost.getMovieID();

                final int thisPos = position;

                viewHolder1.getIbDetail().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(viewHolder1.getIbDetail(), viewHolder1.getAdapterPosition(), thisPost);
                    }
                });

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
            case WITH_REVIEW_TYPE:
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
            case PLACEHOLDER_TYPE:
                final SelfPaginationRecyclerAdapter.ProfileCardHolder profileCardHolder = (SelfPaginationRecyclerAdapter.ProfileCardHolder) viewHolder;
                profileCardHolder.runSetup();
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
        mActivity.startActivity(new Intent(mActivity, UserProfileActivity.class));

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
