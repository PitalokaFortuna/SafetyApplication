package com.example.safetyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    Button btn_logout, btn_maps;
    TextView txt_id, txt_username;
    String id, username;
    SharedPreferences sharedpreferences;

    //UI Component
    TextView Text_X_Gyro, Text_Y_Gyro, Text_Z_Gyro;
    TextView Text_X_Acclero, Text_Y_Acclero, Text_Z_Acclero;
    public static String klasifikasi = "";

    //Bolean(Penanda/ flagging untuk merekam file)
    boolean isRunning;

    private static float smoothed[] = new float[3];

    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;

    private static final float grav[] = new float[3];
    private static final float data[] = new float[3];

    //Float Variabel
    float x_gyro;
    float y_gyro;
    float z_gyro;
    float x_acclero;
    float y_acclero;
    float z_acclero;

    //File Stream
    FileWriter writer;
    final String TAG = "SensorLog";

    //time
    SimpleDateFormat dateFormat;
    String time_string;
    Timer timer;

    //Sensor Component
    SensorManager sensorManager;
    Sensor gyro_sensor;
    Sensor acclero_sensor;

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "nama";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acclero_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        isRunning = false;
        timer = new Timer();

        time_string = new String();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acclero_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        time_string = new String();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_username = (TextView) findViewById(R.id.txt_username);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_maps = (Button) findViewById(R.id.btn_maps);
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);

        txt_id.setText("ID : " + id);
        txt_username.setText("USERNAME : " + username);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(TAG_ID, id);
                finish();
                startActivity(intent);
            }
        });
    }
}