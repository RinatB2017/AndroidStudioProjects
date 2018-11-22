package com.boss.test_googlemaps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_test:
                LatLng marker2 = new LatLng(-34, 150);
                LatLng marker3 = new LatLng(-33, 149);

                Marker m1 = mMap.addMarker(new MarkerOptions().position(marker2).title("Marker2"));
                Marker m2 = mMap.addMarker(new MarkerOptions().position(marker3).title("Marker3"));

                Location markerLocation1 = new Location("");
                markerLocation1.setLatitude(marker2.latitude);
                markerLocation1.setLongitude(marker2.longitude);

                Location markerLocation2 = new Location("");
                markerLocation2.setLatitude(marker3.latitude);
                markerLocation2.setLongitude(marker3.longitude);

                PolylineOptions options = new PolylineOptions()
                        .add(m1.getPosition())
                        .add(m2.getPosition())
                        .color(Color.GREEN);
                mMap.addPolyline(options);

                float dist = markerLocation1.distanceTo(markerLocation2);
                Log.i("States", "dist " + dist);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(myLocation != null) {
                LatLng my = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(my).title("HERE"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(my));
            }
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
