package com.example.todolist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UploadTaskActivity extends AppCompatActivity {

    TextInputLayout taskDescription, taskDate;
    TextInputEditText editTaskDescription, editTaskDate;
    ImageButton btnEditDate;
    MaterialButton btnUploadTask;
    ProgressBar uploadTaskProgressbar;
    int year, month, dayOfMonth;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    SessionManager sessionManager;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference mRef;
    String userId;
    int numberOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.loadNightModeState() == true) {

            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_upload_task);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        mRef = firebaseDatabase.getReference("todo").child("users").child(userId);

        taskDescription = findViewById(R.id.taskDescription);
        taskDate = findViewById(R.id.taskDate);
        editTaskDescription = findViewById(R.id.editTaskDescription);
        editTaskDate = findViewById(R.id.editTaskDate);
        btnEditDate = findViewById(R.id.btnEditDate);
        btnUploadTask = findViewById(R.id.btnUploadTask);
        uploadTaskProgressbar = findViewById(R.id.uploadTaskProgressbar);

        mRef.child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    mRef.child("tasks").child("numberOfTasks").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        editTaskDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                datePickerDialog = new DatePickerDialog(UploadTaskActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                                editTaskDate.setText(day+"/"+(month+1)+"/"+year);
//                            }
//                        }, year, month, dayOfMonth);
//                datePickerDialog.show();
//            }
//        });

        btnEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(UploadTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                String monthString = String.valueOf((month + 1));
                                if (monthString.length() == 1) {
                                    monthString = "0" + monthString;
                                }
                                editTaskDate.setText(day + "/" + monthString + "/" + year);
//                                String d = day+"/"+monthString+"/"+year;
//                                Toast.makeText(getApplicationContext(), d, Toast.LENGTH_LONG).show();
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        mRef.child("tasks").child("numberOfTasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfTasks = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUploadTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTaskDesc = editTaskDescription.getText().toString();
                String editDate = editTaskDate.getText().toString();
                if (editTaskDesc.equals("")) {
                    editTaskDescription.setError("Please enter task description");
                } else if (editDate.equals("")) {
                    editTaskDate.setError("Please set task Date");
                } else {
                    btnUploadTask.setVisibility(View.GONE);
                    uploadTaskProgressbar.setVisibility(View.VISIBLE);

                    String taskKey = mRef.push().getKey();

                    mRef.child("tasks").child("taskList").child(taskKey).child("date").setValue(editDate);
                    mRef.child("tasks").child("taskList").child(taskKey).child("subTasksList").child(taskKey).child("task").setValue(editTaskDesc);
                    mRef.child("tasks").child("taskList").child(taskKey).child("subTasksList").child(taskKey).child("operation").setValue("pending");
                    mRef.child("tasks").child("taskList").child(taskKey).child("taskNumber").setValue(-(numberOfTasks + 1));
                    mRef.child("tasks").child("numberOfTasks").setValue((numberOfTasks + 1));

                    Toast.makeText(getApplicationContext(), "Task uploaded successfully", Toast.LENGTH_SHORT).show();

                    editTaskDate.setText("");
                    editTaskDescription.setText("");
                    uploadTaskProgressbar.setVisibility(View.GONE);
                    btnUploadTask.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    public void pickDate() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getApplicationContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        editTaskDate.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
}
