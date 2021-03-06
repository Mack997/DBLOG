package com.example.mayankagarwal.dblog;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfile extends AppCompatActivity {


    private CircleImageView mDisplayImage;
    private EditText mDisplayName, mDisplayBio;
    private ProgressDialog mProgressDialog;

    private Uri mainImageURI = null;

    private String user_id;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDisplayImage = findViewById(R.id.edit_profile_image);
        mDisplayName = findViewById(R.id.edit_display_name);
        mDisplayBio = findViewById(R.id.edit_bio);

        Button mSavebtn = findViewById(R.id.update_btn);
        mProgressDialog = new ProgressDialog(this);

        //Toolbar
        Toolbar mToolbar = findViewById(R.id.settings_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String bio = task.getResult().getString("bio");

                        mDisplayName.setText(name);
                        mDisplayBio.setText(bio);
                        mDisplayBio.setSelection(mDisplayBio.length());
                        mDisplayName.setSelection(mDisplayName.length());


                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(EditProfile.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mDisplayImage);

                    }
                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(EditProfile.this, "FIRESTORE retrieve error: " + error, Toast.LENGTH_SHORT).show();

                }
            }
        });

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();
            }
        });

        mDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
    }

    public void updateDetails() {

        final String user_name = mDisplayName.getText().toString();
        final String user_bio = mDisplayBio.getText().toString();


        if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_bio)) {

            mProgressDialog.setTitle("Uploading Image");
            mProgressDialog.setMessage("please wait while the image is being uploaded");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

            image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Uri downloadUri = uri;
                                String download_uri = downloadUri.toString();

                                String device_token = FirebaseInstanceId.getInstance().getToken();

                                Map<String, String> userMap = new HashMap<>();

                                userMap.put("name", user_name);
                                userMap.put("bio", user_bio);
                                userMap.put("image", download_uri);
                                userMap.put("device_token", device_token);

                                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            mProgressDialog.dismiss();
                                            back();
                                            Toast.makeText(EditProfile.this, "Account updated", Toast.LENGTH_SHORT).show();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(EditProfile.this, "Fire-store Error: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(EditProfile.this, "Image Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            mProgressDialog.hide();
            Toast.makeText(EditProfile.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
    }

    public void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfile.this);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                mDisplayImage.setImageURI(mainImageURI);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            back();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    public void back(){
        Intent back = new Intent(EditProfile.this, ProfileActivity.class);
        startActivity(back);
        finish();
    }
}
