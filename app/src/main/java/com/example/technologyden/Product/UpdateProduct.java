package com.example.technologyden.Product;

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

import com.example.technologyden.Models.Product;
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

public class UpdateProduct extends AppCompatActivity {

    ImageView myProdImage;
    EditText myProdId;
    EditText myProdName;
    EditText myProdDesc;
    EditText myProdPrice;
    EditText myProdDiscPrice;
    EditText myProdStatus;
    EditText myProdCatId;
    Button btnProdSelectImageUpdate;
    Button btnProdUploadImageUpdate;
    Button btnProdUpdate;

    ArrayList<Product> productList;
    Product product;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String productId = "";
    String prodCatId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        myProdImage = (ImageView)findViewById(R.id.myProdImage);
        myProdId = (EditText)findViewById(R.id.myProdId);
        myProdName = (EditText) findViewById(R.id.myProdName);
        myProdDesc = (EditText) findViewById(R.id.myProdDesc);
        myProdPrice = (EditText) findViewById(R.id.myProdPrice);
        myProdDiscPrice = (EditText) findViewById(R.id.myProdDiscPrice);
        myProdStatus = (EditText) findViewById(R.id.myProdStatus);
        myProdCatId = (EditText)findViewById(R.id.myProdCatId);
        btnProdSelectImageUpdate = (Button)findViewById(R.id.btnProdSelectImageUpdate);
        btnProdUploadImageUpdate = (Button)findViewById(R.id.btnProdUploadImageUpdate);
        btnProdUpdate = (Button)findViewById(R.id.btnProdUpdate);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        if(getIntent() != null)
            productId = getIntent().getStringExtra("productId");
        if(!productId.isEmpty() && productId != null)
        {
            loadProduct(productId);
        }
        productList = new ArrayList<>();
        product = new Product();

        btnProdUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setProductName(myProdName.getText().toString());
                product.setProductDescription(myProdDesc.getText().toString());
                product.setProductPrice(myProdPrice.getText().toString());
                product.setProductDiscountPrice(myProdDiscPrice.getText().toString());
                product.setProductStatus(myProdStatus.getText().toString());
                product.setProdCatId(myProdCatId.getText().toString());
                databaseReference.child(productId).setValue(product);

                // Intent intent = new Intent(UpdateProductCategoryList.this, UpdateProductCategoryList.class);
                Toast.makeText(UpdateProduct.this, "Product Updated", Toast.LENGTH_SHORT).show();
                // storeId = getIntent().getStringExtra("storeId");
                // startActivity(intent);
            }
        });

        btnProdSelectImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnProdUploadImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage(product);
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
            btnProdSelectImageUpdate.setText("Image Selected!");
        }
    }

    private void UploadImage(final Product product) {
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
                    Toast.makeText(UpdateProduct.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            loadProduct(productId);
                            product.setProductImageURL(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateProduct.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadProduct(final String productId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.child(productId).getValue(Product.class);
                String prodImage = (String) dataSnapshot.child(productId).child("productImageURL").getValue();
                String prodName = (String) dataSnapshot.child(productId).child("productName").getValue();
                String prodDesc = (String) dataSnapshot.child(productId).child("productDescription").getValue();
                String prodPrice = (String) dataSnapshot.child(productId).child("productPrice").getValue();
                String prodDiscPrice = (String) dataSnapshot.child(productId).child("productDiscountPrice").getValue();
                String prodStatus = (String) dataSnapshot.child(productId).child("productStatus").getValue();
                String prodCatId = (String) dataSnapshot.child(productId).child("prodCatId").getValue();

                product.setProductImageURL(prodImage);
                product.setProductId(productId);
                product.setProductName(prodName);
                product.setProductDescription(prodDesc);
                product.setProductPrice(prodPrice);
                product.setProductDiscountPrice(prodDiscPrice);
                product.setProductStatus(prodStatus);
                product.setProdCatId(prodCatId);
                Picasso.with(getBaseContext()).load(product.getProductImageURL()).into(myProdImage);
                myProdId.setText(productId);
                myProdName.setText(prodName);
                myProdDesc.setText(prodDesc);
                myProdPrice.setText(prodPrice);
                myProdDiscPrice.setText(prodDiscPrice);
                myProdStatus.setText(prodStatus);
                myProdCatId.setText(prodCatId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProduct.this, "Oops... Error Loading Products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
