package app.moov.moov.util;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.OtherUserProfile;
import app.moov.moov.activity.ToolbarBaseActivity;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy F on 4/14/2018.
 */

/**
 * Abstract custom Activity to be used on pages with basic posts
 *
 * All classes that extend this should call paginationSetup()
 * Custom paginationSetup() methods can be written if required for the activity
 */

public abstract class PaginatingPostsActivity extends ToolbarBaseActivity {

    //CONSTANTS

    private final int MISC_ACTIVITY_TYPE = 0;
    private final int USER_PROFILE_ACTIVITY_TYPE = 1;
    private final int MOVIE_PROFILE_ACTIVITY_TYPE = 2;
    private final int OTHER_USER_PROFILE_ACTIVITY_TYPE = 3;

    private final int POSTS_PER_LOAD = 15;

    private int activityType;

    // INSTANCE VARIABLES

        //Parameters
    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager orderedManager;
    private Context thisContext;

        //Generated internally

    private String lastTime;
    private boolean isLoading;

    private int mTotalItemCount;
    private int mVisibleItemCount;
    private int mfirstVisibleItemPos;

    private long maxPosts;

    private long numPosts;
    private int maxPages;
    private int currentPage;

    private int movieID;
    private String otherUserID;

    MovieProfilePaginatingRecyclerAdapter mAdapterForMovieProfile;

    public PaginationAdapter mAdapter;

    public void otherUserProfilePaginationSetup(Context thisContext, FirebaseAuth firebaseAuth, FirebaseDatabase database,
                                                DatabaseReference baseRef, DatabaseReference postsKeysRef, RecyclerView feedRecycler, String otherUserID) {
        this.thisContext = thisContext;
        this.firebaseAuth = firebaseAuth;
        this.database = database;
        this.baseRef = baseRef;
        this.postsRef = postsKeysRef;
        this.feedRecycler = feedRecycler;
        this.movieID = movieID;
        this.otherUserID = otherUserID;
        orderedManager = new LinearLayoutManager(thisContext);

        setupDatabaseRefs();

        feedRecycler.setHasFixedSize(true);
        feedRecycler.setLayoutManager(orderedManager);
        feedRecycler.setItemAnimator(new DefaultItemAnimator());

        initPostLoad();
    }

    public void movieProfilePaginationSetup(Context thisContext, FirebaseAuth firebaseAuth, FirebaseDatabase database,
                                DatabaseReference baseRef, DatabaseReference postsKeysRef, RecyclerView feedRecycler, int movieID) {

        this.thisContext = thisContext;
        this.firebaseAuth = firebaseAuth;
        this.database = database;
        this.baseRef = baseRef;
        this.postsRef = postsKeysRef;
        this.feedRecycler = feedRecycler;
        this.movieID = movieID;
        orderedManager = new LinearLayoutManager(thisContext);

        setupDatabaseRefs();

        feedRecycler.setHasFixedSize(true);
        feedRecycler.setLayoutManager(orderedManager);
        feedRecycler.setItemAnimator(new DefaultItemAnimator());

        initPostLoad();

    }

    public void paginationSetup(Context thisContext, FirebaseAuth firebaseAuth, FirebaseDatabase database,
                                DatabaseReference baseRef, DatabaseReference postsKeysRef, RecyclerView feedRecycler) {

        this.thisContext = thisContext;
        this.firebaseAuth = firebaseAuth;
        this.database = database;
        this.baseRef = baseRef;
        this.postsRef = postsKeysRef;
        this.feedRecycler = feedRecycler;
        orderedManager = new LinearLayoutManager(thisContext);

        setupDatabaseRefs();

        feedRecycler.setHasFixedSize(true);
        feedRecycler.setLayoutManager(orderedManager);
        feedRecycler.setItemAnimator(new DefaultItemAnimator());

        initPostLoad();

    }

