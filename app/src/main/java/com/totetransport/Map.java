package com.totetransport;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker marker;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    GeoFire geoFire;
    private static final int Requse_Code_Granded_Premission = 1048;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        geoFire = new GeoFire( FirebaseDatabase.getInstance().getReference("Rider_Locations") );

    }
    private void PrebareLoactionRequset() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( 1000 );
        locationRequest.setSmallestDisplacement( .00001f );
        locationRequest.setFastestInterval( 1000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
    }
    private void PrebarreCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult( locationResult );
                List<Location> locations = locationResult.getLocations();
                if(locations.size()>0){
                    Location mLastLoaction = locations.get( locations.size()-1 );
                    geoFire.setLocation( FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation( mLastLoaction.getLatitude(), mLastLoaction.getLongitude() ), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if(marker!=null)
                                marker.remove();
                            marker = mMap.addMarker( new MarkerOptions().title( FirebaseAuth.getInstance().getCurrentUser().getDisplayName() )
                            .position( new LatLng( mLastLoaction.getLatitude(),mLastLoaction.getLongitude() ) ));
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( mLastLoaction.getLatitude(),mLastLoaction.getLongitude() ),10 ) );
                        }
                    } );
                }
            }
        };
    }

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng( -34, 151 );
        mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );
    }
}