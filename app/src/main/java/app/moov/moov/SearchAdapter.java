package app.moov.moov;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lisa on 10/03/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    Context context;
    ArrayList<String> userNameList;


    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.etUsername);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> userNameList) {
        this.context = context;
        this.userNameList = userNameList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.userName.setText(userNameList.get(position));

    }


    @Override
    public int getItemCount() {
        return userNameList.size();
    }
}
