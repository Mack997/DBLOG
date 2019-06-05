package com.example.mayankagarwal.dblog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mDisplayImage;
    private EditText mDisplayName;
    private ProgressDialog mProgressDialog;

    private Uri mainImageURI = null;

    private String user_id;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    public SettingsActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = findViewById(R.id.settings_profile_image);
        mDisplayName = findViewById(R.id.settings_display_name);
        FloatingActionButton camera_btn = findViewById(R.id.settings_camera_btn);

        Button mSaveBtn = findViewById(R.id.update_btn);
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
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mDisplayName.setText(name);
                        mDisplayName.setSelection(mDisplayName.getText().length());

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SettingsActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mDisplayImage);

                    }
                }else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "FIRESTORE retrieve error: " + error, Toast.LENGTH_SHORT).show();

                }
            }
        });


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();
            }
        });



        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SettingsActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else {
                        BringImagePicker();
                    }
                }else {
                    BringImagePicker();
                }
            }
        });
    }

    public void updateDetails(){

        final String user_name = mDisplayName.getText().toString();

        if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

            mProgressDialog.setTitle("Uploading Image");
            mProgressDialog.setMessage("please wait while the image is being uploaded");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

            image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Uri downloadUri = uri;
                                String download_uri = downloadUri.toString();
                                Map<String, String> userMap = new HashMap<>();

                                userMap.put("name", user_name);
                                userMap.put("image", download_uri);

                                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            mProgressDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Account updated", Toast.LENGTH_SHORT).show();

                                        }else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(SettingsActivity.this, "FIRESTORE Error: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Image Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            mProgressDialog.hide();
            Toast.makeText(SettingsActivity.this, "Please fill the name", Toast.LENGTH_SHORT).show();
        }
    }

    public void BringImagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SettingsActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){

                mainImageURI = result.getUri();
                mDisplayImage.setImageURI(mainImageURI);


            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            Intent back = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(back);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(back);
        finish();
    }
}
