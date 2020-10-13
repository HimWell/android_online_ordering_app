package com.example.technologyden;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.technologyden.Cart.Cart;
import com.example.technologyden.Interfaces.ItemClickListener;
import com.example.technologyden.Models.Store;
import com.example.technologyden.ProductCategory.ProductCategoryList;
import com.example.technologyden.Requests.Requests;
import com.example.technologyden.Requests.UserRequests;
import com.example.technologyden.Service.OrderStatusNotification;
import com.example.technologyden.Store.UpdateStore;
import com.example.technologyden.User.MyProfile;
import com.example.technologyden.User.UserLocalStored;
import com.example.technologyden.ViewHolders.StoreViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;
    UserLocalStored userLocalStored;

    RecyclerView recyclerStore;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Store,StoreViewHolder> adapter;

    ArrayList<Store> storeList;

    EditText edtStoreName;
    EditText edtStoreLocation;
    EditText edtStoreNumber;
    EditText edtStoreDelivery;
    Button btnSelect;
    Button btnUpload;

    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Store store;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;
    DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        recyclerStore = (RecyclerView) findViewById(R.id.recyclerStore);
        recyclerStore.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerStore.setLayoutManager(layoutManager);

        storeList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Stores");

        loadStores();

        Intent intent = new Intent(Home.this, OrderStatusNotification.class);
        startService(intent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (userLocalStored.currentUser.getIsAdmin().equals("true")) {
            toolbar.setTitle("Admin Menu");
        } else
            toolbar.setTitle("Technology Den Menu");
            setSupportActionBar(toolbar);

        store = new Store();
        FloatingActionButton fab = findViewById(R.id.fab);
        if (userLocalStored.currentUser.getIsAdmin().equals("true")) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
        else
            fab.setVisibility(View.INVISIBLE);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName =(TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(userLocalStored.currentUser.getName());

    }

    private void loadStores() {

           adapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(Store.class,
                   R.layout.shop_item,
                   StoreViewHolder.class,
                   databaseReference) {
            @Override
            protected void populateViewHolder(StoreViewHolder storeViewHolder, Store store, final int position) {
                Picasso.with(getBaseContext()).load(store.getStoreImage()).into(storeViewHolder.store_image);
                storeViewHolder.store_name.setText(store.getStoreName());
                storeViewHolder.store_location.setText(store.getStoreLocation());
                storeViewHolder.store_number.setText(store.getStoreNumber());
                storeViewHolder.store_delivery.setText(store.getStoreDelivery());

                if(userLocalStored.currentUser.getIsAdmin().equals("true")) {
                    final Store item = store;
                    storeViewHolder.btnUpdateStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Home.this, UpdateStore.class);
                            intent.putExtra("storeId",adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }
                else
                    storeViewHolder.btnUpdateStore.setVisibility(View.INVISIBLE);

                final Store clickItem = store;
                storeViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        // Toast.makeText(Home.this, "" + clickItem.getStoreName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Home.this, ProductCategoryList.class);
                        intent.putExtra("storeId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerStore.setAdapter(adapter);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add New Store");
        alertDialog.setMessage("Please fill in all details");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_store = inflater.inflate(R.layout.add_new_store, null);

        edtStoreName = add_new_store.findViewById(R.id.edtStoreName);
        edtStoreLocation = add_new_store.findViewById(R.id.edtStoreLocation);
        edtStoreNumber = add_new_store.findViewById(R.id.edtStoreNumber);
        edtStoreDelivery = add_new_store.findViewById(R.id.edtStoreDelivery);

        btnSelect = add_new_store.findViewById(R.id.btnSelect);
        btnUpload = add_new_store.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

        alertDialog.setView(add_new_store);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (store != null) {
                    databaseReference.push().setValue(store);
                    Toast.makeText(Home.this, "New Store Added", Toast.LENGTH_SHORT).show();
                    // loadStores();
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
                    Toast.makeText(Home.this, "Uploaded Image Successfully", Toast.LENGTH_SHORT).show();
                    imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            store = new Store();
                            store.setStoreName(edtStoreName.getText().toString());
                            store.setStoreLocation(edtStoreLocation.getText().toString());
                            store.setStoreNumber(edtStoreNumber.getText().toString());
                            store.setStoreDelivery(edtStoreDelivery.getText().toString());
                            store.setStoreImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            btnSelect.setText("Image Selected!");
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
                Intent intent = new Intent(Home.this, MyProfile.class);
                startActivity(intent);

        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(Home.this, Cart.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            if(userLocalStored.currentUser.getIsAdmin().equals("true")) {
                Intent intent = new Intent(Home.this, Requests.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(Home.this, UserRequests.class);
                startActivity(intent);
            }


        } else if (id == R.id.nav_log_out) {
            Paper.book().destroy();

            Intent intent = new Intent(Home.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
