package app.moov.moov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private Button btnLogout;
    private FirebaseAuth firebaseAuth;
    //Reference to the recycler view
    private RecyclerView rvFeed;

    private RecyclerView.Adapter feedAdapter;
    private RecyclerView.LayoutManager feedManager;
    private DatabaseReference mDatabase;

    private ArrayList<String> testStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Posts"); //added this - Lisa
        setUIViews();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        //Assigning it to the ID on XML
        rvFeed = (RecyclerView) findViewById(R.id.recyclerView);
        rvFeed.setHasFixedSize(true);
        rvFeed.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Post, PostViewHolder> FBRA = new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                Post.class,
                R.layout.rv_row, //specifying the design of the cardview, which is the re_row.xml
                PostViewHolder.class,
                mDatabase
//                mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts")

        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {
                viewHolder.setMovieTitle(model.getMovieTitle());
                viewHolder.setMovieRating(model.getMovieRating());
                viewHolder.setMovieReview(model.getMovieReview());
            }
        };
        rvFeed.setAdapter(FBRA);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public PostViewHolder(View itemView) {
            super(itemView);
            View mView = itemView;
        }
        public void setMovieTitle(String movieTitle) {
            TextView movie_title = (TextView) itemView.findViewById(R.id.MovieTitle);
            movie_title.setText(movieTitle);
        }
        public void setMovieRating(String movieRating) {
            TextView movie_rating = (TextView) itemView.findViewById(R.id.MovieRating);
            movie_rating.setText(movieRating);
        }
        public void setMovieReview (String movieReview) {
            TextView movie_review = (TextView) itemView.findViewById(R.id.MovieReview);
            movie_review.setText(movieReview);
        }
    }

    private void logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Feed.this, MainActivity.class));
    }

    private void setUIViews(){

        testStrings.add("lorem");
        testStrings.add("ipsum");
        testStrings.add("placeholder");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        //rvFeed = (RecyclerView) findViewById(R.id.rvFeed);

        //improve performance if all posts have fixed size
        rvFeed.hasFixedSize();

        //set recycler view manager
        feedManager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(feedManager);

    }
}
