package com.Activity.socialconnect;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private List<ModelBlog>  userList;

    private FirebaseFirestore firebaseFirestore;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userList = new ArrayList<>();

        homeAdapter = new HomeAdapter(userList);

        recyclerView = view.findViewById(R.id.home_frag_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(homeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        firebaseFirestore = FirebaseFirestore.getInstance();

        Query firstQuery = firebaseFirestore.collection("UserData").orderBy("blogUserId", Query.Direction.DESCENDING);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {

                    Log.e("RecyclerError ", e.getMessage());

                } else {


                    assert queryDocumentSnapshots != null;
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        ModelBlog modelBlog = doc.getDocument().toObject(ModelBlog.class);
                        userList.add(modelBlog);
                        homeAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

}
