package com.example.myapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class SecondActivity extends AppCompatActivity {
    private TextView speedText, time100to200, time0to100;

    private LocationManager locationManager;
    private long start0to100 = -1;
    private long end0to100 = -1;
    private long start100to200 = -1;
    private long end100to200 = -1;

    private boolean passed100 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        speedText = findViewById(R.id.speedText);
        time0to100 = findViewById(R.id.time0to100);
        time100to200 = findViewById(R.id.time100to200);
        Button btnPrev = findViewById(R.id.btnPrev);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            float speedKmh = location.getSpeed() * 3.6f; // m/s -> km/h
            speedText.setText(String.format(Locale.US, "%.0f km/h", speedKmh));

            long currentTime = System.currentTimeMillis();

            if (speedKmh >= 0 && speedKmh < 5 && start0to100 > 0) {
                // Reset
                start0to100 = -1;
                end0to100 = -1;
                start100to200 = -1;
                end100to200 = -1;
                passed100 = false;
                time0to100.setText("--");
                time100to200.setText("--");
            }

            if (speedKmh >= 5 && start0to100 == -1) {
                start0to100 = currentTime;
            }

            if (speedKmh >= 100 && end0to100 == -1) {
                end0to100 = currentTime;
                long timeMs = end0to100 - start0to100;
                time0to100.setText(timeMs / 1000.0 + "s");
                passed100 = true;
                start100to200 = currentTime;
            }

            if (speedKmh >= 200 && passed100 && end100to200 == -1) {
                end100to200 = currentTime;
                long timeMs = end100to200 - start100to200;
                time100to200.setText(timeMs / 1000.0 + "s");
            }
        }
    };
}
