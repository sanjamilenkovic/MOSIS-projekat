package rs.elfak.mosis.trafficbuddy.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import rs.elfak.mosis.trafficbuddy.MainActivity;
import rs.elfak.mosis.trafficbuddy.MapActivity;
import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class RealTimeLocationService extends Service {

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private final String CHANNEL_ID = "traffic_buddy_channel";
    private NotificationChannel channel;

    public RealTimeLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createNotificationChannel();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location lastLocation = locationResult.getLastLocation();
                FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
                if (loggedInUser != null) {
                    String currentUserId = loggedInUser.getUid();
                    Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserId).child("lat").setValue(lastLocation.getLatitude());
                    Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserId).child("lon").setValue(lastLocation.getLongitude());
                    getNearbyFriends(lastLocation);

                    getNearbyReports(lastLocation);

                }
            }
        };
    }

    private void getNearbyReports(Location lastLocation) {
        Firebase.getDbRef().child(Firebase.DB_REPORTS).get().addOnSuccessListener(snapshot -> {
            int index = 0;
            for (DataSnapshot child : snapshot.getChildren()) {

                Report report = child.getValue(Report.class);
                Location loc = new Location("");

                loc.setLatitude(report.getLat());
                loc.setLongitude(report.getLon());
                if (lastLocation.distanceTo(loc) < 1000.0f) {
                    createNotification(report.getTitle(),index++,false);

                }
            }

        });
    }
    private void getNearbyFriends(Location lastLocation) {
        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();

        Firebase.getDbRef().child(Firebase.DB_USERS).child(loggedInUser.getUid()).get().addOnSuccessListener(snapshot -> {
            User u = snapshot.getValue(User.class);
            for(int i =0; i<u.getFriends().size();i++)
            {

                Firebase.getDbRef().child(Firebase.DB_USERS).child(u.getFriends().get(i)).get().addOnSuccessListener(sn -> {
                    User user = sn.getValue(User.class);
                    Location loc = new Location("");
                    loc.setLatitude(user.getLat());
                    loc.setLongitude(user.getLon());
                    if (lastLocation.distanceTo(loc) < 10000.0f) {
                        createNotification(user.getName(),i+100,true);

                    }

                });
        break;
            }


        });
    }

    private void createNotification(String title,int i ,boolean isFriend) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder;
        if (isFriend == false) {
             builder = new NotificationCompat.Builder(RealTimeLocationService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.oprez)
                    .setContentTitle("Be aware!")
                    .setContentText("There's a danger nearby. " + title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
        } else {
             builder = new NotificationCompat.Builder(RealTimeLocationService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                    .setContentTitle("Friend is close!")
                    .setContentText("Your friend : " + title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(i, builder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.traffic_notification_channel_name);
            String description = getString(R.string.traffic_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


}
