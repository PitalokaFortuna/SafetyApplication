        package com.example.safetyapplication;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentActivity;

        import android.Manifest;
        import android.annotation.TargetApi;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.location.Location;
        import android.media.MediaPlayer;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.content.SharedPreferences;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.text.DecimalFormat;
        import java.text.SimpleDateFormat;
        import java.util.Arrays;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Timer;

        import static java.lang.Thread.sleep;

        public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

        GoogleApiClient mGoogleApiClient;
        private GoogleMap mMap;
        private Marker mCurrLocationMarker;
        private Location mLastLocation;
        public static final String KEY_EMP_ID = "id";
        public static final String KEY_EMP_LAT = "lat";
        public static final String KEY_EMP_LONGG = "longg";
        public static final String KEY_EMP_kondisi = "kondisi";
        public static final String KEY_EMP_WAKTU = "waktu";
        public static final String KEY_EMP_XGYRO = "x_gyr";
        public static final String KEY_EMP_YGYRO = "y_gyr";
        public static final String KEY_EMP_ZGYRO = "z_gyr";
        public static final String KEY_EMP_XACCELERO = "x_accelero";
        public static final String KEY_EMP_YACCELERO = "y_accelero";
        public static final String KEY_EMP_ZACCELERO = "z_accelero";
        public static final String KEY_EMP_XFILTERGYRO = "x_filter_gyr";
        public static final String KEY_EMP_YFILTERGYRO = "y_filter_gyr";
        public static final String KEY_EMP_ZFILTERGYRO = "z_filter_gyr";
        public static final String KEY_EMP_XFILTERACC = "x_filter_accel";
        public static final String KEY_EMP_YFILTERACC = "y_filter_accel";
        public static final String KEY_EMP_ZFILTERACC = "z_filter_accel";

        //public static final String URL_UPDATE_EMP = "http://192.168.43.106/Android/update_data.php";
        //public static final String URL_INSERT_DATA = "http://192.168.43.106/Android/update_kondisi.php";

        public static final String URL_UPDATE_EMP = "http://saltransp.com/restapi/update_data.php";
        public static final String URL_INSERT_DATA = "http://saltransp.com/restapi/update_kondisi.php";
        public static final String URL_INSERT_KLASIFIKASI = "http://saltransp.com/restapi/update_kondisiLVQ.php";

        private String id, nama;
        SharedPreferences sharedpreferences;
        public final static String TAG_ID = "id";
        public final static String TAG_USERNAME = "nama";

        //UI Component
        TextView Text_X_Gyro, Text_Y_Gyro, Text_Z_Gyro;
        TextView Text_X_Acclero, Text_Y_Acclero, Text_Z_Acclero;
        TextView Text_Aktivitas, Text_Kecepatan;


        public static String klasifikasi = "";
        public static long waktu;
       /* public static float x_gyro;
        public static float y_gyro;
        public static float z_gyro;
            public static float x_acclero;
            public static float y_acclero;
            public static float z_acclero;*/


        double x_filter_acc;
        double y_filter_acc;
        double z_filter_acc;
        double x_filter_gyro;
        double y_filter_gyro;
        double z_filter_gyro;
        //Bolean(Penanda/ flagging untuk merekam file)
        boolean isRunning;

        private static float smoothed[] = new float[3];
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

        Button buttonStart;
        Button buttonStop;

        //For map
        LocationRequest mLocationRequest;

        LatLng latLng;
        GoogleMap mGoogleMap;
        SupportMapFragment mFragment;
        Marker mCurrLocation;
        double speed;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mFragment.getMapAsync(this);
            //mMap.setMyLocationEnabled(true);
            sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
            id = getIntent().getStringExtra(TAG_ID);
            nama = getIntent().getStringExtra(TAG_USERNAME);

            isRunning = false;
            timer = new Timer();

            time_string = new String();
            dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            gyro_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            acclero_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            Text_X_Gyro = (TextView) findViewById(R.id.text_x_gyro);
            Text_Y_Gyro = (TextView) findViewById(R.id.text_y_gyro);
            Text_Z_Gyro = (TextView) findViewById(R.id.text_z_gyro);

            Text_X_Acclero = (TextView) findViewById(R.id.text_x_acclero);
            Text_Y_Acclero = (TextView) findViewById(R.id.text_y_acclero);
            Text_Z_Acclero = (TextView) findViewById(R.id.text_z_acclero);

            Text_Aktivitas = (TextView) findViewById(R.id.text_aktivitas);
            Text_Kecepatan = (TextView) findViewById(R.id.text_kecepatan);

            Text_Aktivitas.setText("Klasifikasi: ");
            klasifikasi = "";

            buttonStart = (Button) findViewById(R.id.button_start);
            buttonStop = (Button) findViewById(R.id.button_stop);

            buttonStart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    sensorManager.registerListener(gyroListener, gyro_sensor, SensorManager.SENSOR_DELAY_FASTEST);
                    sensorManager.registerListener(accleroListener, acclero_sensor, SensorManager.SENSOR_DELAY_FASTEST);

                    Log.d(TAG, "Writing to " + getStorageDir());
                    try {
                        writer = new FileWriter(new File(getStorageDir(), "sensors_" + System.currentTimeMillis() + ".csv"));
                        writer.write(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s\n ",
                                "Waktu", "x_gyro", "y_gyro", "z_gyro", "x_acclero", "y_acclero", "z_acclero", "x_filtergyro", "y_filtergyro", "z_filtergyro", "x_filteraccl", "y_filteraccl", "z_filteraccl", "kondisi"));

                        isRunning = true;
                        new MyThread("wew");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            buttonStop.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    buttonStart.setEnabled(true);
                    buttonStop.setEnabled(false);
                    isRunning = false;
                    sensorManager.flush(gyroListener);
                    sensorManager.flush(accleroListener);
                    sensorManager.unregisterListener(gyroListener);
                    sensorManager.unregisterListener(accleroListener);

                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

        }

        //Mulai Menjalankan Algoritma
        public static class euclidean {
            public static double distance(double x[], double y[]) {

                double ds = 0.0;
                for (int n = 0; n < x.length; n++)
                    ds += Math.pow(x[n] - y[n], 2.0);

                ds = Math.sqrt(ds);
                return ds;
            }

            public static double[] distance(double x[], double y[][]) {

                double ds[] = new double[y.length];
                for (int n = 0; n < y.length; n++)
                    ds[n] = distance(y[n], x);
                return ds;
            }
        }
