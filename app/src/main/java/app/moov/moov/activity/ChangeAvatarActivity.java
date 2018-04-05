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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import app.moov.moov.R;

public class ChangeAvatarActivity extends ToolbarBaseActivity {

    private ImageView ivCurrentAvatar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.navBar);
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
                // TODO Auto-generated catch block
                Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Toast.makeText(ChangeAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void chooseNewAvatar() {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GET_FROM_GALLERY);

    }

    private void confirmNewAvatar() {

        Bitmap newAvatar = ((BitmapDrawable)ivCurrentAvatar.getDrawable()).getBitmap();

        File f = new File(this.getCacheDir(), "avatar");
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            newAvatar.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();
            avatarRef.putBytes(bitmapdata);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(ChangeAvatarActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUIViews() {
        ivCurrentAvatar = (ImageView) findViewById(R.id.ivCurrentAvatar);
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
