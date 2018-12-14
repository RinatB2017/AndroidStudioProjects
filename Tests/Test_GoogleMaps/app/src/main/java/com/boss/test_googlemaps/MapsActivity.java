package com.boss.test_googlemaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    private List<LatLng> places = new ArrayList<>();
    private String mapsApiKey;
    private int width;
    final int DEFAULT_ZOOM = 15;
    String path;
    String filename;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            drawMarker(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            drawMarker(mLocationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i("States", "onProviderDisabled");
        }
    };


    private void initMap() {
        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (googlePlayStatus != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
            finish();
        } else {
            if (mMap != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGPSEnabled || isNetworkEnabled))
            return;
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE,
                        mLocationListener);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE,
                        mLocationListener);
            }
        }
    }

    private void drawMarker(Location location) {
        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            Marker m_test = mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Lat: " + gps.latitude + " long: " + gps.longitude));
            m_test.showInfoWindow();
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(gps, DEFAULT_ZOOM);
            mMap.moveCamera(update);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //---
        mapsApiKey = this.getResources().getString(R.string.map_v2_api_key);
        width = getResources().getDisplayMetrics().widthPixels;

        Intent intent = getIntent();
        if(intent != null) {
            filename = intent.getStringExtra("filename");
            if (filename != null) {
                if (!filename.isEmpty()) {
                    File gpxFile = new File(filename);

                    List<Location> gpxList = decodeGPX(gpxFile);

                    places.clear();
                    for (int i = 0; i < gpxList.size(); i++) {
                        places.add(new LatLng(gpxList.get(i).getLatitude(), gpxList.get(i).getLongitude()));
                    }
                }
            }
        }
        //---
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        initMap();
        getCurrentLocation();
        //---
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
            case R.id.action_open_file:
                Intent intent = new Intent(this, FileActivity.class);
                startActivity(intent);
                break;

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

            case R.id.action_test2:
                LatLng marker = new LatLng(50.06169631, 19.93919566);
                Marker m_test = mMap.addMarker(new MarkerOptions().position(marker).title("Lat: " + marker.latitude + "long: " + marker.longitude));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(marker, DEFAULT_ZOOM);
                m_test.showInfoWindow();
                mMap.moveCamera(update);
                break;

            case R.id.action_test3:
                getCurrentLocation();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---

    boolean tap_on_map() {
        // поставить маркер по тапу
        if(mMap == null) {
            return false;
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker");

                mMap.addMarker(marker);
                //System.out.println(point.latitude + "---" + point.longitude);
            }
        });
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        tap_on_map();

        if(places.size() > 0) {
            MarkerOptions[] markers = new MarkerOptions[places.size()];
            for (int i = 0; i < places.size(); i++) {
                markers[i] = new MarkerOptions()
                        .position(places.get(i));
                mMap.addMarker(markers[i]);
            }

            if(filename != null) {
                File gpxFile = new File(filename);
                List<Location> gpxList = decodeGPX(gpxFile);
                if(gpxList != null) {
                    LatLng currentLatLng = new LatLng(gpxList.get(0).getLatitude(), gpxList.get(0).getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                    googleMap.moveCamera(update);
                }
            }
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private List<Location> decodeGPX(File file){
        List<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }
            fileInputStream.close();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
}
