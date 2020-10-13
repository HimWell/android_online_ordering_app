package com.example.technologyden.Store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.technologyden.Home;
import com.example.technologyden.Models.Store;
import com.example.technologyden.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class UpdateStore extends AppCompatActivity {

    ImageView myStoreImage;
    TextView myStoreId;
    EditText myStoreName;
    EditText myStoreLocation;
    EditText myStoreContactNumber;
    EditText myStoreDelivery;
    Button btnStoreSelectImageUpdate;
    Button btnStoreUploadImageUpdate;
    Button btnStoreUpdate;

    ArrayList<Store> storeList;
    Store store;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String storeId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_store);

        myStoreImage = (ImageView)findViewById(R.id.myStoreImage);
        myStoreId = (TextView)findViewById(R.id.myStoreId);
        myStoreName = (EditText) findViewById(R.id.myStoreName);
        myStoreLocation = (EditText)findViewById(R.id.myStoreLocation);
        myStoreContactNumber = (EditText)findViewById(R.id.myStoreContactNumber);
        myStoreDelivery = (EditText)findViewById(R.id.myStoreDelivery);
        btnStoreSelectImageUpdate = (Button)findViewById(R.id.btnStoreSelectImageUpdate);
        btnStoreUploadImageUpdate = (Button)findViewById(R.id.btnStoreUploadImageUpdate);
        btnStoreUpdate = (Button)findViewById(R.id.btnStoreUpdate);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Stores");

        if(getIntent() != null)
            storeId = getIntent().getStringExtra("storeId");
        if(!storeId.isEmpty() && storeId != null)
        {
            loadStoreDetails(storeId);
        }

        storeList = new ArrayList<>();

        store = new Store();

        btnStoreUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.setStoreName(myStoreName.getText().toString());
                store.setStoreLocation(myStoreLocation.getText().toString());
                store.setStoreNumber(myStoreContactNumber.getText().toString());
                store.setStoreDelivery(myStoreDelivery.getText().toString());
                databaseReference.child(storeId).setValue(store);

                // Intent intent = new Intent(UpdateStore.this, Home.class);
                Toast.makeText(UpdateStore.this, "Store Details Updated", Toast.LENGTH_SHORT).show();
                // startActivity(intent);
            }
        });

        btnStoreSelectImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnStoreUploadImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage(store);
            }
        });

    }

    private void loadStoreDetails(final String storeId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.child(storeId).getValue(Store.class);
                String storeImage = (String) dataSnapshot.child(storeId).child("storeImage").getValue();
                String storeName = (String) dataSnapshot.child(storeId).child("storeName").getValue();
                String storeLocation = (String) dataSnapshot.child(storeId).child("storeLocation").getValue();
                String storeContactNumber = (String) dataSnapshot.child(storeId).child("storeNumber").getValue();
                String storeDelivery = (String) dataSnapshot.child(storeId).child("storeDelivery").getValue();

                store.setStoreImage(storeImage);
                store.setStoreId(storeId);
                store.setStoreName(storeName);
                store.setStoreLocation(storeLocation);
                store.setStoreNumber(storeContactNumber);
                store.setStoreDelivery(storeDelivery);
                Picasso.with(getBaseContext()).load(store.getStoreImage()).into(myStoreImage);
                myStoreId.setText(storeId);
                myStoreName.setText(storeName);
                myStoreLocation.setText(storeLocation);
                myStoreContactNumber.setText(storeContactNumber);
                myStoreDelivery.setText(storeDelivery);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateStore.this, "Oops... Error Loading Store Details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnStoreSelectImageUpdate.setText("Image Selected!");
        }
    }

    private void UploadImage(final Store store) {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference imgFolder = storageReference.child("images/" + imgName);
            imgFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateStore.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            loadStoreDetails(storeId);
                            store.setStoreImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateStore.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading Image... " + progress + " %");
                }
            });
        }
    }
}
