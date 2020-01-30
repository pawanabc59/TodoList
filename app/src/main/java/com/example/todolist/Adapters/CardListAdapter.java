package com.example.todolist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Model.CardListModel;
import com.example.todolist.Model.CardSubListModel;
import com.example.todolist.R;
import com.example.todolist.SingleCardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CardListModel> mList;
    ValueEventListener valueEventListener;

    public CardListAdapter(Context mContext, ArrayList<CardListModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.card_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardListAdapter.ViewHolder holder, final int position) {
        RecyclerView cardInsideRecycler = holder.cardInsideRecycler;
        holder.date.setText(mList.get(position).getDate());

        final CardSubListAdapter cardSubListAdapter;
        final ArrayList<CardSubListModel> cardSubListModels;

        cardSubListModels = new ArrayList<>();
        cardSubListAdapter = new CardSubListAdapter(mContext, cardSubListModels);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("todo").child("users").child(userId).child("tasks").child("taskList").child(mList.get(position).getTaskKey()).child("subTasksList");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardSubListModels.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    try {
                        cardSubListModels.add(new CardSubListModel(dataSnapshot1.child("task").getValue().toString(), dataSnapshot1.child("operation").getValue().toString(), mList.get(position).getTaskKey(), dataSnapshot1.getKey()));
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

        holder.singleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SingleCardActivity.class);
                intent.putExtra("taskDate", mList.get(position).getDate());
                intent.putExtra("subTaskKey", mList.get(position).getTaskKey());
                mContext.startActivity(intent);
            }
        });
//        cardSubListModels.add(new CardSubListModel("new Task"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        cardInsideRecycler.setLayoutManager(linearLayoutManager);
        cardInsideRecycler.setAdapter(cardSubListAdapter);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        RecyclerView cardInsideRecycler;
        CardView singleCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.cardDate);
            cardInsideRecycler = itemView.findViewById(R.id.cardInsideRecycler);
            singleCard = itemView.findViewById(R.id.singleCard);
        }
    }
}
