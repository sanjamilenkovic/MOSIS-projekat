package rs.elfak.mosis.trafficbuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Repo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.dialogs.AddReportDialog;
import rs.elfak.mosis.trafficbuddy.dialogs.ReportDialog;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;
import rs.elfak.mosis.trafficbuddy.viewmodel.ReportsViewModel;

public class MapFragment extends Fragment {

    private GoogleMap googleMap;
    private Boolean flag = true;
    private List<MarkerOptions> googleMapMarkers;
    private Boolean allUsersAreSelected = true;
    private Boolean allReportsAreSelected = false;
    private List<Target> markerTargets;
    private CancellationTokenSource cancellationTokenSource;

    @Override
    public void onResume() {
        super.onResume();

        ReportsViewModel viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        if (flag) {
            viewModel.getAllUsers().observe(getViewLifecycleOwner(), user -> {
                if (allUsersAreSelected) {
                    googleMapMarkers = new ArrayList<>();
                    markerTargets = new ArrayList<>();
                    googleMap.clear();
                    for (int i = 0; i < user.size(); i++) {
                        User u = user.get(i);
                        markerTargets.add(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(new LatLng(u.getLat(), u.getLon()));
                                marker.title(u.getName());
                                marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                marker.snippet(u.getUid());

                                googleMapMarkers.add(marker);
                                googleMap.addMarker(marker);
                                googleMap.setOnInfoWindowClickListener(userClickListener);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });


                        Picasso.get().load(u.getImageUrl()).resize(50, 50).into(markerTargets.get(i));
                    }
                }
            });

        } else {
            viewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
                if (allReportsAreSelected) {
                    googleMapMarkers = new ArrayList<>();
                    markerTargets = new ArrayList<>();
                    googleMap.clear();

                    for (int i = 0; i < reports.size(); i++) {
                        Report d = reports.get(i);
                        markerTargets.add(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(new LatLng(d.getLat(), d.getLon()));
                                marker.title(d.getTitle());

                                marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                marker.snippet(d.getId());

                                googleMapMarkers.add(marker);
                                googleMap.addMarker(marker);
                                googleMap.setOnInfoWindowClickListener(reportClickListener);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        });

                        Resources resources = getContext().getResources();
                        final int resourceId = resources.getIdentifier(reports.get(i).getIconTitle(), "drawable",
                                getContext().getPackageName());

                        Picasso.get().load(resourceId).resize(50, 50).into(markerTargets.get(i));


                    }
                }
            });
        }
    }

    final GoogleMap.OnInfoWindowClickListener reportClickListener = marker -> {
    };

    final GoogleMap.OnInfoWindowClickListener userClickListener = marker -> {
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Button changeView = view.findViewById(R.id.changeView);
        MapView mMapViewFullScreen = view.findViewById(R.id.googleMapView);
        mMapViewFullScreen.onCreate(savedInstanceState);
        mMapViewFullScreen.onResume(); // needed to get the map to display immediately
        cancellationTokenSource = new CancellationTokenSource();
        FloatingActionButton addNewReport = view.findViewById(R.id.fab_add_report);

        changeView.setOnClickListener(p -> {
            flag = !flag;
            if (flag) {
                allReportsAreSelected = false;
                allUsersAreSelected = true;
            } else {
                allReportsAreSelected = true;
                allUsersAreSelected = false;
            }
            onResume();
        });

        addNewReport.setOnClickListener(l -> {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation()
                        .addOnSuccessListener(location -> {

                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Bundle addReportB = new Bundle();
                            addReportB.putParcelable("currentLocation", currentLatLng);

                            AddReportDialog ard = new AddReportDialog(getActivity(), addReportB);
                            ard.show();

                        });
        });


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
        });
        return view;
    }

    ;

    public GoogleMap getGoogleMapFriends() {
        return googleMap;
    }

    public void zoomToLastKnownLocation(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.getFusedLocationProviderClient(getActivity()).getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(14).build();
                            //menjanje zoom-a ovde
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
    }
}