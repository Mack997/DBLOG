package com.example.mayankagarwal.dblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mayankagarwal.dblog.Adapters.UsersPostsAdapter;
import com.example.mayankagarwal.dblog.Model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView mDisplayImage;
    private TextView mDisplayName, mDisplayBio;
    private ProgressDialog mProgressDialog;

    private String user_id;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    private RecyclerView userPostListView;
    private List<Post> userPostList;
    private UsersPostsAdapter usersPostsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDisplayImage = findViewById(R.id.user_profile_image);
        mDisplayName = findViewById(R.id.user_display_name);
        mDisplayBio = findViewById(R.id.user_bio);

        Button mEditBtn = findViewById(R.id.edit_btn);
        mProgressDialog = new ProgressDialog(this);


        userPostList = new ArrayList<>();
        userPostListView = findViewById(R.id.post_list_view);
        usersPostsAdapter = new UsersPostsAdapter(userPostList);

        userPostListView.setLayoutManager(new GridLayoutManager(this,4));
        userPostListView.setAdapter(usersPostsAdapter);


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

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mDisplayImage);

                    }
                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "FIRESTORE retrieve error: " + error, Toast.LENGTH_SHORT).show();

                }
            }
        });


        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editPage = new Intent(ProfileActivity.this, EditProfile.class);
                startActivity(editPage);
                finish();
            }
        });

        loadPosts();

    }

    public void loadPosts() {

        Query sortPosts = firebaseFirestore.collection("Posts")
                .whereEqualTo("user_id",user_id)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        sortPosts.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e!=null) {
                    Log.e("Error: ", e.getMessage());
                }else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postID = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postID);

                            userPostList.add(post);
                            usersPostsAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            Intent back = new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(back);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(back);
        finish();
    }
}
