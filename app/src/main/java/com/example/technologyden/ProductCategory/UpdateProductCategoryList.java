package com.example.technologyden.ProductCategory;

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
import android.widget.Toast;

import com.example.technologyden.Home;
import com.example.technologyden.Models.ProductCategory;
import com.example.technologyden.R;
import com.example.technologyden.Store.UpdateStore;
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

public class UpdateProductCategoryList extends AppCompatActivity {

    ImageView myProdCatImage;
    EditText myProdCatId;
    EditText myProdCatName;
    EditText myStoreProdCatId;
    Button btnProdCatSelectImageUpdate;
    Button btnProdCatUploadImageUpdate;
    Button btnProdCatUpdate;

    ArrayList<ProductCategory> productCategoryList;
    ProductCategory productCategory;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String productCategoryId = "";
    String storeId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_category_list);

        myProdCatImage = (ImageView)findViewById(R.id.myProdCatImage);
        myProdCatId = (EditText)findViewById(R.id.myProdCatId);
        myProdCatName = (EditText)findViewById(R.id.myProdCatName);
        myStoreProdCatId = (EditText)findViewById(R.id.myStoreProdCatId);
        btnProdCatSelectImageUpdate = (Button)findViewById(R.id.btnProdCatSelectImageUpdate);
        btnProdCatUploadImageUpdate = (Button)findViewById(R.id.btnProdCatUploadImageUpdate);
        btnProdCatUpdate = (Button)findViewById(R.id.btnProdCatUpdate);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ProductCategories");

        if(getIntent() != null)
            productCategoryId = getIntent().getStringExtra("productCategoryId");
        if(!productCategoryId.isEmpty() && productCategoryId != null)
        {
            loadProductCategory(productCategoryId);
        }

        productCategoryList = new ArrayList<>();
        productCategory = new ProductCategory();

        btnProdCatUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productCategory.setProductCategoryName(myProdCatName.getText().toString());
                productCategory.setStoreProdCatID(myStoreProdCatId.getText().toString());
                databaseReference.child(productCategoryId).setValue(productCategory);

                    // Intent intent = new Intent(UpdateProductCategoryList.this, UpdateProductCategoryList.class);
                    Toast.makeText(UpdateProductCategoryList.this, "Product Category Updated", Toast.LENGTH_SHORT).show();
                    // storeId = getIntent().getStringExtra("storeId");
                    // startActivity(intent);
            }
        });

        btnProdCatSelectImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnProdCatUploadImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage(productCategory);
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
            btnProdCatSelectImageUpdate.setText("Image Selected!");
        }
    }

    private void UploadImage(final ProductCategory productCategory) {
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
                    Toast.makeText(UpdateProductCategoryList.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            loadProductCategory(productCategoryId);
                            productCategory.setProductCategoryImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateProductCategoryList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadProductCategory(final String productCategoryId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProductCategory productCategory = dataSnapshot.child(productCategoryId).getValue(ProductCategory.class);
                String prodCatImage = (String) dataSnapshot.child(productCategoryId).child("productCategoryImage").getValue();
                String prodCatName = (String) dataSnapshot.child(productCategoryId).child("productCategoryName").getValue();
                String storeProdId = (String) dataSnapshot.child(productCategoryId).child("storeProdCatID").getValue();

                productCategory.setProductCategoryImage(prodCatImage);
                productCategory.setProductCategoryId(productCategoryId);
                productCategory.setProductCategoryName(prodCatName);
                productCategory.setStoreProdCatID(storeProdId);
                Picasso.with(getBaseContext()).load(productCategory.getProductCategoryImage()).into(myProdCatImage);
                myProdCatId.setText(productCategoryId);
                myProdCatName.setText(prodCatName);
                myStoreProdCatId.setText(storeProdId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProductCategoryList.this, "Oops... Error Loading Product Category", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
