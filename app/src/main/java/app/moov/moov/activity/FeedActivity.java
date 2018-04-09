package app.moov.moov.activity;

/**
 * Created by Sammy on 3/3/18.
 * Loads and displays the feed
 * and manages user interactions
 * with the toolbar.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.moov.moov.model.Post;
import app.moov.moov.R;
import app.moov.moov.util.PaginationRecyclerAdapter;

public class FeedActivity extends ToolbarBaseActivity {

    private RecyclerView feedRecycler;
    private FirebaseDatabase database;
    private DatabaseReference baseRef;
    private DatabaseReference postsRef;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager orderedManager;
    private String uid;
    private Context thisContext;

    private String lastTime;
    private boolean isLoading;

    private PaginationRecyclerAdapter mAdapter;

    private final int POSTS_PER_LOAD = 15;
    private int mTotalItemCount;
    private int mVisibleItemCount;
    private int mfirstVisibleItemPos;

    private long maxPosts;

    private long numPosts;
    private int maxPages;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarSetup(toolbar);

        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        setUpNavBar(navBar);

        isLoading = true;

        thisContext = this;

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        baseRef = database.getReference();
        postsRef = baseRef.child("Users").child(uid).child("Feed");

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

        setUIViews();
    }

    private void finishedCounting(long numPosts) {
        this.numPosts = numPosts;
        this.maxPages = (int) numPosts/POSTS_PER_LOAD;
        this.currentPage = 1;
    }

    private void loadPosts(long lastTimestamp) {
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
//                for (DataSnapshot nextPost : dataSnapshot.getChildren()) {
//                    Log.e("post key found", nextPost.getKey());
//                    baseRef.child("Posts").child(nextPost.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            mAdapter.addItem(dataSnapshot.getValue(Post.class));
//                            Log.e("new post found", dataSnapshot.getValue(Post.class).toString());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
                isLoading = false;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("was cancelled", "cancelled new post key load");
                isLoading = false;

            }
        };

        loadQuery.addListenerForSingleValueEvent(mListener);
//        loadQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("added listener", "value event listener added to query");
//                for (DataSnapshot nextPost : dataSnapshot.getChildren()) {
//                    Log.e("post key found", nextPost.getKey());
//                    baseRef.child("Posts").child(nextPost.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            mAdapter.addItem(dataSnapshot.getValue(Post.class));
//                            Log.e("new post found", dataSnapshot.getValue(Post.class).toString());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                isLoading = false;
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                Log.e("was cancelled", "cancelled new post key load");
//                isLoading = false;
//
//            }
//        });

        loadQuery.removeEventListener(mListener);

        isLoading = false;
        mAdapter.notifyDataSetChanged();

        currentPage++;

    }

    /**
     * Initialize layout objects, called in onCreate
     */
    private void setUIViews() {
        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
        feedRecycler.setHasFixedSize(true);

        orderedManager = new LinearLayoutManager(this);
//        orderedManager.setReverseLayout(true);
//        orderedManager.setStackFromEnd(true);

        feedRecycler.setLayoutManager(orderedManager);
        feedRecycler.setItemAnimator(new DefaultItemAnimator());

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
//                for (DataSnapshot post : dataSnapshot.getChildren()) {
//                    Log.e("found", "found post key");
//                    baseRef.child("Posts").child(post.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            postList.add(dataSnapshot.getValue(Post.class));
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                mAdapter.addAll(postList);
                Log.e("numitems post", Integer.toString(mAdapter.getItemCount()));
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
}
