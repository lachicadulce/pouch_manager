package org.androidtown.pouchmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PouchDetail extends AppCompatActivity implements OnMapReadyCallback {

    private EditText et_pouch_name;
    private EditText et_sender_receiver;
    private EditText et_memo;
    private Button bt_ok;
    private String pouch_name;
    private EditText et_status;
    private String sender, charge;
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pouch_detail);

        Intent intent1 = getIntent();
        pouch_name = intent1.getStringExtra("pouch_name");
        Log.d("sybaek", "파우치이름(pouch detail): "+pouch_name);
        et_pouch_name = (EditText) findViewById(R.id.et_pouch_name);
        et_sender_receiver = (EditText) findViewById(R.id.et_sender_receiver);
        et_memo = (EditText) findViewById(R.id.et_memo);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        et_pouch_name.setText(pouch_name);
        et_status = (EditText) findViewById(R.id.et_status);

        latitude = 37.2099949;
        longitude = 126.9750946;

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getPouchDetailFunc();

        et_pouch_name.setFocusable(false);
        et_memo.setFocusable(false);
        et_status.setFocusable(false);
        et_sender_receiver.setFocusable(false);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng CS = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(CS);
        markerOptions.title("발신자: "+sender);//발신자
        markerOptions.snippet("담당자: "+charge);//담당자
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CS));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    public void getPouchDetailFunc() {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("pouch_name", pouch_name);
        networkTask.execute(params);

        Log.d("sybaek", "listener");
    }

    public class NetworkTask extends AsyncTask<Map, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("ERROR")) {
            } else {
                try {
                    JSONArray array = new JSONArray(s);
                    JSONObject row = array.getJSONObject(0);

                    et_sender_receiver.setText("발신: "+row.getString("sender")+" 담당: "+row.getString("in_charge"));
                    et_memo.setText(row.getString("memo"));
                    et_status.setText("상태: "+row.getString("status"));
                    sender = row.getString("sender");
                    charge = row.getString("in_charge");
                    //ToDo latitude and longitude

                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
                    mapFragment.getMapAsync(PouchDetail.this);
                } catch (Exception e) {

                }
            }
        }


        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/pouch_detail");

            http.addAllParameters(maps[0]);

            HttpClient post = http.create();
            post.request();
            if (post.getHttpStatusCode() == 200) {
                String body = post.getBody();
                Log.d("sybaek", body);
                return body;
            } else {
                return "ERROR";
            }
        }


    }
}
