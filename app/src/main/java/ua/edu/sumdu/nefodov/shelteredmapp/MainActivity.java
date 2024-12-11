package ua.edu.sumdu.nefodov.shelteredmapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, ShelteredDataListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation = new LatLng(50.9216, 34.80029); // Sumy LatLng
    private ShelteredAPI shelteredService;
    private List<Shelter> shelterList;
    private HashMap<LatLng, Marker> markers;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        markers = new HashMap<>();
        shelteredService = ShelteredAPI.getInstance();
        Button refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(view -> {
            //initiate shelters request on click
            shelteredService.requestShelters(this);
        });

        Button findClosestShelterBtn = findViewById(R.id.findClosest);
        findClosestShelterBtn.setOnClickListener(view -> {
            findClosestShelter();
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //getLastLocation();

        //set up Google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMarkerClickListener(marker -> {
            startShelterInfoActivity(marker.getPosition());
            return false;
        });
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                findViewById(R.id.refreshBtn).setEnabled(false);
                findViewById(R.id.findClosest).setEnabled(false);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                markers.remove(userLocation);
                userLocation = marker.getPosition();
                markers.put(userLocation, marker);
                findViewById(R.id.refreshBtn).setEnabled(true);
                findViewById(R.id.findClosest).setEnabled(true);
            }
        });
        float zoomLevel = 12.0f;
        markers.put(userLocation, createUserMarker(userLocation));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        Marker userMarker = markers.get(userLocation);
                        if (userMarker != null) {
                            userMarker.remove();
                        }
                        markers.remove(userLocation);
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        markers.put(userLocation, createUserMarker(userLocation));
                    } else {
                        System.out.println("Не вдалося отримати місцеположення");
                    }
                });
    }

    private void displayShelters(List<Shelter> shelters) {
        for (Marker marker : markers.values()) {
            if (marker.getPosition() != userLocation) {
                marker.remove();
            }
        }
        markers.clear();

        for (Shelter shelter : shelters) {
            LatLng location = new LatLng(shelter.getLatitude(), shelter.getLongitude());
            markers.put(location, gMap.addMarker(new MarkerOptions().position(location)));
        }
        markers.put(userLocation, createUserMarker(userLocation));
    }

    @Override
    public void onSheltersReceived(List<Shelter> shelters) {
        shelterList = shelters;
        /*save shelters to DB
        for (Shelter s : shelters) {
            shelterDao.insert(s);
        }

         */

        //show shelters
        runOnUiThread(() -> {
            displayShelters(shelters);
            Toast.makeText(MainActivity.this, "Сховища оновлено!", Toast.LENGTH_SHORT).show();
        });

    }

    public void startShelterInfoActivity(LatLng coordinates) {
        // find shelter by LatLng
        if (coordinates != userLocation) {
            Optional<Shelter> foundShelter = shelterList.stream().filter(shelter ->
                    shelter.getLatitude() == coordinates.latitude
                            && shelter.getLongitude() == coordinates.longitude).findFirst();
            if (foundShelter.isPresent()) {
                Intent intent = new Intent(MainActivity.this, ShelterInfoActivity.class);
                Shelter shelter = foundShelter.get();

                //send shelter to ShelterInfoActivity
                Bundle shelterInfo = new Bundle();
                shelterInfo.putDouble("latitude", shelter.getLatitude());
                shelterInfo.putDouble("longitude", shelter.getLongitude());
                shelterInfo.putString("status", shelter.getStatus().name());
                ArrayList<String> conditionValues = new ArrayList<>();
                for (ShelterCondition condition : shelter.getConditions()) {
                    conditionValues.add(condition.name());
                }
                shelterInfo.putStringArrayList("conditions", conditionValues);
                shelterInfo.putInt("capacity", shelter.getCapacity());
                shelterInfo.putDouble("area", shelter.getArea());
                shelterInfo.putString("additional", shelter.getAdditional());
                intent.putExtra("shelter_info", shelterInfo);

                Bundle userLocBundle = new Bundle();
                userLocBundle.putDouble("latitude", userLocation.latitude);
                userLocBundle.putDouble("longitude", userLocation.longitude);
                intent.putExtra("user_location", userLocBundle);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Помилка зі сховищем", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Ваша локація", Toast.LENGTH_SHORT).show();
        }
    }

    public double haversineDistance(double startLat, double startLng, double endLat, double endLng) {
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public void highlightClosestMarker(LatLng coords) {
        Marker marker = markers.get(coords);
        if (marker != null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Log.i(TAG, "Nearest marker: " + coords.toString());
        } else {
            Toast.makeText(MainActivity.this, "Неможливо знайти найближче сховище", Toast.LENGTH_SHORT).show();
        }
    }

    public void findClosestShelter() {
        if (!shelterList.isEmpty()) {
            double minLat = 0;
            double minLng = 0;
            double minDistance = Double.MAX_VALUE;
            Log.i(TAG, userLocation.toString());
            for (Shelter shelter : shelterList) {

                double distance = haversineDistance(userLocation.latitude, userLocation.longitude, shelter.getLatitude(), shelter.getLongitude());
                //double distance = Math.sqrt(Math.pow(userLocation.latitude - shelter.getLatitude(), 2) + Math.pow(userLocation.longitude - shelter.getLongitude(), 2));
                Log.i(TAG, "Distance: " + distance + "\n Coordinates: " + shelter.getLatitude() + ' ' + shelter.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    minLat = shelter.getLatitude();
                    minLng = shelter.getLongitude();
                }
            }
            Log.i(TAG, "Minimal distance: " + minDistance);
            highlightClosestMarker(new LatLng(minLat, minLng));
        } else {
            Toast.makeText(MainActivity.this, "Оновіть дані про сховища", Toast.LENGTH_SHORT).show();
        }
    }

    private Marker createUserMarker(LatLng position) {
        return gMap.addMarker(new MarkerOptions().position(userLocation)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
}