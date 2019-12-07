package com.kazishihan.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kazishihan.myapplication.Weather.WeatherResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private WeatherResult weatherResult;
    public Location location;

    private TextView currentWeatherDiscription, currentWeathertemp, currentWeatherWind, currentWeatherHumidity;
    private ImageView currentWeatherIcon;
    String url;
    private String units = "metric";
    FusedLocationProviderClient fusedLocationProviderClient;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherResult = new WeatherResult();
        recyclerView = findViewById(R.id.recyclerCiewID);


        currentWeatherDiscription = findViewById(R.id.cityNameCurrentTvId);
        currentWeatherIcon = findViewById(R.id.weatherCurrentIconIvId);
        currentWeathertemp = findViewById(R.id.tempCurrentWeitherTvId);
        currentWeatherWind = findViewById(R.id.windCurrentWeitherTvId);
        currentWeatherHumidity = findViewById(R.id.humidityCurrentWeitherTvId);


        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Please wait......");
        mProgressDialog.show();


        weatherResult = new WeatherResult();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // getWeatherUpdate("forecast?lat=23.7533312&lon=90.3769738&units=metric&appid=a0e0d52b2dbb8228d3f19466bb398fd0");
    }


    private void getMyLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    location = task.getResult();
                    url = String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s", location.getLatitude(), location.getLongitude(), units, getResources().getString(R.string.appid));
                    // Toast.makeText(WeatherActivity.this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();

                    try {
                        getAddress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getWeatherUpdate(url);
                }

            }
        });

    }

    private void getWeatherUpdate(String url) {

        IOpenWeatherMap iOpenWeatherMap = RetrofitClass.getRetrofitInstance().create(IOpenWeatherMap.class);

        // Call<WeatherResult> weatherResultCall= iOpenWeatherMap.getWeatherData("forecast?lat=23.7533312&lon=90.3769738&units=metric&appid=a0e0d52b2dbb8228d3f19466bb398fd0");
        Call<WeatherResult> weatherResultCall = iOpenWeatherMap.getWeatherData(url);

        weatherResultCall.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if (response.code() == 200) {
                    weatherResult = response.body();
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    weatherAdapter = new WeatherAdapter(MainActivity.this, weatherResult);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(weatherAdapter);


///////////////// show current weather

                    currentWeatherDiscription.setText("" + weatherResult.getList().get(0).getWeather().get(0).getDescription());
                    Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                            .append(weatherResult.getList().get(0).getWeather().get(0).getIcon())
                            .append(".png").toString()).into(currentWeatherIcon);
                    currentWeathertemp.setText("Temp: " + weatherResult.getList().get(0).getMain().getTemp() + " °C");
                    currentWeatherWind.setText("Wind :" + weatherResult.getList().get(0).getWind().getSpeed() + " km/h");

                    mProgressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {

            }
        });

    }


    private void getAddress() throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        String addressString = "";
        if (addresses.get(0).getLocality() != null) {
            addressString += addresses.get(0).getThoroughfare() + ", ";
        }
        if (addresses.get(0).getAdminArea() != null) {
            addressString += addresses.get(0).getLocality() + ", ";
        }
        if (addresses.get(0).getCountryName() != null) {
            addressString += addresses.get(0).getCountryName();
        }

        try {
            getSupportActionBar().setTitle(addressString);
        } catch (Exception s) {

        }

    }


}
