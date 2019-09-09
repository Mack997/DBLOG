package com.example.mayankagarwal.dblog.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mayankagarwal.dblog.Model.Post;
import com.example.mayankagarwal.dblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    public List<Post> postList;
    public Context context;

    public PostRecyclerAdapter(List<Post> postList){
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String currentUserID = mAuth.getCurrentUser().getUid();

        String desc_data = postList.get(position).getDescription();
        holder.setDescText(desc_data);

        String post_image_url = postList.get(position).getImage();
        String thumb_image_url = postList.get(position).getThumb_image();
        holder.setPostImage(post_image_url, thumb_image_url);
        Log.d("Image: ", post_image_url);


        final String fetchedPostId = postList.get(position).PostId;

        String user_id = postList.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                } else {

                }
            }
        });

        long milliseconds = postList.get(position).getTimestamp().getTime();
        String date = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();
        holder.setTime(date);


        firebaseFirestore.collection("Posts/" + fetchedPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (!documentSnapshot.isEmpty()){

                    int count = documentSnapshot.size();
                    holder.likeCount(count);

                }else {

                    holder.likeCount(0);
                }
            }
        });

        firebaseFirestore.collection("Posts/" + fetchedPostId + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    holder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_pressed_icon));
                }else{
                    holder.postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.like_not_pressed_icon));
                }
            }
        });


        holder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts/" + fetchedPostId + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){
                            Map<String, Object> likesMaps = new HashMap<>();

                            likesMaps.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + fetchedPostId + "/Likes").document(currentUserID).set(likesMaps);
                        }else {
                            firebaseFirestore.collection("Posts/" + fetchedPostId + "/Likes").document(currentUserID).delete();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView descView;
        private TextView postDate;
        private TextView userNameView;
        private CircleImageView userImageView;
        private ImageView postImageView;

        private ImageButton postLikeBtn;
        private TextView postLikeCount;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            postLikeBtn = mView.findViewById(R.id.likeBtnUnClicked);
        }


        public void setUserData(String username, String userImage){

            userNameView = mView.findViewById(R.id.userName);

            userImageView = mView.findViewById(R.id.userImage);


            Glide.with(context).load(userImage).into(userImageView);

            userNameView.setText(username);
        }


        public void setDescText(String descText){

            descView = mView.findViewById(R.id.postDescription);

            descView.setText(descText);
        }

        public void setPostImage(String downloadUri, String thumbUri){
            postImageView = mView.findViewById(R.id.fetchPostImage);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.load2);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumbUri))
                    .into(postImageView);
        }

        public void setTime(String date){
            postDate = mView.findViewById(R.id.postDate);

            postDate.setText(date);
        }

        public void likeCount(int count){
            postLikeCount = mView.findViewById(R.id.likeCount);
            postLikeCount.setText(count + " Likes");
        }
    }
}
