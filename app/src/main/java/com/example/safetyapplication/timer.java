package com.example.safetyapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.safetyapplication.login.Safety.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.safetyapplication.Login.session_status;

public class timer extends AppCompatActivity {
    public int counter;
    Button button;
    TextView textView;
    String tag_json_obj = "json_obj_req";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String url = Server.URL + "timer.php";
    private static final String TAG = timer.class.getSimpleName();

    //Update database
    public static final String KEY_EMP_ID = "id";
    public static final String KEY_EMP_triger = "triger";
    public static final String URL_UPDATE_EMP = "http://saltransp.com/restapi/update_trigger.php";

    private String id;
    int success;
    SharedPreferences sharedpreferences;
    public final static String TAG_ID = "id";
    public long start_time = 60000; // waktu tunggu 1 menit
    private final long interval = 1000;
    private CountDownTimer countDownTimer2;
    private MediaPlayer mp;
    ProgressDialog pDialog;

    private boolean  isCanceled = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra(TAG_ID);

        final TextView counttime=findViewById(R.id.textView);
        final TextView keterangan=findViewById(R.id.textView_keterangan);
        mp =MediaPlayer.create(timer.this, R.raw.siren);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                updateNormal();
                isCanceled = true;
                checkLogin(id);
            }
        });

        new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isCanceled){
                    cancel();
                    mp.stop();
                    System.out.println("Tes Stop Media Player");
                }
                counttime.setText(String.valueOf(counter));
                mp.start();
                counter++;
            }
            @Override
            public void onFinish() {
                button.setEnabled(false);
                updateData();
                keterangan.setText("*Anda terdeteksi kecelakaan, tombol ini tidak dapat ditekan lagi");
                counttime.setText("Kondisi Darurat!!.. Share Location");

                countDownTimer2 = new MyCountDownTimer(start_time,interval);
                countDownTimer2.start();

            }
        }.start();
    }
    private void releaseMediaPlayer() {
        try {
            if (mp != null) {
                if (mp.isPlaying())
                    mp.stop();
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long start_time, long interval){
            super(start_time,interval);
        }
        final TextView tunggu = findViewById(R.id.textView1);

        @Override
        public void onTick(long milisUntilFinished){
            tunggu.setText("Dalam 60 detik data kecelakaan di database diganti normal");
            releaseMediaPlayer();
            //tunggu.setText(counter);
            counter++;
        }
        @Override
        public void onFinish(){
            updateNormal();
            checkLogin(id);
        }
    }

    public void updateData() {
        final String triger = "1";
        final String kondisi = "kecelakaan";

        class UpdateData extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(timer.this, "Updating...", "Wait...", false, false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(timer.this, s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(timer.KEY_EMP_ID,id);
                hashMap.put(timer.KEY_EMP_triger,triger);
                hashMap.put(MapsActivity.KEY_EMP_kondisi,kondisi);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(timer.URL_UPDATE_EMP,hashMap);
                return s;
            }
        }
        UpdateData ue = new UpdateData();
        ue.execute();
    }

    public void updateNormal() {
        final String triger = "0";
        final String kondisi = "normal";

        class UpdateData extends AsyncTask<Void, Void, String> {
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
                hashMap.put(timer.KEY_EMP_ID,id);
                hashMap.put(timer.KEY_EMP_triger,triger);
                hashMap.put(MapsActivity.KEY_EMP_kondisi,kondisi);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(timer.URL_UPDATE_EMP,hashMap);
                return s;
            }
        }
        UpdateData ue = new UpdateData();
        ue.execute();
    }
    @Override
    public void onBackPressed() {
        isCanceled = true;
        finish();
    }

    private void checkLogin(final String id) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    // Check for error node in json
                    if (success == 1) {
                        String id = jObj.getString(TAG_ID);
                        Log.e("Successfully scan!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.commit();

                        // Memanggil main activity
                        Intent intent = new Intent(timer.this, MapsActivity.class);
                        intent.putExtra(TAG_ID, id);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
}