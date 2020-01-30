package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapters.CardSubListAdapter;
import com.example.todolist.Model.CardSubListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SingleCardActivity extends AppCompatActivity {

    RecyclerView showSubtaskRecycler;
    TextView tvTaskDate;
    EditText editAddSubTask;
    ImageButton btnSubTaskAdd;
    CardSubListAdapter cardSubListAdapter;
    ArrayList<CardSubListModel> cardSubListModels;
    ProgressBar subTaskAddProgressBar;

    ValueEventListener valueEventListener;

    SessionManager sessionManager;
    String taskDate, TaskKey;

    DatabaseReference mRef;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.loadNightModeState() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_single_card);

        showSubtaskRecycler = findViewById(R.id.showSubtaskRecycler);
        tvTaskDate = findViewById(R.id.tvTaskDate);
        editAddSubTask = findViewById(R.id.editAddSubTask);
        btnSubTaskAdd = findViewById(R.id.btnSubTaskAdd);
        subTaskAddProgressBar = findViewById(R.id.subTaskAddProgressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        Intent intent = getIntent();
        taskDate = intent.getExtras().getString("taskDate");
        TaskKey = intent.getExtras().getString("subTaskKey");

        mRef = FirebaseDatabase.getInstance().getReference("todo").child("users").child(userId).child("tasks").child("taskList").child(TaskKey).child("subTasksList");

        cardSubListModels = new ArrayList<>();
        cardSubListAdapter = new CardSubListAdapter(getApplicationContext(), cardSubListModels);

        tvTaskDate.setText(taskDate);

        btnSubTaskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEditAddSubTask = editAddSubTask.getText().toString();

                btnSubTaskAdd.setVisibility(View.GONE);
                subTaskAddProgressBar.setVisibility(View.VISIBLE);

                if (strEditAddSubTask.equals("")){
                    editAddSubTask.setError("Please enter task to add");
                }
                else{
                    String subTaskKey = mRef.push().getKey();
                    mRef.child(subTaskKey).child("task").setValue(strEditAddSubTask);
                    mRef.child(subTaskKey).child("operation").setValue("pending");
                    editAddSubTask.setText("");

                }
                btnSubTaskAdd.setVisibility(View.VISIBLE);
                subTaskAddProgressBar.setVisibility(View.GONE);
            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardSubListModels.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    try {
                        cardSubListModels.add(new CardSubListModel(dataSnapshot1.child("task").getValue().toString(), dataSnapshot1.child("operation").getValue().toString(), TaskKey, dataSnapshot1.getKey()));
                        cardSubListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addValueEventListener(valueEventListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        showSubtaskRecycler.setLayoutManager(linearLayoutManager);
        showSubtaskRecycler.setAdapter(cardSubListAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRef.removeEventListener(valueEventListener);

    }

}
