package app.moov.moov.util;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import app.moov.moov.model.Post;

/**
 * Created by Sammy on 4/17/2018.
 *
 * Abstract class with abstract methods that all PaginationAdapters must
 * contain.
 */

public abstract class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public abstract void addItem(Post newPost);
    public abstract void setLastTimeStamp(long newTime);
    public abstract List<Post> getPostList();
    public abstract Long getLastTimestamp();

}
