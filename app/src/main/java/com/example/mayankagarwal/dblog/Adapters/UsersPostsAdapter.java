package com.example.mayankagarwal.dblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mayankagarwal.dblog.Model.Post;
import com.example.mayankagarwal.dblog.R;

import java.util.List;

public class UsersPostsAdapter extends RecyclerView.Adapter<UsersPostsAdapter.ViewHolder> {

    public List<Post> userPostList;
    public Context context;

    public UsersPostsAdapter(List<Post> postList){
        this.userPostList = postList;
    }

    @NonNull
    @Override
    public UsersPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        context = parent.getContext();

        return new UsersPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersPostsAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        String post_image_url = userPostList.get(position).getImage();
        String thumb_image_url = userPostList.get(position).getThumb_image();
        holder.setPostImage(post_image_url, thumb_image_url);

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
            postImageView = mView.findViewById(R.id.fetchPostImage);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.load2);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumbUri))
                    .into(postImageView);
        }


    }
}

