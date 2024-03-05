package com.example.appinstagram.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appinstagram.R;
import com.example.appinstagram.adapter.AdapterFeed;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class FeedFragment extends Fragment {

    private List<Feed> listaFeed;
    private RecyclerView recyclerFeed;
    private AdapterFeed adapterFeed;
    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference feedRef;
    private String idUsuarioLogado;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        idUsuarioLogado = UsuarioFirebase.getID();
        feedRef = ConfigFireBase
                .getFireBaseDataBase()
                .child("feed")
                .child(idUsuarioLogado);

        listaFeed    = new ArrayList<>();
        adapterFeed  = new AdapterFeed(listaFeed, getActivity());
        recyclerFeed = view.findViewById(R.id.recyclerFeed);

        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(
                getActivity()
        ));
        recyclerFeed.setAdapter(adapterFeed);
        return view;
    }

    private void listarFeed(){
        valueEventListenerFeed = feedRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds  : snapshot.getChildren()){

                            listaFeed.add(ds.getValue(Feed.class));
                        }
                        Collections.reverse(listaFeed);
                        adapterFeed.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListenerFeed);
    }
}