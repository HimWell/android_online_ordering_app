package com.example.technologyden.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.technologyden.Home;
import com.example.technologyden.Models.User;
import com.example.technologyden.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyProfile extends AppCompatActivity {

    EditText myPhoneNumber;
    EditText myEmail;
    EditText myName;
    EditText myPassword;
    Button btnProfileUpdate;

    ArrayList<User> userList;
    User user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        myPhoneNumber = (EditText) findViewById(R.id.myPhoneNumber);
        myEmail = (EditText)findViewById(R.id.myEmail);
        myName = (EditText)findViewById(R.id.myName);
        myPassword = (EditText)findViewById(R.id.myPassword);
        btnProfileUpdate = (Button)findViewById(R.id.btnProfileUpdate);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        userList = new ArrayList<>();

        phoneNumber = UserLocalStored.currentUser.getPhoneNumber();
            loadProfileData(phoneNumber);
        user = new User();

        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = myPhoneNumber.getText().toString();
                String email = myEmail.getText().toString();
                String name = myName.getText().toString();
                String password = myPassword.getText().toString();

                UpdateProfileDetails(phoneNumber, email, name, password);

            }
        });

    }

    private void loadProfileData(final String phoneNumber) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                String email = (String) dataSnapshot.child(phoneNumber).child("email").getValue();
                String password = (String) dataSnapshot.child(phoneNumber).child("password").getValue();
                String name = (String) dataSnapshot.child(phoneNumber).child("name").getValue();

                user.setPassword(password);
                user.setEmail(email);
                user.setName(name);
                user.setPhoneNumber(phoneNumber);
                myPhoneNumber.setText(phoneNumber);
                myEmail.setText(email);
                myName.setText(name);
                myPassword.setText(password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyProfile.this, "Oops... Error Loading Profile Details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateProfileDetails(final String phoneNumber, final String email, final String name, final String password) {
        databaseReference.getRef().child(phoneNumber).child("email").setValue(email);
        databaseReference.getRef().child(phoneNumber).child("name").setValue(name);
        databaseReference.getRef().child(phoneNumber).child("password").setValue(password);

        Intent intent = new Intent(MyProfile.this, Home.class);
        Toast.makeText(MyProfile.this, "Profile Details Updated", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
