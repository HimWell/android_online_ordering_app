package com.example.technologyden.Product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.technologyden.Models.Order;
import com.example.technologyden.Models.Product;
import com.example.technologyden.R;
import com.example.technologyden.User.UserLocalStored;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductInformation extends AppCompatActivity {

    TextView prod_Name;
    TextView prod_Price;
    TextView prod_Desc;
    TextView prod_Status;
    ImageView prod_Img;
    ElegantNumberButton numberButton;
    CollapsingToolbarLayout collapsing;
    FloatingActionButton btnCart;

    String productId = "";

    Product product;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");
        databaseReference2 = firebaseDatabase.getReference("Orders");
        databaseReference3 = firebaseDatabase.getReference("OrderHistory");

        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        collapsing = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getProductStatus().equals("In-Stock")) {
                    Order order = new Order(
                            productId,
                            product.getProductName(),
                            numberButton.getNumber(),
                            product.getProductPrice(),
                            product.getProductDiscountPrice(),
                            UserLocalStored.currentUser.getPhoneNumber(),
                            UserLocalStored.currentUser.getEmail(),
                            UserLocalStored.currentUser.getName());
                    databaseReference2.child(String.valueOf(System.currentTimeMillis())).setValue(order);
                    databaseReference3.child(String.valueOf(System.currentTimeMillis())).setValue(order);
                    Toast.makeText(ProductInformation.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }

                if (product.getProductStatus().equals("Out-of-Stock")) {
                    Toast.makeText(ProductInformation.this, "Product Out-of-Stock. Please contact store for further information.", Toast.LENGTH_SHORT).show();
                }

                if (product.getProductStatus().equals("Not Available")) {
                    Toast.makeText(ProductInformation.this, "Product Not Available. Please contact store for further information.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        prod_Name = (TextView)findViewById(R.id.prod_Name);
        prod_Price = (TextView)findViewById(R.id.prod_Price);
        prod_Desc = (TextView)findViewById(R.id.prod_Desc);
        prod_Status = (TextView)findViewById(R.id.prod_Status);
        prod_Img = (ImageView)findViewById(R.id.prod_Img);

        if(getIntent() != null)
            productId = getIntent().getStringExtra("productId");
        if(!productId.isEmpty() && productId != null)
        {
            getProductInformation(productId);
        }

    }

    private void getProductInformation(final String productId) {
        databaseReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                product = dataSnapshot.getValue(Product.class);
                Picasso.with(getBaseContext()).load(product.getProductImageURL()).into(prod_Img);
                collapsing.setTitle(product.getProductName());
                prod_Price.setText(product.getProductPrice());
                prod_Name.setText(product.getProductName());
                prod_Desc.setText(product.getProductDescription());
                prod_Status.setText(product.getProductStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
