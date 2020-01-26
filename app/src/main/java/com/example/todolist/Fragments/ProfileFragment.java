package com.example.todolist.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.LoginActivity;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.example.todolist.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    SwitchCompat nightSwitch;
    TextView showEmailText, txtMyUploads;
    Button btnmyUploads, btnDisclaimer;
    String userId;
    MaterialButton btnLogout, btnEditPhoto;
    ProgressBar editPhotoProgressBar;

    SessionManager sessionManager;
    ContextThemeWrapper contextThemeWrapper;
    Bitmap bitmap;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef, mRef2;
    String TAG = "my";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sessionManager = new SessionManager(getContext());
        if (sessionManager.loadNightModeState() == true) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.darktheme);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        }

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // Inflate the layout for this fragment
        View view = localInflater.inflate(R.layout.fragment_profile, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mRef = firebaseDatabase.getReference("todo").child("users");
        user = firebaseAuth.getCurrentUser();

        userId = user.getUid();

        nightSwitch = view.findViewById(R.id.night_switch);
        showEmailText = view.findViewById(R.id.showEmail);
        btnLogout = view.findViewById(R.id.btnlogout);

        mRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showEmailText.setText(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                ReCreateApp();
            }
        });

        if (sessionManager.loadNightModeState() == true) {
            nightSwitch.setChecked(true);
        } else {
            nightSwitch.setChecked(false);
        }

        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sessionManager.setNightModeState(true);
                    nightSwitch.setChecked(true);

                } else {
                    sessionManager.setNightModeState(false);
                    nightSwitch.setChecked(false);

                }

                ReCreateApp();

            }
        });

        return view;
    }

    public void ReCreateApp() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

}
