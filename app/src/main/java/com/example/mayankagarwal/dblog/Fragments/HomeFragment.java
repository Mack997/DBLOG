package com.example.mayankagarwal.dblog.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayankagarwal.dblog.Adapters.PostRecyclerAdapter;
import com.example.mayankagarwal.dblog.Model.Post;
import com.example.mayankagarwal.dblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView postListView;
    private List<Post> postList;
    private PostRecyclerAdapter postRecyclerAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPage = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postList = new ArrayList<>();
        postListView = view.findViewById(R.id.post_list_view);
        postRecyclerAdapter = new PostRecyclerAdapter(postList);

        postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postListView.setAdapter(postRecyclerAdapter);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Boolean reachedLast = !recyclerView.canScrollVertically(-1);
                    if (reachedLast) {

                        loadMore();
                    }
                }
            });

        }
        loadFirst();
        return view;
    }

    public void loadFirst() {

        Query sortPosts = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);


        sortPosts.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (isFirstPage){
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();

                        Post post = doc.getDocument().toObject(Post.class).withId(postID);

                        if (isFirstPage) {

                            postList.add(post);

                        }else {

                            postList.add(0, post);

                        }
                        postRecyclerAdapter.notifyDataSetChanged();

                    }
                }
                isFirstPage = false;
            }
        });
    }


    public void loadMore(){

        Query loadMoreQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        loadMoreQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postID = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postID);
                            postList.add(post);
                            postRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });
    }
}