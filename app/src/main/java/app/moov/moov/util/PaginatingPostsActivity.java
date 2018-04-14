package app.moov.moov.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.moov.moov.activity.ToolbarBaseActivity;
import app.moov.moov.model.Post;

/**
 * Created by Sammy F on 4/14/2018.
 */

/**
 * Abstract custom Activity to be used on pages with basic posts
 *
 * All classes that extend this must initialize feedRecycler
 */

public abstract class PaginatingPostsActivity extends ToolbarBaseActivity {

    // Must initialzie to work
    public RecyclerView feedRecycler;
    public FirebaseDatabase database;
    public DatabaseReference baseRef;
    public DatabaseReference postsRef;
    public FirebaseAuth firebaseAuth;
    public LinearLayoutManager orderedManager;
    public String uid;
    public Context thisContext;

    // All of this is handled by the abstract class
    public String lastTime;
    public boolean isLoading;

    public final int POSTS_PER_LOAD = 15;
    public int mTotalItemCount;
    public int mVisibleItemCount;
    public int mfirstVisibleItemPos;

    public long maxPosts;

    public long numPosts;
    public int maxPages;
    public int currentPage;

    public PaginationRecyclerAdapter mAdapter;

    public void setupDatabaseRefs()  {
        isLoading = true;
        thisContext = this;

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

    public void initPostLoad() {

        // load first set of posts
        Query keysQuery = postsRef.orderByChild("timestamp").limitToFirst(POSTS_PER_LOAD);

        mAdapter = new PaginationRecyclerAdapter(thisContext);

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
                                mAdapter.addItem(dataSnapshot.getValue(Post.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mAdapter.setLastTimeStamp(dataSnapshot.getValue(Post.class).getTime());
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
                                mAdapter.addItem(dataSnapshot.getValue(Post.class));
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
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                mTotalItemCount = (int) numPosts;
                mTotalItemCount = orderedManager.getItemCount();
                mVisibleItemCount = orderedManager.getChildCount();
                mfirstVisibleItemPos = orderedManager.findFirstVisibleItemPosition();

                if ((!isLoading) && (mTotalItemCount <= (mVisibleItemCount + mfirstVisibleItemPos)) && (currentPage <= maxPages)
//                if ((!isLoading) && (mTotalItemCount <= (mVisibleItemCount + mfirstVisibleItemPos)) && (mAdapter.getItemCount() <= maxPosts)
                        && mfirstVisibleItemPos >= 0) {

                    loadPosts(mAdapter.getLastTimestamp());
                    isLoading = true;

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
                                mAdapter.addItem(dataSnapshot.getValue(Post.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    baseRef.child("Posts").child(dataIter.next().getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mAdapter.setLastTimeStamp(dataSnapshot.getValue(Post.class).getTime());
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
                                mAdapter.addItem(dataSnapshot.getValue(Post.class));
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
        loadQuery.removeEventListener(mListener);

        isLoading = false;
        mAdapter.notifyDataSetChanged();

        currentPage++;

    }

}
