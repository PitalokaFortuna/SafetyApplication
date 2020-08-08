package com.example.safetyapplication;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class lvq_normal extends AppCompatActivity {
    //UI Component
    TextView Text_X_Gyroscope, Text_Y_Gyroscope, Text_Z_Gyroscope;
    TextView Text_X_Accelerometer, Text_Y_Accelerometer, Text_Z_Accelerometer;
    TextView Text_X_Filter_Accelerometer, Text_Y_Filter_Accelerometer, Text_Z_Filter_Accelerometer;
    TextView Text_X_Filter_Gyroscope, Text_Y_Filter_Gyroscope, Text_Z_Filter_Gyroscope;
    TextView Text_Terkecil, Text_Aktivitas, Text_LVQ;

    //Sensor Component
    SensorManager sensorManager;
    Sensor gyro_sensor;
    Sensor acclero_sensor;

    Button buttonStart;
    Button buttonStop;

    //Float Variabel
    float x_gyro, y_gyro, z_gyro;
    float x_acclero, y_acclero, z_acclero;
    float x_filter_acc, y_filter_acc, z_filter_acc;
    float x_filter_gyro, y_filter_gyro, z_filter_gyro;
    float lvq;
    public static String klasifikasi = "";

    private static float smoothed[] = new float[3];

    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;

    boolean isRunning;
    private static final float grav[] = new float[3];
    private static final float data[] = new float[3];
    //File Stream
    FileWriter writer;
    final String TAG = "SensorLog";

    //time
    SimpleDateFormat dateFormat;
    String time_string;

}
