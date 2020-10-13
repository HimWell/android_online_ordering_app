package com.example.technologyden.ProductCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.technologyden.Home;
import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.Models.ProductCategory;
import com.example.technologyden.Models.Store;
import com.example.technologyden.Product.ProductList;
import com.example.technologyden.R;
import com.example.technologyden.User.UserLocalStored;
import com.example.technologyden.ViewHolders.ProductCategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class ProductCategoryList extends AppCompatActivity {

    RecyclerView recycler_ProdCat;
    RecyclerView.LayoutManager layoutManager;

    EditText edtProdCatName;
    Button btnProdCatSelect;
    Button btnProdCatUpload;

    FirebaseRecyclerAdapter<ProductCategory, ProductCategoryViewHolder> adapter;

    ArrayList<ProductCategory> productCategoryList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProductCategory productCategory;

    String storeId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category_list);

        recycler_ProdCat = (RecyclerView)findViewById(R.id.recycler_ProdCat);
        recycler_ProdCat.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_ProdCat.setLayoutManager(layoutManager);

        productCategoryList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProductCategories");

        if(getIntent() != null)
            storeId = getIntent().getStringExtra("storeId");
        if(!storeId.isEmpty() && storeId != null)
        {
             loadProductCategories(storeId);
        }

        productCategory = new ProductCategory();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ProductCategories");

        FloatingActionButton fabProdCat = findViewById(R.id.fabProdCat);
        if (UserLocalStored.currentUser.getIsAdmin().equals("true")) {
            fabProdCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }
      else
        fabProdCat.setVisibility(View.INVISIBLE);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductCategoryList.this);
        alertDialog.setTitle("Add New Product Category");
        alertDialog.setMessage("Please fill in all details");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_product_category = inflater.inflate(R.layout.add_new_product_category, null);

        edtProdCatName = add_new_product_category.findViewById(R.id.edtProdCatName);
        btnProdCatSelect = add_new_product_category.findViewById(R.id.btnProdCatSelect);
        btnProdCatUpload = add_new_product_category.findViewById(R.id.btnProdCatUpload);

        btnProdCatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnProdCatUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

        alertDialog.setView(add_new_product_category);
        alertDialog.setIcon(R.drawable.ic_local_grocery_store_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (productCategory != null) {
                    databaseReference.push().setValue(productCategory);
                    Toast.makeText(ProductCategoryList.this, "New Product Category Added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void UploadImage() {
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
                    Toast.makeText(ProductCategoryList.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            productCategory = new ProductCategory();
                            productCategory.setProductCategoryName(edtProdCatName.getText().toString());
                            productCategory.setProductCategoryImage(uri.toString());
                            productCategory.setStoreProdCatID(storeId);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductCategoryList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnProdCatSelect.setText("Image Selected!");
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void loadProductCategories(String storeId) {

        adapter = new FirebaseRecyclerAdapter<ProductCategory, ProductCategoryViewHolder>(ProductCategory.class,
                R.layout.product_category_item,
                ProductCategoryViewHolder.class,
                databaseReference.orderByChild("storeProdCatID").equalTo(storeId)) {
            @Override
            protected void populateViewHolder(ProductCategoryViewHolder productCategoryViewHolder, ProductCategory productCategory, final int position) {

                Picasso.with(getBaseContext()).load(productCategory.getProductCategoryImage()).into(productCategoryViewHolder.productCategoryImage);
                productCategoryViewHolder.productCategoryName.setText(productCategory.getProductCategoryName());

                if(UserLocalStored.currentUser.getIsAdmin().equals("true")) {
                    final ProductCategory item = productCategory;
                    productCategoryViewHolder.btnUpdateProdCat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                         Intent intent = new Intent(ProductCategoryList.this, UpdateProductCategoryList.class);
                         intent.putExtra("productCategoryId",adapter.getRef(position).getKey());
                         startActivity(intent);
                        }
                    });
                }
                else
                    productCategoryViewHolder.btnUpdateProdCat.setVisibility(View.INVISIBLE);

                final ProductCategory local = productCategory;
                productCategoryViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        // Toast.makeText(ProductCategoryList.this, "" + local.getProductCategoryName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductCategoryList.this, ProductList.class);
                        intent.putExtra("productCategoryId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_ProdCat.setAdapter(adapter);
    }
}
