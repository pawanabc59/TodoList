package com.example.todolist.Fragments;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Adapters.CardSubListAdapter;
import com.example.todolist.Model.CardSubListModel;
import com.example.todolist.R;
import com.example.todolist.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TodayFragment extends Fragment {

    RecyclerView showTodaySubtaskRecycler;
    EditText editTodayAddSubTask;
    ImageButton btnTodaySubTaskAdd, btnAddFirstTaskToday;
    CardSubListAdapter cardSubListAdapter;
    ArrayList<CardSubListModel> cardSubListModels;
    ProgressBar todaySubTaskAddProgressBar;
    TextView noTask;

    ValueEventListener valueEventListener, valueEventListener2, valueEventListener3;

    SessionManager sessionManager;
    ContextThemeWrapper contextThemeWrapper;
    SimpleDateFormat simpleDateFormat;
    FirebaseUser user;
    DatabaseReference mRef, mRef2, mRef3;
    String userId, currentDate, parentTaskKey="";
    Query query;
    TextView heading;

    String TAG = "my";

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
        View view = localInflater.inflate(R.layout.fragment_today, container, false);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = simpleDateFormat.format(new Date());

//        Toast.makeText(getContext(), currentDate, Toast.LENGTH_SHORT).show();

        showTodaySubtaskRecycler = view.findViewById(R.id.showTodaySubtaskRecycler);
        editTodayAddSubTask = view.findViewById(R.id.editTodayAddSubTask);
        btnTodaySubTaskAdd = view.findViewById(R.id.btnTodaySubTaskAdd);
        todaySubTaskAddProgressBar = view.findViewById(R.id.todaySubTaskAddProgressBar);
        heading = view.findViewById(R.id.tvTodayTaskDate);
        noTask = view.findViewById(R.id.noTask);
        btnAddFirstTaskToday = view.findViewById(R.id.btnAddFirstTaskToday);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        mRef = FirebaseDatabase.getInstance().getReference("todo").child("users").child(userId).child("tasks").child("taskList");

        cardSubListModels = new ArrayList<>();
        cardSubListAdapter = new CardSubListAdapter(getContext(), cardSubListModels);

        valueEventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()){
                    btnTodaySubTaskAdd.setEnabled(false);
                    showTodaySubtaskRecycler.setVisibility(View.GONE);
                    noTask.setVisibility(View.VISIBLE);
                }
                else {
                    btnTodaySubTaskAdd.setEnabled(true);
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    Log.d(TAG, "onDataChange: It comes before the if block");

                        if (!dataSnapshot1.child("date").getValue().toString().equals(currentDate)) {
                            showTodaySubtaskRecycler.setVisibility(View.GONE);
                            noTask.setVisibility(View.VISIBLE);
                            btnTodaySubTaskAdd.setEnabled(false);
//                            btnTodaySubTaskAdd.setVisibility(View.GONE);
//                            btnAddFirstTaskToday.setVisibility(View.VISIBLE);
//                            btnAddFirstTaskToday.setEnabled(true);
                        }
                        if (dataSnapshot1.child("date").getValue().toString().equals(currentDate)) {
                            showTodaySubtaskRecycler.setVisibility(View.VISIBLE);
                            noTask.setVisibility(View.GONE);
                            btnTodaySubTaskAdd.setEnabled(true);
//                            btnTodaySubTaskAdd.setVisibility(View.VISIBLE);
//                            btnAddFirstTaskToday.setEnabled(false);
//                            btnAddFirstTaskToday.setVisibility(View.GONE);

                            parentTaskKey = dataSnapshot1.getKey();

                            mRef.child(dataSnapshot1.getKey()).child("subTasksList").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    cardSubListModels.clear();
                                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                                        try {
                                            cardSubListModels.add(new CardSubListModel(dataSnapshot3.child("task").getValue().toString(), dataSnapshot3.child("operation").getValue().toString(), dataSnapshot1.getKey(), dataSnapshot3.getKey()));
                                            cardSubListAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addValueEventListener(valueEventListener3);

        btnTodaySubTaskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEditAddSubTask = editTodayAddSubTask.getText().toString();

                btnTodaySubTaskAdd.setVisibility(View.GONE);
                todaySubTaskAddProgressBar.setVisibility(View.VISIBLE);

                if (strEditAddSubTask.equals("")) {
                    editTodayAddSubTask.setError("Please enter task to add");
                } else {
                    String subTaskKey = mRef.push().getKey();

                    mRef.child(parentTaskKey).child("subTasksList").child(subTaskKey).child("task").setValue(strEditAddSubTask);
                    mRef.child(parentTaskKey).child("subTasksList").child(subTaskKey).child("operation").setValue("pending");
                    editTodayAddSubTask.setText("");

                }
                btnTodaySubTaskAdd.setVisibility(View.VISIBLE);
                todaySubTaskAddProgressBar.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        showTodaySubtaskRecycler.setLayoutManager(linearLayoutManager);
        showTodaySubtaskRecycler.setAdapter(cardSubListAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRef.removeEventListener(valueEventListener3);
    }
}
