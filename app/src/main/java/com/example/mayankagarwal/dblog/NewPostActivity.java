package com.example.mayankagarwal.dblog;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class NewPostActivity extends AppCompatActivity {


    private Uri postImageUri = null;

    private ImageView postImage ;

    Button addpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Toolbar mToolbar = findViewById(R.id.post_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton addPostImageBtn = findViewById(R.id.add_post_image_btn);
        postImage = findViewById(R.id.postImage);
        addpost = findViewById(R.id.addPostBtn);


        addPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)

                        .start(NewPostActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){

                postImageUri = result.getUri();
                postImage.setImageURI(postImageUri);


            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