    public void setupDatabaseRefs()  {
        isLoading = true;
//        thisContext = this;

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                finishedCounting(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maxPosts = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void finishedCounting(long numPosts) {
        this.numPosts = numPosts;
        this.maxPages = (int) numPosts/POSTS_PER_LOAD;
        this.currentPage = 1;
    }

    private int getContextType() {
        if (thisContext instanceof UserProfileActivity) {
            return USER_PROFILE_ACTIVITY_TYPE;
        }
        else if (thisContext instanceof MovieProfileActivity) {
            return MOVIE_PROFILE_ACTIVITY_TYPE;
        } else if (thisContext instanceof  OtherUserProfile) {
            return OTHER_USER_PROFILE_ACTIVITY_TYPE;
        } else {
            return MISC_ACTIVITY_TYPE;
        }
    }

    public void initPostLoad() {

        activityType = getContextType();

        // load first set of posts
        Query keysQuery = postsRef.orderByChild("timestamp").limitToFirst(POSTS_PER_LOAD);

        if (activityType == USER_PROFILE_ACTIVITY_TYPE) {
            UserProfileActivity mActivity = (UserProfileActivity) thisContext;
            mAdapter = new SelfPaginationRecyclerAdapter(mActivity);
        } else if (activityType == MOVIE_PROFILE_ACTIVITY_TYPE) {
            mAdapter = new MovieProfilePaginatingRecyclerAdapter(thisContext, movieID);
            mAdapterForMovieProfile = (MovieProfilePaginatingRecyclerAdapter) mAdapter;
        } else if (activityType == OTHER_USER_PROFILE_ACTIVITY_TYPE) {
            OtherUserProfile mActivity = (OtherUserProfile) thisContext;
            mAdapter = new OtherUserProfilePaginationRecyclerAdapter(mActivity, otherUserID);
        }
        else {
            mAdapter = new PaginationRecyclerAdapter(thisContext);
        }

        feedRecycler.setAdapter(mAdapter);

        keysQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> dataIter = dataSnapshot.getChildren().iterator();
                int length = (int) dataSnapshot.getChildrenCount();

                if (length == POSTS_PER_LOAD) {
                    for (int i = 0; i < length - 2; i++) {
                        baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mAdapter.addItem(dataSnapshot.getValue(Post.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mAdapter.setLastTimeStamp(dataSnapshot.getValue(Post.class).getTime());
                            }
                            Log.e("last time", Long.toString(dataSnapshot.getValue(Post.class).getTime()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    for (int i = 0; i < length; i++) {
                        baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())  {
                                    mAdapter.addItem(dataSnapshot.getValue(Post.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                isLoading = false;

            }
        });

        isLoading = false;
        mAdapter.notifyDataSetChanged();

        Log.e("post list", mAdapter.getPostList().toString());

        feedRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            /**
             * When we change scroll states, store the current state
             * and check if the scroll is completed.
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            /**
             * When scrolling, update information about the state of
             * the items stored in the recycler view
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItem = orderedManager.getItemCount();
                currentVisibleItemCount = orderedManager.getChildCount();
                currentFirstVisibleItem = orderedManager.findFirstVisibleItemPosition();
            }

            /**
             * Check if scrolling is complete and, if so, load posts
             */
            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && currentScrollState == RecyclerView.SCROLL_STATE_IDLE) {

                    if (!isLoading && currentPage <= maxPages) {
                        loadPosts(mAdapter.getLastTimestamp());
                        isLoading = true;
                    }

                }
            }
        });

    }

    /**
     * When called, loads <= 15 posts, adds them to the adapter, and notifies
     * the adapter of the change.
     * @param lastTimestamp
     */
    public void loadPosts(long lastTimestamp) {

        Query loadQuery = postsRef.orderByChild("timestamp").startAt(mAdapter.getLastTimestamp()).limitToFirst(POSTS_PER_LOAD);

        ValueEventListener mListener = new ValueEventListener() { //query info from firebase and add it
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataIter = dataSnapshot.getChildren().iterator();
                int length = (int) dataSnapshot.getChildrenCount();

                if (length == POSTS_PER_LOAD) {
                    for (int i = 0; i < length - 2; i++) {
                        baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mAdapter.addItem(dataSnapshot.getValue(Post.class));
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(thisContext, "Unable to load posts", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mAdapter.setLastTimeStamp(dataSnapshot.getValue(Post.class).getTime());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(thisContext, "Error getting last timestamp.", Toast.LENGTH_LONG).show();

                        }
                    });
                } else { // there are less than 15 points, so we're done loading afterwards
                    for (int i = 0; i < length; i++) {
                        baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mAdapter.addItem(dataSnapshot.getValue(Post.class));
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(thisContext, "Unable to load posts", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                isLoading = false;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(thisContext, "Error getting last timestamp.", Toast.LENGTH_LONG).show();
                isLoading = false;

            }
        };

        loadQuery.addListenerForSingleValueEvent(mListener);
        loadQuery.removeEventListener(mListener);

        isLoading = false;
        mAdapter.notifyDataSetChanged();

        currentPage++; //increment current "page" number in order to check if we've reached the last page

    }
}
