package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView cityEditText;
    private Button searchButton;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.forecaButton);
        cityEditText = findViewById(R.id.cityTextView);


        // Initialize location manager and listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Handle location changes, e.g., get weather for the new location
                getWeatherForLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }
        };

        // Check for location permission and request if necessary
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Start listening for location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, ConverterSettings.class);
        startActivity(intent);
    }

    private void parseWeather(String response) {
        try {
            JSONObject weatherJSON = new JSONObject(response);
            String weather = weatherJSON.getJSONArray("weather").getJSONObject(0).getString("main");
            double temperature = weatherJSON.getJSONObject("main").getDouble("temp");
            double wind = weatherJSON.getJSONObject("wind").getDouble("speed");
            String city = weatherJSON.getString("name");

            // Assuming you have TextViews with these IDs in your layout
            TextView tempTextView = findViewById(R.id.tempTextView);
            TextView descTextView = findViewById(R.id.descTextView);
            TextView windTextView = findViewById(R.id.windTextView);
            TextView cityTextView = findViewById(R.id.cityTextView);

            // Get the unit type from SharedPreferences
            SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
            boolean isMetric = preferences.getBoolean("isMetric", true);

            // Convert temperature to the appropriate unit
            String unitSymbol = isMetric ? "°C" : "°F";
            String windUnitSymbol = isMetric ? "m/s" : "mph";
            // Populate TextViews with the data
            tempTextView.setText(getString(R.string.temperature_label) + "\n" + temperature + " " + unitSymbol);
            descTextView.setText(getString(R.string.description_label) + "\n" + weather);
            windTextView.setText(getString(R.string.wind_speed_label) + "\n" + wind + " " + windUnitSymbol);
            cityTextView.setText(city);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing weather data", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openForeca(View view) {
        String foreca = "https://www.foreca.com/100634963/Tampere-Finland";
        Uri forecaUri = Uri.parse(foreca);
        Intent intent = new Intent(Intent.ACTION_VIEW, forecaUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening for location updates when the activity is destroyed
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void getWeatherForLocation(double latitude, double longitude) {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isMetric = preferences.getBoolean("isMetric", true);

        String unit = isMetric ? "metric" : "imperial";
        String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=" +
                latitude + "&lon=" + longitude + "&appid=6c433438776b5be4ac86001dc88de74d&units=" + unit;

        StringRequest request = new StringRequest(Request.Method.GET, WEATHER_URL, response -> {
            parseWeather(response);
        }, error -> {
            Toast.makeText(this, "Error getting weather data", Toast.LENGTH_LONG).show();
        });

        Volley.newRequestQueue(this).add(request);
    }
}
