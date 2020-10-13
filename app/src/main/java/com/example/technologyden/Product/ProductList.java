package com.example.technologyden.Product;

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

import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.Models.Product;
import com.example.technologyden.ProductCategory.UpdateProductCategoryList;
import com.example.technologyden.R;
import com.example.technologyden.User.UserLocalStored;
import com.example.technologyden.ViewHolders.ProductViewHolder;
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

public class ProductList extends AppCompatActivity {

    RecyclerView recycler_Prod;
    RecyclerView.LayoutManager layoutManager;

    EditText edtProdName;
    EditText edtProdDescription;
    EditText edtProdPrice;
    EditText edtProdDiscPrice;
    EditText edtProdStatus;
    Button btnProdSelect;
    Button btnProdUpload;

    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    ArrayList<Product> productList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Product product;

    String productCategoryId = "";

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recycler_Prod = (RecyclerView)findViewById(R.id.recycler_Prod);
        recycler_Prod.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_Prod.setLayoutManager(layoutManager);

        productList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        if(getIntent() != null)
            productCategoryId = getIntent().getStringExtra("productCategoryId");
        if(!productCategoryId.isEmpty() && productCategoryId != null)
        {
            loadProducts(productCategoryId);
        }

        product = new Product();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        FloatingActionButton fabProd = findViewById(R.id.fabProd);
        if (UserLocalStored.currentUser.getIsAdmin().equals("true")) {
            fabProd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }
        else
            fabProd.setVisibility(View.INVISIBLE);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Add New Product");
        alertDialog.setMessage("Please fill in all details");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_product = inflater.inflate(R.layout.add_new_product, null);

        edtProdName = add_new_product.findViewById(R.id.edtProdName);
        edtProdDescription = add_new_product.findViewById(R.id.edtProdDescription);
        edtProdPrice = add_new_product.findViewById(R.id.edtProdPrice);
        edtProdDiscPrice = add_new_product.findViewById(R.id.edtProdDiscPrice);
        edtProdStatus = add_new_product.findViewById(R.id.edtProdStatus);
        btnProdSelect = add_new_product.findViewById(R.id.btnProdSelect);
        btnProdUpload = add_new_product.findViewById(R.id.btnProdUpload);

        btnProdSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnProdUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

        alertDialog.setView(add_new_product);
        alertDialog.setIcon(R.drawable.ic_local_grocery_store_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (product != null) {
                    databaseReference.push().setValue(product);
                    Toast.makeText(ProductList.this, "New Product Added", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProductList.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            product = new Product();
                            product.setProductName(edtProdName.getText().toString());
                            product.setProductDescription(edtProdDescription.getText().toString());
                            product.setProductPrice(edtProdPrice.getText().toString());
                            product.setProductDiscountPrice(edtProdDiscPrice.getText().toString());
                            product.setProductStatus(edtProdStatus.getText().toString());
                            product.setProductImageURL(uri.toString());
                            product.setProdCatId(productCategoryId);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            btnProdSelect.setText("Image Selected!");
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void loadProducts(String productCategoryId) {

        adapter = new FirebaseRecyclerAdapter<Product,ProductViewHolder>(Product.class,
                R.layout.product_item,
                ProductViewHolder.class,
                databaseReference.orderByChild("prodCatId").equalTo(productCategoryId)) {
            @Override
            protected void populateViewHolder(ProductViewHolder productViewHolder, Product product, final int position) {

                Picasso.with(getBaseContext()).load(product.getProductImageURL()).into(productViewHolder.productImage);
                productViewHolder.productName.setText(product.getProductName());

                if(UserLocalStored.currentUser.getIsAdmin().equals("true")) {
                    final Product item = product;
                    productViewHolder.btnProdUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProductList.this, UpdateProduct.class);
                            intent.putExtra("productId",adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }
                else
                    productViewHolder.btnProdUpdate.setVisibility(View.INVISIBLE);

                final Product local = product;
                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        // Toast.makeText(ProductList.this, "" + local.getProductName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductList.this, ProductInformation.class);
                        intent.putExtra("productId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        recycler_Prod.setAdapter(adapter);
    }
}
