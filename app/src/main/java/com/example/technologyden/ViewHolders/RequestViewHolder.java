package com.example.technologyden.ViewHolders;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.R;
import com.example.technologyden.Requests.Requests;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public EditText requestNo;
    public EditText requestName;
    public EditText requestNumber;
    public EditText requestAddress;
    public EditText requestStatus;
    public EditText requestTotal;
    public Button btnRequestEdit;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private ItemClickListener itemClickListener;

    public RequestViewHolder(View itemView) {
        super(itemView);

        requestNo = (EditText) itemView.findViewById(R.id.requestNo);
        requestName = (EditText) itemView.findViewById(R.id.requestName);
        requestNumber = (EditText) itemView.findViewById(R.id.requestNumber);
        requestAddress = (EditText) itemView.findViewById(R.id.requestAddress);
        requestStatus = (EditText) itemView.findViewById(R.id.requestStatus);
        requestTotal = (EditText) itemView.findViewById(R.id.requestTotal);
        btnRequestEdit = (Button)itemView.findViewById(R.id.btnRequestEdit);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Requests");

        btnRequestEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reqNo = requestNo.getText().toString();
                String reqName = requestName.getText().toString();
                String reqNumber = requestNumber.getText().toString();
                String reqAddress = requestAddress.getText().toString();
                String reqStatus = requestStatus.getText().toString();
                String reqTotal = requestTotal.getText().toString();

                UpdateRequestDetails(reqNo, reqName, reqNumber, reqAddress, reqStatus, reqTotal);
            }
        });

        itemView.setOnClickListener(this);
    }
        private void UpdateRequestDetails(final String reqNo, final String reqName, final String reqNumber, final String reqAddress,
        final String reqStatus, final String reqTotal) {

            databaseReference.getRef().child(reqNo).child("name").setValue(reqName);
            databaseReference.getRef().child(reqNo).child("phone").setValue(reqNumber);
            databaseReference.getRef().child(reqNo).child("address").setValue(reqAddress);
            databaseReference.getRef().child(reqNo).child("status").setValue(reqStatus);
            databaseReference.getRef().child(reqNo).child("total").setValue(reqTotal);

            Intent intent = new Intent(itemView.getContext(), Requests.class);
            Toast.makeText(itemView.getContext(), "Request Details Updated", Toast.LENGTH_SHORT).show();
            itemView.getContext().startActivity(intent);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
         itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
