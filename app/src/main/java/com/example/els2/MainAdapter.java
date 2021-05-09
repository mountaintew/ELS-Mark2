package com.example.els2;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ContactModel> arrayList;
    private OnContactListener onContactListener;


    public MainAdapter(Activity activity, ArrayList<ContactModel> arrayList, OnContactListener onContactListener){
        this.activity = activity;
        this.arrayList = arrayList;
        this.onContactListener = onContactListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Intialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        //Return View
        return new ViewHolder(view, onContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        //Initialize Contact model
        ContactModel contactModel = arrayList.get(position);


        //Set name
        holder.tvName.setText(contactModel.getName());;
        //Set number
        holder.tvNumber.setText(contactModel.getNumber());;

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName, tvNumber;
        OnContactListener onContactListener;


        public ViewHolder(@NonNull View itemView, OnContactListener onContactListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.contact_name);
            tvNumber = itemView.findViewById(R.id.contact_number);
            this.onContactListener = onContactListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onContactListener.onContactClick(getAdapterPosition());
        }
    }

    public interface OnContactListener {
        void onContactClick(int pos);
    }
}
