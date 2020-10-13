package com.example.technologyden.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRequestViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView requestId;
    public TextView requestName;
    public TextView requestNumber;
    public TextView requestAddress;
    public TextView requestStatus;
    public TextView requestTotal;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private ItemClickListener itemClickListener;

    public UserRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        requestId = (TextView)itemView.findViewById(R.id.requestId);
        requestName = (TextView)itemView.findViewById(R.id.requestName);
        requestNumber = (TextView)itemView.findViewById(R.id.requestNumber);
        requestAddress = (TextView)itemView.findViewById(R.id.requestAddress);
        requestStatus = (TextView)itemView.findViewById(R.id.requestStatus);
        requestTotal = (TextView)itemView.findViewById(R.id.requestTotal);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Requests");

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
