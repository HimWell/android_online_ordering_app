package com.example.technologyden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.technologyden.Models.User;
import com.example.technologyden.User.SignUp;
import com.example.technologyden.User.UserLocalStored;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    EditText edtPhoneNumber;
    EditText edtPasswordLogin;
    Button btnLogin;
    Button btnSignUp;
    TextView txtSlogan;
    EditText edtSecurePhoneNumber;
    EditText edtSecureCode;
    CheckBox checkRemember;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    UserLocalStored userLocalStored = new UserLocalStored();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtPasswordLogin = (EditText) findViewById(R.id.edtPasswordLogin);
        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        checkRemember = (CheckBox)findViewById(R.id.checkRemember);

        Paper.init(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");

        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        TextView txtTC = findViewById(R.id.txtTC);

        txtTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermsAndConditions();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserLocalStored.hasInternetConnection(getBaseContext())) {

                    if(checkRemember.isChecked()) {
                        Paper.book().write(UserLocalStored.user_key, edtPhoneNumber.getText().toString());
                        Paper.book().write(UserLocalStored.user_password, edtPasswordLogin.getText().toString());
                    }

                    SignInUser(edtPhoneNumber.getText().toString(), edtPasswordLogin.getText().toString());
                }
                else {
                    Toast.makeText(Login.this, "No Internet Connection, Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String userKey = Paper.book().read(UserLocalStored.user_key);
        String passwordKey = Paper.book().read(UserLocalStored.user_password);
        
        if(userKey != null && passwordKey != null) {
            if(!userKey.isEmpty() && !passwordKey.isEmpty())
                login(userKey,passwordKey);
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    private void showTermsAndConditions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle("Terms and Conditions");

        LayoutInflater inflater = this.getLayoutInflater();
        View terms_and_conditions = inflater.inflate(R.layout.terms_and_conditions_layout, null);

        alertDialog.setView(terms_and_conditions);
        alertDialog.setIcon(R.drawable.ic_info_black_24dp);

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void login(String phone, String passwordKey) {
        SignInUser(phone,passwordKey);
    }

    private void showDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle("Forgot Password?");
        alertDialog.setMessage("Enter Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View reset_password = inflater.inflate(R.layout.forgot_password_layout, null);

       edtSecurePhoneNumber = reset_password.findViewById(R.id.edtSecurePhoneNumber);
       edtSecureCode = (EditText)reset_password.findViewById(R.id.edtSecureCode);
        alertDialog.setView(reset_password);
        alertDialog.setIcon(R.drawable.ic_security_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtSecurePhoneNumber.getText().toString()).getValue(User.class);
                        if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(Login.this,"Your Password is: " + user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Login.this, "Wrong Secure Code, Try Again or Contact Support", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, "Oops, Wrong Login Details.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void SignInUser(String phoneNumber, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Login you into the technology experience, Please Wait...");
        progressDialog.show();

        final String localPhone = phoneNumber;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()) {
                    progressDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    String email = (String) dataSnapshot.child(localPhone).child("email").getValue();
                    String password = (String) dataSnapshot.child(localPhone).child("password").getValue();
                    String isAdmin = (String) dataSnapshot.child(localPhone).child("isAdmin").getValue();
                    String name = (String) dataSnapshot.child(localPhone).child("name").getValue();
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setIsAdmin(isAdmin);
                    user.setName(name);
                    user.setPhoneNumber(localPhone);

                    if (isAdmin.equals("true")) {

                        if (password.equals(localPassword)) {
                            Intent intent = new Intent(Login.this, Home.class);
                            userLocalStored.currentUser = user;
                            Toast.makeText(Login.this, "Login as Admin User.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else
                            Toast.makeText(Login.this, "Wrong Password, Try Again.", Toast.LENGTH_SHORT).show();
                    } else if (isAdmin.equals("false")) {
                        if (password.equals(localPassword)) {
                            Intent intent = new Intent(Login.this, Home.class);
                            userLocalStored.currentUser = user;
                            Toast.makeText(Login.this, "Login Successfully, Start you Online Experience", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "User does not Exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this, "Wrong Password, Try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