// Kelas untuk menghitung jarak euclidean distance dari masing-masing data masukan
        public static void hitung(double array2[], double aix2[][]) {
            double target[] = {0,3,1,2,3,0,0,1,1,3,3,1,2,0,2,0,1,2,0,3,3,3,1,1,3,1,2,0,2,1,2,3,1,2,2,2,1,2,2,3,2,2,2,0,0,0,3,2,1,0,1,0,1,0,0,1,2,2,3,0,3,2,0,2,2,2,3,1,2,0,1,0,2,3,0,1,2,2,3,0,3,2,1,3,1,3,0,0,1,1};
            klasifikasi = "";
            double ds5[] = euclidean.distance(array2,aix2);
            double indexTerkecil = ds5[0];
            System.out.println("Nilai Smoothed Sensor: " +Arrays.toString(array2));
            System.out.println("Jarak ke tiap codebook : " +Arrays.toString(ds5));

            int indeks = 0;
            for (int n = 1; n < ds5.length; n++) {
                if (indexTerkecil > ds5[n]) {
                    indexTerkecil = ds5[n];
                    indeks = n;
                }
            }

            double output = target[indeks];

            if (output == 0) {
                klasifikasi = "berhenti";
            } else if (output == 1) {
                klasifikasi = "kanan";
            } else if (output == 2) {
                klasifikasi = "kiri";
            } else if (output == 3) {
                klasifikasi = "lurus";
            }
        }

        private String getStorageDir() {

            return this.getExternalFilesDir(null).getAbsolutePath();
        }

        @Override
        protected void onResume() {
            super.onResume();
            sensorManager.registerListener(gyroListener, gyro_sensor,SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(accleroListener, acclero_sensor,SensorManager.SENSOR_DELAY_FASTEST);
        }

        public SensorEventListener gyroListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int acc) {
            }
            public void onSensorChanged(SensorEvent event) {
                smoothed = LowPassGyro.filter(event.values, data);
                data[0] = smoothed[0];
                data[1] = smoothed[1];
                data[2] = smoothed[2];

                x_gyro = event.values[0];
                y_gyro = event.values[1];
                z_gyro = event.values[2];

                dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                time_string = dateFormat.format(new Date());

                Text_X_Gyro.setText("X : " + (int) x_gyro + " rad/s");
                Text_Y_Gyro.setText("Y : " + (int) y_gyro + " rad/s");
                Text_Z_Gyro.setText("Z : " + (int) z_gyro + " rad/s");

                return;
            }
        };

        public SensorEventListener accleroListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                smoothed = LowPass.filter(event.values, grav);
                grav[0] = smoothed[0];
                grav[1] = smoothed[1];
                grav[2] = smoothed[2];

                x_acclero = event.values[0];
                y_acclero = event.values[1];
                z_acclero = event.values[2];

                //waktu dan tanggal record
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                time_string = dateFormat.format(new Date());

                Text_X_Acclero.setText("X : " + (int) x_acclero + " m/s2");
                Text_Y_Acclero.setText("Y : " + (int) y_acclero + " m/s2");
                Text_Z_Acclero.setText("Z : " + (int) z_acclero + " m/s2");

                if(x_acclero>4.2 && y_gyro<-1 && z_gyro<-2.5 || x_acclero<-4 && y_gyro>1 && z_gyro>1.5){
                    klasifikasi="Kecelakaan";

                    isRunning = false;
                    sensorManager.flush(gyroListener);
                    sensorManager.flush(accleroListener);
                    sensorManager.unregisterListener(gyroListener);
                    sensorManager.unregisterListener(accleroListener);
                    InsertData();
                    InsertKlasifikasi();
                }

                Text_Aktivitas.setText("Klasifikasi: "+klasifikasi);
                return;
            }

            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Method untuk menjalankan method hitung dan method yang menyimpan data training
        public void klasifikasiLVQ() {
            double aix2[][] = {{0.03576168224599301,-0.01934412390223578,0.03515095282039576,0.03231187872702217,-0.0001836898035861596,0.10013735338962056},
                    {-0.0429017072866451,0.041317631488409355,0.007286107862198835,0.08671619264479041,0.008781006829270115,1.0022971042173012},
                    {0.004676962557405894,-0.056830688078179856,0.01872233719062672,0.34044130201645495,1.0866545235465679,-0.47747005870783593},
                    {0.14633560040609683,0.2749897093844378,0.6095452660305714,-0.1947161736313017,-0.14179143989980078,0.15594273588264135},
                    {-0.14554849853172377,-0.1369819114109441,0.15167511191475466,-0.1981582263402049,-0.7860705520005683,0.9305777737121885},
                    {-0.01596460103583733,-0.007779994062697355,-0.016256204210517235,0.14225100128288168,-0.10805356345239378,0.023068665936145722},
                    {0.012686233820173814,0.016150223273947217,0.005017861604820266,-0.06212986288913426,-0.09084647591193194,0.12467263570336636},
                    {0.0616868941628495,-0.10182019720178644,-0.7564841644795726,1.0026786201172604,0.11822689994091888,0.9745363635789993},
                    {0.40731272259192036,-0.5212092301792528,-0.6681135100630661,-0.30088122318590316,-0.9464521010115536,1.0369542659556215},
                    {0.023605900896855478,0.08786919851222474,0.06757537012582882,0.2722465347951867,-0.05476214322603479,-0.37703384500548054},
                    {0.01695682960316199,0.05392775694428295,-0.12311342627798831,-0.038898869068368834,-0.3652828039614603,0.3140629353082062},
                    {0.04963659416796811,-0.08380338461838198,-0.666945483134305,0.9412115022936574,0.6436120309949396,-0.331097141286085},
                    {0.3734668943286545,0.3957847454102787,0.8872354205568773,-0.6919488121987646,-0.7591205971938485,1.5893468567856144},
                    {-0.013309933308176756,0.006232732644122703,-0.015363852032577223,-0.0406156292382726,0.5099799258298873,-0.11549941979263209},
                    {0.18442172119092165,0.27802614698108474,0.7816647559125421,-0.1635963386762243,-0.750397269258247,0.5597081827250617},
                    {0.013332935893912847,-0.010623150893131197,-0.024132663962052985,-0.13746605817034654,0.14527336282670783,0.433774298807424},
                    {0.28719876667843974,-0.3791783472133076,-0.3318071254839623,0.030314292510384682,-0.44164269426599023,0.4086037616009409},
                    {0.14513681928371638,0.08490531089228544,1.0095785947996152,-1.4862825879055497,-1.511547201263398,1.4443429359810833},
                    {0.012498108402508323,-0.003177879327677277,-0.0005500445884238218,0.004745571485792278,0.08335970809715874,0.1267039993032748},
                    {0.04147466036971199,0.018841393483752786,-0.1896354589614687,-0.07492702550168859,-0.28968052520009724,0.25129419085564353},
                    {-0.31540063924193357,-0.15124059152175962,-0.07970033727786922,-0.1546798576039257,-0.6219625536360079,1.6243648826957142},
                    {0.020117743067177313,0.024207054597351074,-0.03446522621568078,0.12094713736585792,0.48417648312553585,-0.579600565965934},
                    {0.04963659416796811,-0.08380338461838198,-0.666945483134305,0.9412115022936574,0.6436120309949396,-0.331097141286085},
                    {0.10882300736154701,-0.2735027994467513,-0.8196024998803937,0.215027726881863,-0.43310288312646844,0.20800877060488465},
                    {-0.003178422632473072,-0.06022287641468162,0.01168309639357905,0.1215337966466689,0.22497027385137217,-0.3064866973667499},
                    {0.23734101325677853,-0.348337714602551,-0.7745015165729613,1.0163538711518596,-1.3935024626403951,0.4518656429726643},
                    {-0.0227,-0.1174,0.3691,-0.6044,-1.5173,1.7837},
                    {-0.04114498906251434,-0.030366262275385383,-0.07404018789919828,0.18421418880598053,-0.09361871848686963,-0.13296302636582435},
                    {0.06583462740565178,0.14130868564416263,-0.09502341654890424,-0.1995646265093203,-1.1000673617116745,0.829617474433408},
                    {0.2737234006473681,-0.36250043501088003,-0.5155346370096485,0.2820211700688525,-0.5591624702243138,1.120594433387837},
                    {0.025129010067586676,-0.05046781948336462,-0.19692471746136042,-0.5259654856678462,0.5326529023850791,-0.1804338139881409},
                    {-0.0682883952086375,0.051995577010775194,-0.1517982687968591,-0.07124733687840584,-0.30561820559933606,-0.057650188187805165},
                    {0.2066121078339558,-0.23954090502193445,-0.7706861312458759,1.0259223455609703,-0.8970102441483941,0.49097397186166186},
                    {0.17248579322132881,0.24257049431143227,0.8141080127683404,-0.3615666955065915,0.03022030499530195,0.9134622537968732},
                    {0.11988,0.23247,-0.03699,-0.25842,-1.85117,1.10185},
                    {0.192112742484992,0.2506475575356627,0.7353462405216336,-0.8273238987168962,-0.8709332315274306,1.2036687506874666},
                    {0.2156810740893223,-0.28458408535826374,-0.24664268218042423,-0.017992936040323863,0.5766228186242445,1.1854923806491136},
                    {0.20545828209421846,0.25921954324112956,0.7043314185863447,-0.6546724174212046,-0.468033395972749,0.6642659500036852},
                    {0.04924027514741374,0.054313218989602105,0.06437326871492033,-0.17025862566747205,1.3433752423082141,-0.5768110504785326},
                    {0.01695682960316199,0.05392775694428295,-0.12311342627798831,-0.038898869068368834,-0.3652828039614603,0.3140629353082062},
                    {0.15656494655028402,0.09824786993620603,0.8180333656628654,-0.8956146331517254,-0.9793218059508297,0.6179172096899588},
                    {0.12649331939155478,0.2957387684445289,0.28439107005518904,-0.34552778319409494,-1.8116735590177182,1.5668571604897903},
                    {0.02959351283057818,0.0025942707933550003,0.31941367183455915,-0.30821786201100587,0.21322591120796744,-0.20702309048489786},
                    {-0.009316424966767888,-0.016701903600084483,0.007054280926648873,-0.08889259429439055,0.04407286328137917,0.15902794915514398},
                    {-0.01596460103583733,-0.007779994062697355,-0.016256204210517235,0.14225100128288168,-0.10805356345239378,0.023068665936145722},
                    {-0.10613979838489998,-0.14629033232995928,0.12999000749412226,-0.014125983883333686,-0.09027872776463965,0.2775352712703651},
                    {0.0063039956073538544,-0.05093186336431298,0.0009091243821013258,-0.10718644486026017,0.6211932923507499,0.2850978056458853},
                    {0.038390010602066815,-0.037552388339195004,0.6595702966381369,-0.8356062459589843,-0.019616609956322913,0.5442874154717146},
                    {0.29856506047452563,-0.3516075497326032,-0.5697238611285202,1.2745085037451513,-2.941398786276424,1.536251892303229},
                    {-0.03350204197914811,0.040126732312556854,-0.007655403552484808,-0.0879580515349225,0.24186535759195232,0.07085443175369058},
                    {0.02159479541296597,-0.06598240113666042,-0.6614735486181159,0.2734312203019017,0.12148049652215294,0.22947617187682262},
                    {0.0032983949806165095,-0.001619310350109533,0.00780751829144721,0.068053289565937,-0.12331204644598144,0.16159915689242732},
                    {0.23734101325677853,-0.348337714602551,-0.7745015165729613,1.0163538711518596,-1.3935024626403951,0.4518656429726643},
                    {-0.04786740595273814,0.06805543577668252,-0.003512187670187261,-0.392864988470382,-0.2172828011232543,0.134669077984971},
                    {0.015470717624776907,-0.03077284646146442,0.057959928023252116,0.12025469942233673,0.1266583202650571,-0.12019852141210148},
                    {0.19139773963317383,-0.22343697369478385,-1.0872570447696202,1.3290932539987774,-0.7443164024285855,1.221514437382796},
                    {0.0913455778371597,0.10598490097756097,0.6082053282864249,-0.9095594398654165,-0.8260268017338687,0.3991096396313997},
                    {0.07510435456630593,0.1114196521236405,0.11146942018634026,-0.32549847649096453,0.11722454410655424,-0.8917151388997646},
                    {-0.08332677320111086,-0.04738014516540443,0.03263865680952953,0.20592141586641452,-0.4472126818748888,0.7051342143215246},
                    {0.03507254353334887,0.057617647428328436,-0.02216545239633802,0.05528620490413784,0.15476880222807413,0.1266398950604936},
                    {0.0716039831973558,-0.07600795947014347,0.0024308745440767064,0.49633940485121103,1.6150744212285213,0.38009907677175847},
                    {0.0922,-0.134,0.5634,-1.275,-1.2285,0.283},
                    {-0.07896903604341823,0.12378906773983897,-0.04358395515379542,-0.2315163806429933,-0.420555712802303,0.1371069972644025},
                    {0.2976062999981687,0.3791660275227738,0.4488690314640073,-0.1067430072508358,-0.5706419645007265,1.1384607142657783},
                    {0.1893498954020336,0.22538587563911086,0.9163346234963962,-1.2005722825446603,-0.46871914430935235,1.709720708461354},
                    {0.15928705441543606,0.13985541302847992,0.6705973886090094,-1.15751771483318,-0.39830053953705225,0.27049919318634297},
                    {-0.07481730645635434,-0.08300179504271797,-0.19655539916248152,-0.07645244439302265,-0.10375294006131142,-0.23319172708533445},
                    {0.13588337917196133,-0.11247970639915604,-0.7301957136747268,0.5382782344374992,0.3396150297666884,0.5182801601820942},
                    {0.239722780306311,0.3345604501826776,0.5472708448673658,0.08584678253681587,0.4949337182093519,-0.5081990309108793},
                    {-0.230221357132918,-0.01097319253207174,0.05689828414351784,0.17108028448842766,0.6737416024469675,0.19128745761196356},
                    {0.06261369663949967,-0.3790095942256647,-0.8588953912969735,0.25748449126182593,-0.729012873695327,-0.1629011806772331},
                    {0.002325557610086391,-0.023835010795776417,0.01885572386827657,0.07915093994330759,-0.21789732344095714,0.3184360976748467},
                    {0.0837171338948885,0.09515789182270058,-0.11328564376305929,0.008498130383954418,0.39457052108235935,0.47376975658592274},
                    {-0.13139036320094946,0.07889651803812683,-0.1119217404480247,0.1052390363360988,0.3594560370792222,0.3210753725048066},
                    {-0.230221357132918,-0.01097319253207174,0.05689828414351784,0.17108028448842766,0.6737416024469675,0.19128745761196356},
                    {0.28613054466367743,-0.3666649138101271,-0.7752664356407092,0.7188552273932605,-0.3609446731305732,0.8888179954810113},
                    {0.0922,-0.134,0.5634,-1.275,-1.2285,0.283},
                    {0.08556814621826228,0.18249028746551146,-0.13349609115339506,0.0390916244739613,0.35274190815624745,0.39150969117127404},
                    {-0.01073863084670383,0.037174215863274344,-0.033011544110835435,-0.013981973163070137,0.7246406355578776,-0.2611490188931932},
                    {-0.15523389863088785,0.06953427703467839,-0.01737530474443496,-0.03865677533064308,0.3300593220651761,-0.12087776617606705},
                    {-0.02554769202321001,0.016520336994502085,0.03348885354379142,-0.1287440642815214,0.0005447351363772224,0.5413767483434282},
                    {0.12649331939155478,0.2957387684445289,0.28439107005518904,-0.34552778319409494,-1.8116735590177182,1.5668571604897903},
                    {0.04409118925003596,-0.04965122729170805,-0.45232660585613477,0.7310910070920583,0.043726802983993195,-0.18407967803419265},
                    {-0.01758942169497465,0.016382534536626973,0.07619100294158869,0.058214558255529944,0.39647757065713013,-0.08716444139887068},
                    {0.22889957981393028,-0.3896100243913976,-0.6073461973901914,-0.38031155624507296,-0.0529141151492062,0.21052958237246425},
                    {-0.03440164563556968,0.039628853907782355,0.03737090366069794,0.03237962494273981,-0.06220240086779253,0.05562938023947818},
                    {0.05126791266699879,0.01053568377743407,-0.010957325106496035,-0.03058075155661432,-0.16151450016407917,0.1922123580108615},
                    {-0.016813687218708125,0.03194785539975383,0.004240775902526935,0.10129932233134634,-0.1296721446273546,0.05382777782341246},
                    {0.29856506047452563,-0.3516075497326032,-0.5697238611285202,1.2745085037451513,-2.941398786276424,1.536251892303229},
                    {0.2888057403436568,-0.35149736921776886,-0.893469397031843,0.9699082272326058,-0.9823959224447018,2.0720659074717904},
            };

            double array2[] = new double[6];
            array2[0] = data[0];
            array2[1] = data[1];
            array2[2] = data[2];
            array2[3] = grav[0];
            array2[4] = grav[1];
            array2[5] = grav[2];

            double raw[] = new double[6];
            raw[0] = x_gyro;
            raw[1] = y_gyro;
            raw[2] = z_gyro;
            raw[3] = x_acclero;
            raw[4] = y_acclero;
            raw[5] = z_acclero;

            //System.out.println("Nilai Sensor : " +Arrays.toString(raw) );

            if(x_acclero>4.2 && y_gyro<-1 && z_gyro<-2.5 || x_acclero<-4 && y_gyro>1 && z_gyro>1.5){
                klasifikasi="Kecelakaan";
                System.out.println("Nilai Sensor : " +Arrays.toString(raw) );
                System.out.println("Hasil : " +klasifikasi );
            }

            else {
                hitung(array2, aix2);
                System.out.println("Hasil : " +klasifikasi );
            }
        };

    class MyThread implements Runnable {
        String name;
        Thread t;

        MyThread(String thread) {
            name = thread;
            t = new Thread(this, name);
            t.start();
        }

        public void run() {
            saveData();
        }
    }
        void saveData() {
                while (isRunning) {
                    long millis = System.currentTimeMillis();
                    try {
                        long start = System.nanoTime();
                        double data[] = new double[6];
                        data[0] = x_gyro;
                        data[1] = y_gyro;
                        data[2] = z_gyro;
                        data[3] = x_acclero;
                        data[4] = y_acclero;
                        data[5] = z_acclero;

                        double filter[] = new double[6];
                        filter[0] = x_filter_gyro;
                        filter[1] = y_filter_gyro;
                        filter[2] = z_filter_gyro;
                        filter[3] = x_filter_acc;
                        filter[4] = y_filter_acc;
                        filter[5] = z_filter_acc;
                        prev(filter, data);

                        double array2[] = new double[6];
                        array2[0] = filter[0];
                        array2[1] = filter[1];
                        array2[2] = filter[2];
                        array2[3] = filter[3];
                        array2[4] = filter[4];
                        array2[5] = filter[5];

                        if(x_acclero>4.2 && y_gyro<-1 && z_gyro<-2.5 || x_acclero<-4 && y_gyro>1 && z_gyro>1.5){
                            klasifikasi="Kecelakaan";
                        }

                        klasifikasiLVQ();
                        long end = System.nanoTime();
                        waktu = (end - start)/1000000000;
                        InsertData();
                        InsertKlasifikasi();

                        //outlier jika lebih dari ini maka kecelakaan. jika kurang dari ini akan masuk filter

                        writer.write(String.format("%s, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %s \n ",
                                time_string, x_gyro, y_gyro, z_gyro, x_acclero, y_acclero, z_acclero, filter[0], filter[1], filter[2], filter[3], filter[4], filter[5], klasifikasi));
                        Thread.sleep(300 - millis % 300);

                        if(klasifikasi == "Kecelakaan"){
                            isRunning = false;
                            sensorManager.flush(gyroListener);
                            sensorManager.flush(accleroListener);
                            sensorManager.unregisterListener(gyroListener);
                            sensorManager.unregisterListener(accleroListener);
                            writer.close();

                            updateData();
                            Intent intent = new Intent(MapsActivity.this, timer.class);
                            intent.putExtra(TAG_ID, id);
                            finish();
                            startActivity(intent);
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // INI RUMUS FILTER
            private double[] prev(double[] filter, double[] data) {
                if (data == null || filter == null)
                    throw new NullPointerException("input and prev float arrays must be non-NULL");
                if (data.length != filter.length)
                    throw new IllegalArgumentException("input and prev must be the same length");

                //System.out.println("Data : " +Arrays.toString(data));

                for (int i = 0; i < data.length; i++) {
                    filter[i] = filter[i] + 0.1 * (data[i] - filter[i]);
                    //System.out.println("Nilai Smoothed Sensor: " +Arrays.toString(filter));
                    //Log.d("Debug", " Smoothed Sensor - " + Integer.toString(i) + " : " + Double.toString(filter[i]));
                }
                return filter;
            }

            public void InsertKlasifikasi() {
                final String kondisi = String.valueOf(klasifikasi);
                final String waktuu = String.valueOf(waktu);
                final String x_gyr = String.valueOf(x_gyro);
                final String y_gyr = String.valueOf(y_gyro);
                final String z_gyr = String.valueOf(z_gyro);
                final String x_accelero = String.valueOf(x_acclero);
                final String y_accelero = String.valueOf(y_acclero);
                final String z_accelero = String.valueOf(z_acclero);
                final String x_filter_gyr = String.valueOf(data[0]);
                final String y_filter_gyr = String.valueOf(data[1]);
                final String z_filter_gyr = String.valueOf(data[1]);
                final String x_filter_accel = String.valueOf(grav[0]);
                final String y_filter_accel = String.valueOf(grav[1]);
                final String z_filter_accel = String.valueOf(grav[2]);

                class UpdateData extends AsyncTask<Void, Void, String> {
                    ProgressDialog loading;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //loading = ProgressDialog.show(MapsActivity.this, "Updating...", "Wait...", false, false);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        //loading.dismiss();
                        //Toast.makeText(MapsActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(MapsActivity.KEY_EMP_ID,id);
                        hashMap.put(MapsActivity.KEY_EMP_kondisi,kondisi);
                        hashMap.put(MapsActivity.KEY_EMP_WAKTU, waktuu);
                        hashMap.put(MapsActivity.KEY_EMP_XGYRO, x_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_YGYRO, y_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_ZGYRO, z_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_XACCELERO, x_accelero);
                        hashMap.put(MapsActivity.KEY_EMP_YACCELERO, y_accelero);
                        hashMap.put(MapsActivity.KEY_EMP_ZACCELERO, z_accelero);
                        hashMap.put(MapsActivity.KEY_EMP_XFILTERGYRO, x_filter_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_YFILTERGYRO, y_filter_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_ZFILTERGYRO, z_filter_gyr);
                        hashMap.put(MapsActivity.KEY_EMP_XFILTERACC, x_filter_accel);
                        hashMap.put(MapsActivity.KEY_EMP_YFILTERACC, y_filter_accel);
                        hashMap.put(MapsActivity.KEY_EMP_ZFILTERACC, z_filter_accel);

                        RequestHandler rh = new RequestHandler();

                        String s = rh.sendPostRequest(MapsActivity.URL_INSERT_KLASIFIKASI,hashMap);
                        return s;
                    }
                }
                UpdateData ue = new UpdateData();
                ue.execute();
            }
            public void updateData() {
                final String lat = String.valueOf(mLastLocation.getLatitude());
                final String longg = String.valueOf(mLastLocation.getLongitude());

                class UpdateData extends AsyncTask<Void, Void, String> {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(MapsActivity.KEY_EMP_ID,id);
                        hashMap.put(MapsActivity.KEY_EMP_LONGG,longg);
                        hashMap.put(MapsActivity.KEY_EMP_LAT,lat);
                        RequestHandler rh = new RequestHandler();

                        String s = rh.sendPostRequest(MapsActivity.URL_UPDATE_EMP,hashMap);

                        return s;
                    }
                }
                UpdateData ue = new UpdateData();
                ue.execute();
            }

            public void InsertData() {
                final String kondisi = String.valueOf(klasifikasi);
                final String waktuu = String.valueOf(waktu);
                class InsertData extends AsyncTask<Void, Void, String> {
                    ProgressDialog loading;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(MapsActivity.KEY_EMP_ID,id);
                        hashMap.put(MapsActivity.KEY_EMP_kondisi,kondisi);
                        hashMap.put(MapsActivity.KEY_EMP_WAKTU, waktuu);
                        RequestHandler rh = new RequestHandler();

                        String s = rh.sendPostRequest(MapsActivity.URL_INSERT_DATA,hashMap);

                        return s;
                    }
                }
                InsertData ue = new InsertData();
                ue.execute();
            }

            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera. In this case,
             * we just add a marker near Sydney, Australia.
             * If Google Play services is not installed on the device, the user will be prompted to install
             * it inside the SupportMapFragment. This method will only be triggered once the user has
             * installed Google Play services and returned to the app.
             */
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mGoogleMap = googleMap;
                mGoogleMap.setMyLocationEnabled(true);
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }

            @Override
            public void onPause() {
                super.onPause();
                sensorManager.unregisterListener(gyroListener);
                sensorManager.unregisterListener(accleroListener);

                //Unregister for location callbacks:
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }
            }

            protected synchronized void buildGoogleApiClient() {
                Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

            @Override
            public void onConnected(Bundle bundle) {
                Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    //place marker at current position
                    mGoogleMap.clear();
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).zoom(14).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    // create markerOptions
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(
                            mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    // ROSE color icon
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    markerOptions.position(latLng);
                    // adding markerOptions
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    //dropPinEffect(marker);
                }

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10);
                mLocationRequest.setFastestInterval(10);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                //remove previous current location marker and add new one at current position
                if (mCurrLocation != null) {
                    mCurrLocation.remove();
                }
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocation = mGoogleMap.addMarker(markerOptions);

            }


            @Override
            public void onBackPressed() {
                new AlertDialog.Builder(this).setTitle("Keluar")
                        .setMessage("Keluar aplikasi ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                finish();
                            }
                        }).setNegativeButton("Tidak", null).show();
            }
        }