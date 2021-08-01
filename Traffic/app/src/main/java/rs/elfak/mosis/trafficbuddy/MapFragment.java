package rs.elfak.mosis.trafficbuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class MapFragment extends Fragment {

    private MapView mMapViewFullScreen;
    private GoogleMap googleMap;
    private List<MarkerOptions> googleMapMarkers;
    private List<Target> markerTargets;
    private CancellationTokenSource cancellationTokenSource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapViewFullScreen = view.findViewById(R.id.googleMapView);
        mMapViewFullScreen.onCreate(savedInstanceState);
        mMapViewFullScreen.onResume(); // needed to get the map to display immediately
        cancellationTokenSource = new CancellationTokenSource();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapViewFullScreen.getMapAsync(mMap -> {
            googleMap = mMap;

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For zooming automatically to the current user location of the marker
                zoomToLastKnownLocation(googleMap);
            }


            if (googleMapMarkers != null) {
                for (MarkerOptions marker : googleMapMarkers)
                    googleMap.addMarker(marker);
            }

            // For dropping a marker at a point on the Map
            LatLng sydney = new LatLng(43.32, 21.89);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("2"));

            sydney = new LatLng(43.52, 21.89);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("1"));
            googleMap.setOnInfoWindowClickListener(friendClickListener);
            //kada se klikne na dialog iznad markera...ide se friendClickListener, tu je zasad samo Toast
            //dodati otvaranje fragmenta ili forme, mzda bolje fragment
        });
        return view;
    }

    GoogleMap.OnInfoWindowClickListener friendClickListener = marker -> {
        String friendId = marker.getSnippet();
        Bundle bundle = new Bundle();
        Toast.makeText(getContext(), friendId, Toast.LENGTH_LONG).show();
    };

    public GoogleMap getGoogleMapFriends() {
        return googleMap;
    }

    public void zoomToLastKnownLocation(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(getActivity()).getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(8).build();
                            //menjanje zoom-a ovde
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
    }

}