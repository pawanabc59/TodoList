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

import com.example.todolist.Model.CardListModel;
import com.example.todolist.Model.CardSubListModel;
import com.example.todolist.R;
import com.example.todolist.SessionManager;

import java.util.ArrayList;

public class CardSubListAdapter extends RecyclerView.Adapter<CardSubListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CardSubListModel> mList;
    SessionManager sessionManager;
    ContextThemeWrapper contextThemeWrapper;

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
    public void onBindViewHolder(@NonNull CardSubListAdapter.ViewHolder holder, int position) {
        TextView cardInsideTaskText = holder.cardInsideTaskText;
        cardInsideTaskText.setText(mList.get(position).getTask());
        cardInsideTaskText.setPaintFlags(cardInsideTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
