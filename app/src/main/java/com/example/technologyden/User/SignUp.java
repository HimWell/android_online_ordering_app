package com.example.technologyden.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.technologyden.Login;
import com.example.technologyden.Models.User;
import com.example.technologyden.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    EditText edtPhoneNumber;
    EditText edtEmail;
    EditText edtAdmin;
    EditText edtName;
    EditText edtPassword;
    EditText edtSecureCode;
    Button btnRegSignUp;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // UserLocalStored userLocalStored = new UserLocalStored();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtAdmin = (EditText) findViewById(R.id.edtAdmin);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtSecureCode = (EditText)findViewById(R.id.edtSecureCode);
        btnRegSignUp = (Button)findViewById(R.id.btnRegSignUp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        btnRegSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(UserLocalStored.hasInternetConnection(getBaseContext())) {

                    final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                    progressDialog.setMessage("Signing up, Give us a moment before entering your technology experience...");
                    progressDialog.show();

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhoneNumber.getText().toString()).exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this,"User is already Registered", Toast.LENGTH_SHORT).show();
                            }
                            else
                                progressDialog.dismiss();
                            User user = new User(edtEmail.getText().toString(),edtAdmin.getText().toString(),edtName.getText().toString(),edtPassword.getText().toString(),
                                    edtSecureCode.getText().toString());
                            databaseReference.child(edtPhoneNumber.getText().toString()).setValue(user);
                            Intent intent = new Intent(SignUp.this, Login.class);
                            // userLocalStored.currentUser = user;
                            Toast.makeText(SignUp.this,"Sign up Successfully, Login to begin your Online Experience", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SignUp.this,"Oops, Required details may be left out", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(SignUp.this, "No Internet Connection, Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
