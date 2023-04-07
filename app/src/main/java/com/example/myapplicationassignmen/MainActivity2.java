package com.example.myapplicationassignmen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private ProgressBar pb = null;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Switch notify;
    String LOC;
    String act;
    TextView text;
    NotificationChannel channel;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        drawerLayout = findViewById(R.id.menu);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        notify = findViewById(R.id.switch1);
        text= findViewById(R.id.textView2);
        ActivityCompat.requestPermissions(MainActivity2.this,
                new String[]{ACCESS_FINE_LOCATION}, 1);
        NotificationManager manager =getSystemService(NotificationManager.class);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            channel= new NotificationChannel("0","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor( Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100,1000,200,340});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ActivityCompat.requestPermissions(MainActivity2.this,
                        new String[]{ACCESS_FINE_LOCATION}, 2);
                if(notify.isChecked()){
                    MyThread n = new MyThread(handler, LOC);
                    n.start();

                }
                handler.postDelayed(this, 10000);
            }
        },10000);



        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nvdrawer = (NavigationView) findViewById(R.id.menu1);
        setupDrawerContent(nvdrawer);

    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity2.this,
                            ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        LocationListener locationListener = new MyLocationListener();

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, locationListener);
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                return;
            }
            case 2: {


                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        LocationListener locationListener = new MyLocationListener();

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, locationListener);
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                return;
            }

        }
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        if (item.getItemId() == R.id.nav_settings) {
                            item.setChecked(true);
                            drawerLayout.closeDrawers();
                            return true;
                        } else
                            return false;
                    }
                });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOC = cityName;
            System.out.println(LOC);
            text.setText(LOC);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
    private String readStream(InputStream in) {
        try {
            ByteArrayOutputStream oo = new ByteArrayOutputStream();
            int i = in.read();
            while (i != -1) {
                oo.write(i);
                i = in.read();

            }
            return oo.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private class MyThread extends Thread {
        private Handler handler;
        private String loc;
        public MyThread(Handler hanlder,String loc) {
            handler = hanlder;
            this.loc=loc;
        }
        String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZpZm5obmJ5ZXhucXZ5Z3lqemZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODAyMjUxNzcsImV4cCI6MTk5NTgwMTE3N30.k0I9NLl2Xulj6lkLR09NjHxdDD5W-My9Dr8yyoqZNJY";

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            Looper.prepare();
            try {

                URL a = new URL("https://fifnhnbyexnqvygyjzfz.supabase.co/rest/v1/City?Name=eq."+LOC);
                HttpURLConnection hv = (HttpURLConnection) a.openConnection();
                hv.setRequestProperty("apikey", key);
                hv.setRequestProperty("Authorization", "Bearer " + key);

                String result="";
                InputStream in = new BufferedInputStream(hv.getInputStream());
                result = readStream(in);
                Intent i = new Intent(getApplicationContext(),MainActivity2.class);
                i.putExtra("res",result);
                JSONArray ao  = new JSONArray(i.getStringExtra("res"));
                JSONObject o0 = ao.getJSONObject(0);
                act =o0.getString("Covid state");
                if (act.equals("Active")) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity2.this, "0")
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle("Covid")
                            .setContentText(LOC+" is Covid active!!!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{100,1000,200,340});


                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity2.this);
                    managerCompat.notify(Integer.parseInt(channel.getId()), builder.build());

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
