package app.moov.moov.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import app.moov.moov.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sammy on 3/31/2018.
 *
 * ViewHolder that displays information about a given
 * user in the results page.
 */

//VIEWHOLDER CLASS FOR USERS
public class UserResultViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    private String thisUid;
    private CircleImageView ivAvatar;
    public UserResultViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivUserIcon);
    }

    public void setUsername(String userName) {
        TextView tvUserName = (TextView) itemView.findViewById(R.id.tvUsername);
        tvUserName.setText(userName);
    }

    public void setUID(String UID) {
        this.thisUid = UID;
    }

    public String getUid() { return thisUid; }

    public CircleImageView getIvAvatar() { return ivAvatar; }

    public void setFullName(String name) {
        TextView tvFullName = (TextView) itemView.findViewById(R.id.tvFullname);
        tvFullName.setText(name);
    }
}