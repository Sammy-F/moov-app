package app.moov.moov.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import app.moov.moov.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sammy 4/4/2018
 * Modified by Sammy 4/5/2018
 *
 * Class that handles the page for changing
 * the user's avatar in the settings panel.
 */
public class ChangeAvatarActivity extends ToolbarBaseActivity {

    private CircleImageView ivCurrentAvatar;
    private TextView tvAvatarInfo;
    private Button btnAvChange;
    private Button btnAvConfirm;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private String userID;

    private StorageReference baseStorageRef;
    private StorageReference avatarRef;

    private String avatarURL;

    public static final int GET_FROM_GALLERY = 3;
    final long ONE_MEGABYTE = 1024 * 1024;

    /**
     * Initializes all of the object (like toolbar, bottom nav bar), sets up data references
     * and specifies which layout XML to use
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
        BottomNavigationViewHelper.disableShiftMode(navBar);
        setUpNavBar(navBar);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        baseStorageRef = firebaseStorage.getReference();
        avatarRef = baseStorageRef.child("images").child("avatars").child(userID + ".png");
        avatarURL = avatarRef.getDownloadUrl().toString();

        setUIViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                ivCurrentAvatar.setImageBitmap(bitmap);
                tvAvatarInfo.setText(getString(R.string.new_avatar));
                btnAvConfirm.setEnabled(true);
            } catch (FileNotFoundException e) {
                Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize and run an intent to get the user's image
     * input for the new avatar.
     */
    private void chooseNewAvatar() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GET_FROM_GALLERY);
    }

    /**
     * Method called when avatar confirmation button is clicked.
     * Get the scaled image from the avatar ImageView, convert it
     * to a Bitmap, then to a bytes[], and upload it to
     * the user's avatar reference in Firebase Storage.
     */
    private void confirmNewAvatar() {

        Bitmap newAvatar = ((BitmapDrawable)ivCurrentAvatar.getDrawable()).getBitmap();

        File f = new File(this.getCacheDir(), "avatar");
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            newAvatar.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();
            avatarRef.putBytes(bitmapdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Intent intent = new Intent(ChangeAvatarActivity.this, FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Set up the Views in the Activity.
     */
    private void setUIViews() {
        ivCurrentAvatar = (CircleImageView) findViewById(R.id.ivCurrentAvatar);
        tvAvatarInfo = (TextView) findViewById(R.id.tvAvInfo);
        btnAvChange = (Button) findViewById(R.id.btnAvChange);
        btnAvConfirm = (Button) findViewById(R.id.btnConfirmNew);

        avatarRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap currentAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivCurrentAvatar.setImageBitmap(currentAvatar);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT);

            }
        });

        btnAvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseNewAvatar();
            }
        });

        btnAvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmNewAvatar();
            }
        });
    }

}
