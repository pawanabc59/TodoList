package com.example.todolist.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.Adapters.CardListAdapter;
import com.example.todolist.Model.CardListModel;
import com.example.todolist.R;
import com.example.todolist.SessionManager;
import com.example.todolist.UploadTaskActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    SessionManager sessionManager;
    ContextThemeWrapper contextThemeWrapper;
    RecyclerView homeRecyclerView;
    CardListAdapter cardListAdapter;
    ArrayList<CardListModel> cardListModels;
    FloatingActionButton btnAddTask;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;
    ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        if (sessionManager.loadNightModeState() == true) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.darktheme);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        }

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // Inflate the layout for this fragment
        View view = localInflater.inflate(R.layout.fragment_home, container, false);

        homeRecyclerView = view.findViewById(R.id.homeRecyclerView);
        btnAddTask = view.findViewById(R.id.btnAddTask);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        mRef = firebaseDatabase.getReference("todo").child("users").child(userId).child("tasks").child("taskList");

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadTaskActivity.class);
                getActivity().startActivity(intent);
            }
        });

        cardListModels = new ArrayList<>();
        cardListAdapter = new CardListAdapter(getContext(), cardListModels);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardListModels.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    cardListModels.add(new CardListModel(dataSnapshot1.child("date").getValue().toString(), dataSnapshot1.getKey()));
                    cardListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addValueEventListener(valueEventListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        homeRecyclerView.setLayoutManager(linearLayoutManager);
        homeRecyclerView.setAdapter(cardListAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRef.removeEventListener(valueEventListener);
    }
}
