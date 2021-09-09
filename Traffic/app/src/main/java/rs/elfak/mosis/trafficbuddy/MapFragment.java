package rs.elfak.mosis.trafficbuddy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.core.Repo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.dialogs.AddReportDialog;
import rs.elfak.mosis.trafficbuddy.dialogs.FilterDialog;
import rs.elfak.mosis.trafficbuddy.dialogs.FriendDialog;
import rs.elfak.mosis.trafficbuddy.dialogs.ReportDialog;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;
import rs.elfak.mosis.trafficbuddy.viewmodel.ReportsViewModel;

public class MapFragment extends Fragment implements FilterDialog.FilterClickListener {

    private GoogleMap googleMap;
    private Boolean flag = true;
    private List<MarkerOptions> googleMapMarkers;
    private EditText searchMap;
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
                    showAllReports(reports);

//                    googleMapMarkers = new ArrayList<>();
//                    markerTargets = new ArrayList<>();
//                    googleMap.clear();
//
//                    for (int i = 0; i < reports.size(); i++) {
//                        Report d = reports.get(i);
//                        markerTargets.add(new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                MarkerOptions marker = new MarkerOptions();
//                                marker.position(new LatLng(d.getLat(), d.getLon()));
//                                marker.title(d.getTitle());
//
//                                marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                                marker.snippet(d.getId());
//
//                                googleMapMarkers.add(marker);
//                                googleMap.addMarker(marker);
//                                googleMap.setOnInfoWindowClickListener(reportClickListener);
//                            }
//
//                            @Override
//                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                            }
//
//                            @Override
//                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                            }
//                        });
//
//                        Resources resources = getContext().getResources();
//                        final int resourceId = resources.getIdentifier(reports.get(i).getIconTitle(), "drawable",
//                                getContext().getPackageName());
//
//                        Picasso.get().load(resourceId).resize(50, 50).into(markerTargets.get(i));
//
//
//                    }
//                }
                }
            });
        }
    }

    final GoogleMap.OnInfoWindowClickListener reportClickListener = marker -> {

        //Toast.makeText(getContext(), "Report Selected", Toast.LENGTH_SHORT).show();

        String idReport = marker.getSnippet();
        Toast.makeText(getContext(), idReport, Toast.LENGTH_SHORT).show();

        Firebase.getDbRef().child(Firebase.DB_REPORTS).child(idReport).get().addOnSuccessListener(snapshot -> {
            Report report = snapshot.getValue(Report.class);
            ReportDialog rd = new ReportDialog((Activity) getContext(), report);
            rd.show();
        });
    };

    final GoogleMap.OnInfoWindowClickListener userClickListener = marker -> {
        //Toast.makeText(getContext(), "User selected", Toast.LENGTH_SHORT).show();

        String idFriend = marker.getSnippet();
        //Toast.makeText(getContext(), idFriend, Toast.LENGTH_SHORT).show();

        Firebase.getDbRef().child(Firebase.DB_USERS).child(idFriend).get().addOnSuccessListener(snapshot -> {
            User friend = snapshot.getValue(User.class);
            FriendDialog fd = new FriendDialog((Activity) getContext(), friend);
            fd.show();
        });
    };

    public void showAllReports(List<Report> reports) {
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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        searchMap = view.findViewById(R.id.searchMap);
        searchMap.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    hideKeyboardFrom(getContext(), view);
                    Toast.makeText(getContext(), searchMap.getText(), Toast.LENGTH_SHORT).show();
                    String s = searchMap.getText().toString().toLowerCase();
                    prikaziFiltriraneReportove(s, null, null);
                    searchMap.setText(" ");

                    return true;
                }
                return false;
            }
        });

        FloatingActionButton addNewReport = view.findViewById(R.id.fab_add_report);
        FloatingActionButton filterDate = view.findViewById(R.id.fab_filter_date);
        filterDate.setOnClickListener(l -> {
            FilterDialog fd = new FilterDialog(getActivity());
            fd.setFilterClickListener(this);
            fd.show();
        });

        changeView.setOnClickListener(p -> {
            flag = !flag;
            if (flag) {
                allReportsAreSelected = false;
                allUsersAreSelected = true;
                changeView.setText("VIEW REPORTS");
            } else {
                allReportsAreSelected = true;
                allUsersAreSelected = false;
                changeView.setText("VIEW USERS");

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prikaziFiltriraneReportove(String title, String startDate, String radius) {

        flag = !flag;
        if (flag) {
            allReportsAreSelected = false;
            allUsersAreSelected = true;
        } else {
            allReportsAreSelected = true;
            allUsersAreSelected = false;

        }

        Firebase.getDbRef().child(Firebase.DB_REPORTS).get().addOnSuccessListener(snapshot -> {
            ArrayList<Report> all = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Report report = child.getValue(Report.class);
                all.add(report);
            }

            ArrayList<Report> filteredReports = new ArrayList<Report>();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            filteredReports = all;

            if (!(radius == null || radius.length() == 0)) {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double distance = Double.parseDouble(radius);

                filteredReports.removeIf(d -> {
                    Location currentLocation = new Location("");
                    currentLocation.setLongitude(d.getLon());
                    currentLocation.setLatitude(d.getLat());
                    return myLocation.distanceTo(currentLocation) > distance;
                });
            }

            if (title != null) {
                filteredReports.removeIf(
                        d -> !d.getIconTitle().equals(title)
                );
            }

            if (startDate != null) {
                ArrayList<Report> filtered2 = new ArrayList<>(filteredReports);

                filteredReports.clear();

                for (Report r : filtered2) {
                    String dateAdded = r.getDate();
                    try {
                        if (sdf.parse(startDate).before(sdf.parse(dateAdded))) {
                            filteredReports.add(r);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }


            if (filteredReports.size() > 0)
                showAllReports(filteredReports);
            else {
                Toast.makeText(getContext(), "Nema reportova koji zadovoljavaju prosledjene filtere!", Toast.LENGTH_LONG).show();
                showAllReports(all);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }


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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onIconClick(String title, String date, String radius) {
        Toast.makeText(getContext(), "vraceno " + title + " " + date + " " + radius, Toast.LENGTH_SHORT).show();
        prikaziFiltriraneReportove(title, date, radius);
    }


}