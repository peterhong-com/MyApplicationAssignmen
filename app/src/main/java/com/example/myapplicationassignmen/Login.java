package com.example.myapplicationassignmen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Login extends AppCompatActivity {
    String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZpZm5obmJ5ZXhucXZ5Z3lqemZ6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODAyMjUxNzcsImV4cCI6MTk5NTgwMTE3N30.k0I9NLl2Xulj6lkLR09NjHxdDD5W-My9Dr8yyoqZNJY";

    EditText login;
    EditText register;
    Button Login;
    Button Register;

    final Handler handler= new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.AccountName);
        register = findViewById(R.id.Password);
        Login = findViewById(R.id.button);
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            String name = null;
                            String password = null;
                            name = login.getText().toString();
                            password = register.getText().toString();
                            System.out.println(password);
                            URL a = new URL("https://fifnhnbyexnqvygyjzfz.supabase.co/rest/v1/Account");
                            HttpURLConnection hv = (HttpURLConnection) a.openConnection();
                            hv.setRequestProperty("apikey", key);
                            hv.setRequestProperty("Authorization", "Bearer " + key);

                            hv.setRequestMethod("POST");
                            hv.setRequestProperty("Content-Type", "application/json");
                            hv.setRequestProperty("Prefer", "return=minimal");
                            hv.setDoOutput(true);

                            JSONObject o0 = new JSONObject();

                            o0.put("Name", name);
                            o0.put("Password", password);

                            OutputStream out = hv.getOutputStream();
                            out.write(o0.toString().getBytes());
                            out.flush();
                            if(hv.getResponseCode()==200){
                                Intent i =  new Intent(getApplicationContext(),MainActivity2.class);
                                startActivity(i);
                            }
                            else{
                                Log.e("This error","Responcode "+hv.getResponseCode());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread thread = new MyThread(handler);
                thread.start();


        }
    });
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

        public MyThread(Handler hanlder) {
            handler = hanlder;
        }

        public void run() {
            Looper.prepare();
            try {
                String name = null;
                String password = null;
                name=login.getText().toString();
                password=register.getText().toString();
                URL a = new URL("https://fifnhnbyexnqvygyjzfz.supabase.co/rest/v1/Account?Name=eq." + name);
                HttpURLConnection hv = (HttpURLConnection) a.openConnection();
                hv.setRequestProperty("apikey", key);
                hv.setRequestProperty("Authorization", "Bearer " + key);

                String result="";
                InputStream in = new BufferedInputStream(hv.getInputStream());
                result = readStream(in);
                Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                i.putExtra("res", result);
                JSONArray jsonArray = new JSONArray(i.getStringExtra("res"));
                JSONObject o0 = jsonArray.getJSONObject(0);
                i.putExtra("name",o0.getString("Name"));
                if (o0.getString("Name").equals(name)&&o0.getString("Password").equals(password)) {

                    startActivity(i);
                } else if(o0==null){
                    Toast.makeText(getApplicationContext(),"Login Failure",Toast.LENGTH_LONG).show();
                    Log.e("This error", "Responcode " + hv.getResponseCode());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}}

