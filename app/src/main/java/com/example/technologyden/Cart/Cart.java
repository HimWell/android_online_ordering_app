package com.example.technologyden.Cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.technologyden.Models.Order;
import com.example.technologyden.Models.Request;
import com.example.technologyden.Payment.Config;
import com.example.technologyden.R;
import com.example.technologyden.User.UserLocalStored;
import com.example.technologyden.ViewHolders.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recycler_Cart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    TextView status;
    TextView totalPrice;
    Button btnPlaceOrder;

    List<Order> orders = new ArrayList<>();

    static PayPalConfiguration payPalConfiguration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PayPalClientID);

    String payPalAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        startService(intent);

        recycler_Cart = (RecyclerView)findViewById(R.id.recycler_Cart);
        recycler_Cart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_Cart.setLayoutManager(layoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference =  firebaseDatabase.getReference("Requests");
        databaseReference2 = firebaseDatabase.getReference("Orders");

        status = (TextView)findViewById(R.id.status);
        totalPrice = (TextView)findViewById(R.id.total);
        btnPlaceOrder = (Button)findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
                alertDialog.setTitle("One More Step!");
                alertDialog.setMessage("Enter Shipping Address:");

                final EditText address = new EditText(Cart.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                address.setLayoutParams(layoutParams);
                alertDialog.setView(address);
                alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        payPalAddress = address.getText().toString();

                        String formatAmount = totalPrice.getText().toString().replace("$", "")
                                .replace(",","");

                        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                                "USD", "Technology Den Order", PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

//                        Request request = new Request(UserLocalStored.currentUser.getPhoneNumber(),
//                                UserLocalStored.currentUser.getName(),address.getText().toString(),totalPrice.getText().toString(),orders, status.getText().toString());
//                        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(request);
//                        Toast.makeText(Cart.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
//
//                        databaseReference2.getRef().removeValue();

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
        });



        if(UserLocalStored.currentUser.getIsAdmin().equals("true")) {
            loadAllOrders();
        }
        else
        loadCart(UserLocalStored.currentUser.getPhoneNumber());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(paymentConfirmation != null) {
                    try {
                        String paymentDetail = paymentConfirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);
                        Request request = new Request(UserLocalStored.currentUser.getPhoneNumber(),
                                UserLocalStored.currentUser.getName(),payPalAddress,totalPrice.getText().toString(),orders, status.getText().toString(),
                                jsonObject.getJSONObject("response").getString("state"));
                        databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                        Toast.makeText(Cart.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
                        databaseReference2.getRef().removeValue();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllOrders() {
        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(Order.class, R.layout.cart_item,
                OrderViewHolder.class, databaseReference2) {
            @Override
            protected void populateViewHolder(final OrderViewHolder orderViewHolder, final Order order, int position) {
                orderViewHolder.cart_item_name.setText(order.getProductName());
                orderViewHolder.cart_item_count.setText(order.getQuantity());
                orderViewHolder.cart_item_price.setText(order.getPrice());

                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orders.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Order order1 = dataSnapshot.getValue(Order.class);
                            orders.add(order1);

                            int total = 0;
                            for(int i = 0; i < orders.size(); i++) {
                                total += (Integer.parseInt(orders.get(i).getPrice())) * (Integer.parseInt(orders.get(i).getQuantity()));
                                Locale locale = new Locale("en", "US");
                                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                                totalPrice.setText(numberFormat.format(total));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        recycler_Cart.setAdapter(adapter);
    }

    private void loadCart(String phoneNumber) {

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(Order.class, R.layout.cart_item,
                OrderViewHolder.class, databaseReference2.orderByChild("phoneNumber").equalTo(phoneNumber)) {
            @Override
            protected void populateViewHolder(final OrderViewHolder orderViewHolder, final Order order, int position) {
                     orderViewHolder.cart_item_name.setText(order.getProductName());
                     orderViewHolder.cart_item_count.setText(order.getQuantity());
                     orderViewHolder.cart_item_price.setText(order.getPrice());

                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            orders.clear();
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Order order1 = dataSnapshot.getValue(Order.class);
                                orders.add(order1);

                                int total = 0;
                                for(int i = 0; i < orders.size(); i++) {
                                    total += (Integer.parseInt(orders.get(i).getPrice())) * (Integer.parseInt(orders.get(i).getQuantity()));
                                    Locale locale = new Locale("en", "US");
                                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                                    totalPrice.setText(numberFormat.format(total));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
        };
        recycler_Cart.setAdapter(adapter);
    }
}
