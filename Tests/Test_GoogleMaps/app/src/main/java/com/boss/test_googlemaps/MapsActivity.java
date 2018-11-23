package com.boss.test_googlemaps;

import android.content.Context;
import android.content.Intent;
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

    private List<LatLng> places = new ArrayList<>();
    private String mapsApiKey;
    private int width;
    final int DEFAULT_ZOOM = 15;
    String path;
    String filename;

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

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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
