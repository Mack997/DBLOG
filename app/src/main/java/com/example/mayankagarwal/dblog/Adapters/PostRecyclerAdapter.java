package com.example.mayankagarwal.dblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mayankagarwal.dblog.Model.Post;
import com.example.mayankagarwal.dblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private FirebaseFirestore firebaseFirestore;

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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String desc_data = postList.get(position).getDescription();
        holder.setDescText(desc_data);

        String post_image_url = postList.get(position).getImage();
        holder.setPostimage(post_image_url);


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

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView descView;
        private TextView postDate;
        private TextView usernameView;
        private CircleImageView userImageView;
        private ImageView postImageView;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }


        public void setUserData(String username, String userImage){

            descView = mView.findViewById(R.id.userName);

            userImageView = mView.findViewById(R.id.userImage);


            Glide.with(context).load(userImage).into(userImageView);

            descView.setText(username);
        }


        public void setDescText(String descText){

            descView = mView.findViewById(R.id.postDescription);

            descView.setText(descText);
        }

        public void setPostimage(String downloadUri){
            postImageView = mView.findViewById(R.id.fetchPostImage);


            Glide.with(context).load(downloadUri).into(postImageView);
        }

        public void setTime(String date){
            postDate = mView.findViewById(R.id.postDate);

            postDate.setText(date);


        }
    }
}
