package com.example.todolist.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Model.CardSubListModel;
import com.example.todolist.R;
import com.example.todolist.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardSubListAdapter extends RecyclerView.Adapter<CardSubListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CardSubListModel> mList;
    SessionManager sessionManager;
    ContextThemeWrapper contextThemeWrapper;
    ValueEventListener valueEventListener;

    DatabaseReference mRef;
    FirebaseUser user;
    String userId;

    public CardSubListAdapter(Context mContext, ArrayList<CardSubListModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public CardSubListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

//        View view = layoutInflater.inflate(R.layout.card_item_list, parent, false);

        sessionManager = new SessionManager(mContext);
        if (sessionManager.loadNightModeState() == true) {
            contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.darktheme);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.AppTheme);
        }

        LayoutInflater localInflater = layoutInflater.cloneInContext(contextThemeWrapper);

        // Inflate the layout for this fragment
        View view = localInflater.inflate(R.layout.card_item_list, parent, false);

        CardSubListAdapter.ViewHolder viewHolder = new CardSubListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardSubListAdapter.ViewHolder holder, final int position) {
        final TextView cardInsideTaskText = holder.cardInsideTaskText;
        final String operation = mList.get(position).getOperation();

        if (operation.equals("pending")) {
            cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            cardInsideTaskText.setText(mList.get(position).getTask());
        } else if (operation.equals("done")) {
            cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cardInsideTaskText.setText(mList.get(position).getTask());
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        mRef = FirebaseDatabase.getInstance().getReference("todo").child("users").child(userId).child("tasks").child("taskList").child(mList.get(position).getTaskKey()).child("subTasksList");

        cardInsideTaskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (operation.equals("pending")) {
                                if (dataSnapshot1.getKey().equals(mList.get(position).getSubTasksListkey())) {
                                    mRef.child(dataSnapshot1.getKey()).child("operation").setValue("done");
                                    cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    break;
                                }
                            } else {
                                if (dataSnapshot1.getKey().equals(mList.get(position).getSubTasksListkey())) {
                                    mRef.child(dataSnapshot1.getKey()).child("operation").setValue("pending");
                                    cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mRef.addValueEventListener(valueEventListener);
            }
        });
//        cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        try {
            mRef.removeEventListener(valueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardInsideTaskText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardInsideTaskText = itemView.findViewById(R.id.cardInsideTaskText);
        }
    }
}
