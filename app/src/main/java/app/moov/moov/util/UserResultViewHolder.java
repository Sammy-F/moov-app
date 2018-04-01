package app.moov.moov.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import app.moov.moov.R;

/**
 * Created by Sammy on 3/31/2018.
 */

//VIEWHOLDER CLASS FOR USERS
public class UserResultViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    private String thisUid;
    public UserResultViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setUsername(String userName) {
        TextView tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
        tvUserName.setText(userName);
    }

    public void setUID(String UID) {
        this.thisUid = UID;
    }

    public String getUid() { return thisUid; }
}