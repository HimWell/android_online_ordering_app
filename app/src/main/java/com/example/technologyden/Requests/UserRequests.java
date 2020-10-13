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
import com.example.technologyden.ViewHolders.UserRequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserRequests extends AppCompatActivity {

    RecyclerView recycler_UserRequests;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, UserRequestViewHolder> adapter;

    ArrayList<Request> requestList;

    DatabaseReference databaseReference;

    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_requests);

        recycler_UserRequests = (RecyclerView)findViewById(R.id.recycler_UserRequests);
        recycler_UserRequests.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_UserRequests.setLayoutManager(layoutManager);

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
        adapter = new FirebaseRecyclerAdapter<Request, UserRequestViewHolder>(Request.class,
                R.layout.user_request_item,
                UserRequestViewHolder.class,
                databaseReference) {
            @Override
            protected void populateViewHolder(UserRequestViewHolder userRequestViewHolder, final Request request, int position) {

                userRequestViewHolder.requestId.setText(adapter.getRef(position).getKey());
                userRequestViewHolder.requestName.setText(request.getName());
                userRequestViewHolder.requestNumber.setText(request.getPhone());
                userRequestViewHolder.requestAddress.setText(request.getAddress());
                userRequestViewHolder.requestStatus.setText(request.getStatus());
                userRequestViewHolder.requestTotal.setText(request.getTotal());

                userRequestViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent intent = new Intent(UserRequests.this, TrackingOrder.class);
                        UserLocalStored.currentRequest = request;
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_UserRequests.setAdapter(adapter);
    }

    private void loadRequests(String phoneNumber) {

        adapter = new FirebaseRecyclerAdapter<Request, UserRequestViewHolder>(Request.class,
                R.layout.user_request_item,
                UserRequestViewHolder.class,
                databaseReference.orderByChild("phone").equalTo(phoneNumber)) {
            @Override
            protected void populateViewHolder(UserRequestViewHolder userRequestViewHolder, final Request request, int position) {

                userRequestViewHolder.requestId.setText(adapter.getRef(position).getKey());
                userRequestViewHolder.requestName.setText(request.getName());
                userRequestViewHolder.requestNumber.setText(request.getPhone());
                userRequestViewHolder.requestAddress.setText(request.getAddress());
                userRequestViewHolder.requestStatus.setText(request.getStatus());
                userRequestViewHolder.requestTotal.setText(request.getTotal());

                userRequestViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent intent = new Intent(UserRequests.this, TrackingOrder.class);
                        UserLocalStored.currentRequest = request;
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_UserRequests.setAdapter(adapter);
    }
}
