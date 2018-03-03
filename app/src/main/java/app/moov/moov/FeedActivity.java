//package app.moov.moov;
//
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//
//public class FeedActivity extends AppCompatActivity {
//
//    private RecyclerView feedRecycler;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed2);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        setUIViews();
//    }
//
//    private void setUIViews() {
//
//        feedRecycler = (RecyclerView) findViewById(R.id.feedRecycler);
//        feedRecycler.setHasFixedSize(true);
//        feedRecycler.setLayoutManager(new LinearLayoutManager());
//
//    }
//
//}
