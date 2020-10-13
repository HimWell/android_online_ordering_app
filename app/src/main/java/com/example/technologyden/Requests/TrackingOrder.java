package com.example.technologyden.Requests;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.example.technologyden.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in South Africa, and move the camera.
        LatLng sa = new LatLng(-26.270760, 28.112268);
        mMap.addMarker(new MarkerOptions().position(sa).title("Marker in South Africa"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sa));
    }
}



