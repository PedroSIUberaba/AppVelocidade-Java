package com.example.myapp; // Substitua pelo seu pacote

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView locationTextView, speedTextView;
    private Button startButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os componentes da UI
        locationTextView = findViewById(R.id.locationTextView);
        speedTextView = findViewById(R.id.speedTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Inicializar o FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar o callback para atualizações de localização
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Atualizar a UI com a latitude e longitude
                    String locationText = "Latitude: " + location.getLatitude() +
                            "\nLongitude: " + location.getLongitude();
                    locationTextView.setText(locationText);

                    // Calcular e exibir a velocidade (em km/h)
                    if (location.hasSpeed()) {
                        float speedMs = location.getSpeed(); // Velocidade em m/s
                        float speedKmh = speedMs * 3.6f; // Converter para km/h
                        speedTextView.setText(String.format("Velocidade: %.2f km/h", speedKmh));
                    } else {
                        speedTextView.setText("Velocidade: Não disponível");
                    }
                }
            }
        };

        // Configurar os botões
        startButton.setOnClickListener(v -> startLocationUpdates());
        stopButton.setOnClickListener(v -> stopLocationUpdates());
    }

    private void startLocationUpdates() {
        // Verificar permissões
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Configurar a solicitação de localização
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Atualizar a cada 5 segundos
        locationRequest.setFastestInterval(2000); // Intervalo mais rápido
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Iniciar atualizações de localização
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Toast.makeText(this, "Rastreamento iniciado", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        locationTextView.setText("Localização: Aguardando...");
        speedTextView.setText("Velocidade: Aguardando...");
        Toast.makeText(this, "Rastreamento parado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}