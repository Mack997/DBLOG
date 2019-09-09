
package com.example.mayankagarwal.dblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mayankagarwal.dblog.Model.Post;
import com.example.mayankagarwal.dblog.R;
import com.example.mayankagarwal.dblog.SingleUserPostView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class UsersPostsAdapter extends RecyclerView.Adapter<UsersPostsAdapter.ViewHolder> {

    public List<Post> userPostList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    public UsersPostsAdapter(List<Post> postList){
        this.userPostList = postList;
    }

    @NonNull
    @Override
    public UsersPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_posts_list_item, parent, false);
        context = parent.getContext();

        return new UsersPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersPostsAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String post_image_url = userPostList.get(position).getImage();
        final String thumb_image_url = userPostList.get(position).getThumb_image();
        holder.setPostImage(post_image_url, thumb_image_url);

        final String post_desc = userPostList.get(position).getDescription();

        final String user_id = userPostList.get(position).getUser_id();

        final String singlePostFetchId = userPostList.get(position).PostId;

        long milliseconds = userPostList.get(position).getTimestamp().getTime();
        final String date = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String userName = task.getResult().getString("name");
                            String userImage = task.getResult().getString("image");


                            Bundle bundle = new Bundle();
                            bundle.putString("userID", user_id);
                            bundle.putString("username", userName);
                            bundle.putString("userImage", userImage);
                            bundle.putString("date", date);
                            bundle.putString("post_image", post_image_url);
                            bundle.putString("thumb_image_url", thumb_image_url);
                            bundle.putString("post_desc", post_desc);
                            bundle.putString("singlePostId",singlePostFetchId);

                            Intent singlePosts = new Intent(context, SingleUserPostView.class);
                            singlePosts.putExtras(bundle);
                            context.startActivity(singlePosts);

                        } else {

                        }
                    }
                });



            }
        });

    }

    @Override
    public int getItemCount() {
        return userPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView postImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPostImage(String downloadUri, String thumbUri){
            postImageView = mView.findViewById(R.id.users_posts);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.load2);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumbUri))
                    .into(postImageView);
        }


    }
}