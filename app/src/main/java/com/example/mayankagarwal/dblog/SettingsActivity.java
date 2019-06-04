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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mDisplayImage;
    private EditText mDisplayName;
    private ProgressDialog mProgressDialog;

    private Uri mainImageURI = null;

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


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name = mDisplayName.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                    mProgressDialog.setTitle("Uploading Image");
                    mProgressDialog.setMessage("please wait while the image is being uploaded");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    String user_id = mAuth.getCurrentUser().getUid();

                    StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){

                                String download_uri = task.getResult().getStorage().getDownloadUrl().toString();


                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();

                        }
                    });

                }

                mProgressDialog.dismiss();


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
