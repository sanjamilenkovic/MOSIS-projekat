package rs.elfak.mosis.trafficbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import rs.elfak.mosis.trafficbuddy.services.RealTimeLocationService;

public class MapActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    public void  onStart() {
        super.onStart();
        Intent service = new Intent(this, RealTimeLocationService.class);
        startService(service);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpNavigation();
    }

    public void setUpNavigation() {
        bottomNavigationView = findViewById(R.id.bttm_nav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
    }
}