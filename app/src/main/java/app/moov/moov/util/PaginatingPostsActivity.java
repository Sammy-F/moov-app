package app.moov.moov.util;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.moov.moov.activity.MovieProfileActivity;
import app.moov.moov.activity.ToolbarBaseActivity;
import app.moov.moov.activity.UserProfileActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy F on 4/14/2018.
 */

/**
 * Abstract custom Activity to be used on pages with basic posts
 *
 * All classes that extend this should paginationSetup()
 */

public abstract class PaginatingPostsActivity extends ToolbarBaseActivity {

    private final int MISC_ACTIVITY_TYPE = 0;
    private final int USER_PROFILE_ACTIVITY_TYPE = 1;
    private final int MOVIE_PROFILE_ACTIVITY_TYPE = 2;

    private int activityType;

    // Must initialzie to work
    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager orderedManager;
    private Context thisContext;

    // All of this is handled by the abstract class
    private String lastTime;
    private boolean isLoading;

    private final int POSTS_PER_LOAD = 15;
    private int mTotalItemCount;
    private int mVisibleItemCount;
    private int mfirstVisibleItemPos;

    private long maxPosts;

    private long numPosts;
    private int maxPages;
    private int currentPage;

    private int movieID;

    MovieProfilePaginatingRecyclerAdapter mAdapterForMovieProfile;
//
//    public PaginationRecyclerAdapter mAdapter;

    public PaginationAdapter mAdapter;

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
        } else {
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

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                mTotalItemCount = (int) numPosts;
                totalItem = orderedManager.getItemCount();
                currentVisibleItemCount = orderedManager.getChildCount();
                currentFirstVisibleItem = orderedManager.findFirstVisibleItemPosition();

//                if ((!isLoading) && (totalItem <= (currentVisibleItemCount + currentFirstVisibleItem)) && (currentPage <= maxPages)
////                if ((!isLoading) && (mTotalItemCount <= (mVisibleItemCount + mfirstVisibleItemPos)) && (mAdapter.getItemCount() <= maxPosts)
//                        && mfirstVisibleItemPos >= 0) {
//
//                    loadPosts(mAdapter.getLastTimestamp());
//                    isLoading = true;
//
//                }

            }

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

    public void loadPosts(long lastTimestamp) {
        Log.e("firing loader post", "loadPosts() fired");

        Query loadQuery = postsRef.orderByChild("timestamp").startAt(mAdapter.getLastTimestamp()).limitToFirst(POSTS_PER_LOAD);

        Log.e("query created", "created query");
        Log.e("query info", loadQuery.toString());

        ValueEventListener mListener = new ValueEventListener() {
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
                                if (dataSnapshot.exists()) {
                                    mAdapter.addItem(dataSnapshot.getValue(Post.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                isLoading = false;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("was cancelled", "cancelled new post key load");
                isLoading = false;

            }
        };

        loadQuery.addListenerForSingleValueEvent(mListener);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
            }
        }, 100);

        loadQuery.removeEventListener(mListener);

        isLoading = false;
        mAdapter.notifyDataSetChanged();

        currentPage++;

    }

    public MovieProfilePaginatingRecyclerAdapter getMovieProfilePaginatingRecyclerAdapter() {
        if (mAdapterForMovieProfile != null) {
            return mAdapterForMovieProfile;
        } else {
            return null;
        }
    }

}
