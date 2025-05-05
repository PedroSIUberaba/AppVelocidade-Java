package com.example.myapp;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView locationTextView, speedTextView;
    private Button startButton, stopButton;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os componentes da UI
        locationTextView = findViewById(R.id.locationTextView);
        speedTextView = findViewById(R.id.speedTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        mapView = findViewById(R.id.mapView);

        // Verificar se o MapView foi encontrado
        if (mapView == null) {
            Toast.makeText(this, "Erro: MapView não encontrado no layout", Toast.LENGTH_LONG).show();
            return;
        }

        // Inicializar o MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inicializar o FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar o callback para atualizações de localização
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // Atualizar a UI com a latitude e longitude
                    String locationText = "Latitude: " + location.getLatitude() +
                            "\nLongitude: " + location.getLongitude();
                    locationTextView.setText(locationText);

                    // Calcular e exibir a velocidade (em km/h)
                    if (location.hasSpeed()) {
                        float speedMs = location.getSpeed(); // Velocidade em m/s
                        float speedKmh = speedMs * 3.6f; // Converter para km/h
                        speedTextView.setText(String.format("Velocidade: %.0f km/h", speedKmh));
                    } else {
                        speedTextView.setText("Velocidade: Não disponível");
                    }

                    // Atualizar o mapa com a nova localização
                    if (googleMap != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Você está aqui"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 25));
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
        locationRequest.setInterval(1000); // Atualizar a cada 0,5 segundos
        locationRequest.setFastestInterval(400); // Intervalo mais rápido
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Iniciar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Falha ao iniciar rastreamento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            Toast.makeText(this, "Rastreamento iniciado", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener(aVoid -> {
                    locationTextView.setText("Localização: Aguardando...");
                    speedTextView.setText("Velocidade: Aguardando...");
                    Toast.makeText(this, "Rastreamento parado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Falha ao parar rastreamento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "Permissão de localização necessária para o mapa", Toast.LENGTH_LONG).show();
        }
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
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}