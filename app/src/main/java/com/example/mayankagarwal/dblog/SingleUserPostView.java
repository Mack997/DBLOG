package com.example.mayankagarwal.dblog;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleUserPostView extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user_post_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CircleImageView userImageview = findViewById(R.id.singleUserImage);
        TextView userName = findViewById(R.id.singleUserName);
        TextView postDate = findViewById(R.id.singlePostDate);
        TextView postDesc = findViewById(R.id.singleUserPostDescription);
        ImageView postImage = findViewById(R.id.singleUserPostImage);
        final ImageButton likeBtn = findViewById(R.id.singleLikeBtnUnClicked);
        final TextView likesCount = findViewById(R.id.singleLikeCount);


        Toolbar mToolbar = findViewById(R.id.single_posts_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("username");
        final String currentUserID = bundle.getString("userID");
        String userImage = bundle.getString("userImage");
        String date = bundle.getString("date");
        String post_image_url = bundle.getString("post_image");
        String thumb_image_url = bundle.getString("thumb_image_url");
        String post_desc = bundle.getString("post_desc");
        final String postID = bundle.getString("singlePostId");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.load2);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(post_image_url)
                .thumbnail(Glide.with(this).load(thumb_image_url))
                .into(postImage);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(userImage)
                .into(userImageview);

        postDesc.setText(post_desc);
        userName.setText(name);
        postDate.setText(date);


        firebaseFirestore.collection("Posts/" + postID + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (!documentSnapshot.isEmpty()){

                    int count = documentSnapshot.size();
                    likesCount.setText(count + " Likes");

                }else {

                    likesCount.setText(0 + " Likes");
                }
            }
        });

        firebaseFirestore.collection("Posts/" + postID + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    likeBtn.setImageDrawable(getDrawable(R.drawable.like_pressed_icon));
                }else{
                    likeBtn.setImageDrawable(getDrawable(R.drawable.like_not_pressed_icon));
                }
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + postID + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){
                            Map<String, Object> likesMaps = new HashMap<>();

                            likesMaps.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + postID + "/Likes").document(currentUserID).set(likesMaps);
                        }else {
                            firebaseFirestore.collection("Posts/" + postID + "/Likes").document(currentUserID).delete();
                        }
                    }
                });
            }
        });

    }
}
