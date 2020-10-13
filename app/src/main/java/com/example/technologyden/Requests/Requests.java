package com.example.technologyden.Requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.Models.Request;
import com.example.technologyden.R;
import com.example.technologyden.User.UserLocalStored;
import com.example.technologyden.ViewHolders.RequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Requests extends AppCompatActivity {

    RecyclerView recycler_Requests;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, RequestViewHolder> adapter;

    ArrayList<Request> requestList;

    DatabaseReference databaseReference;

    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        recycler_Requests = (RecyclerView)findViewById(R.id.recycler_Requests);
        recycler_Requests.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_Requests.setLayoutManager(layoutManager);

        requestList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Requests");

        request = new Request();

        if(UserLocalStored.currentUser.getIsAdmin().equals("true")) {
            loadAllRequests();
        }
        else
            loadRequests(UserLocalStored.currentUser.getPhoneNumber());

    }

    private void loadAllRequests() {
        adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(Request.class,
                R.layout.request_item,
                RequestViewHolder.class,
                databaseReference) {
            @Override
            protected void populateViewHolder(RequestViewHolder requestViewHolder, final Request request, int position) {

                requestViewHolder.requestNo.setText(adapter.getRef(position).getKey());
                requestViewHolder.requestName.setText(request.getName());
                requestViewHolder.requestNumber.setText(request.getPhone());
                requestViewHolder.requestAddress.setText(request.getAddress());
                requestViewHolder.requestStatus.setText(request.getStatus());
                requestViewHolder.requestTotal.setText(request.getTotal());

                requestViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent intent = new Intent(Requests.this, TrackingOrder.class);
                        UserLocalStored.currentRequest = request;
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_Requests.setAdapter(adapter);
    }

    private void loadRequests(String phoneNumber) {

        adapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(Request.class,
                R.layout.request_item,
                RequestViewHolder.class,
                databaseReference.orderByChild("phone").equalTo(phoneNumber)) {
            @Override
            protected void populateViewHolder(RequestViewHolder requestViewHolder, final Request request, int position) {

                requestViewHolder.requestNo.setText(adapter.getRef(position).getKey());
                requestViewHolder.requestName.setText(request.getName());
                requestViewHolder.requestNumber.setText(request.getPhone());
                requestViewHolder.requestAddress.setText(request.getAddress());
                requestViewHolder.requestStatus.setText(request.getStatus());
                requestViewHolder.requestTotal.setText(request.getTotal());

                requestViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent intent = new Intent(Requests.this, TrackingOrder.class);
                        UserLocalStored.currentRequest = request;
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_Requests.setAdapter(adapter);
    }
}
