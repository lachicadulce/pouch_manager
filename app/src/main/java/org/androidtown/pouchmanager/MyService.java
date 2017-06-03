package org.androidtown.pouchmanager;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {
    private static final String TAG = "MemberGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000; // millisecond
    private static final float LOCATION_DISTANCE = 10f;

    private Boolean isUpdateContinuously;
    private IntentFilter mRequestBrIntentFilter;
    private RequestBroadcastReceiver mRequestBroadcastReceiver;
    private SharedPreferences loginPreferences;
    private String user_id;

    android.support.v4.app.NotificationCompat.Builder mNotificationBuilder;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
                {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            if (!isUpdateContinuously) {
                stopLocationRequest();
            }

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            NetworkTask networkTask1 = new NetworkTask();
            Map params = new HashMap();
            params.put("lat", latitude);
            params.put("lon", longitude);
            params.put("user_id", user_id);
            networkTask1.execute(params);

            Log.d("sybaek", "GPS Sent");

            Toast.makeText(getApplicationContext(), "위도: " + latitude + " / 경도: " + longitude, Toast.LENGTH_SHORT).show();
        }

        public class NetworkTask extends AsyncTask<Map, Integer, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("ERROR")) {
                            Toast.makeText(getApplicationContext(), "서버에 접속할 수 없습니다", Toast.LENGTH_LONG).show();
                        } else {
                        try {
                        Log.d("sybaek", new JSONObject(s).getBoolean("result") + "");
                        if (new JSONObject(s).getBoolean("result")) {
                            // true
                            Toast.makeText(getApplicationContext(),"gps가 전송됐습니다.",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }


            @Override
            protected String doInBackground(Map... maps) {

                HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/gps");

                http.addAllParameters(maps[0]);

                HttpClient post = http.create();
                post.request();

                    Log.d("sybaek", "doInBackground code: " + post.getHttpStatusCode());

                    if (post.getHttpStatusCode() == 200) {
                        String body = post.getBody();

                        Log.d("sybaek", body);
                    return body;
                } else {
                    return "ERROR";
                }
            }


        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        mRequestBrIntentFilter = new IntentFilter();
        mRequestBrIntentFilter.addAction("org.androidtown.pouchmanager.action.LOCATION_UPDATE_REQUEST");
        mRequestBroadcastReceiver = new RequestBroadcastReceiver();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        user_id = loginPreferences.getString("id", "");
        registerReceiver(mRequestBroadcastReceiver, mRequestBrIntentFilter);

        isUpdateContinuously = false;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

/*
    public class BleScan {

        private BluetoothAdapter mBluetoothAdapter;
        private boolean mScanning;
        private Handler mHandler;

        public BleScan() {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        private static final long SCAN_PERIOD = 100000;

        private void scanLeDevice(final boolean enable) {
            if (enable) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

        private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "sybaekbleBleScan");
                    }
                });
            }
        };
    }
*/

    private void startLocationRequest() {
        Log.i(TAG, "start GPS update");

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    private void stopLocationRequest() {
        Log.i(TAG, "stop GPS update");

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        Intent mMainIntent = new Intent(this, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                this, 1, mMainIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        mNotificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setContentTitle("Pouch Manager")
                        .setContentIntent(mPendingIntent)
                        .setContentText("파우치 메니저가 실행중입니다")
                        .setOngoing(true);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.e(TAG, "onDestroy");
        stopLocationRequest();

        unregisterReceiver(mRequestBroadcastReceiver);
    }

    private class RequestBroadcastReceiver extends BroadcastReceiver {
        //private BleScan mBleScan;
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("request")) {
                case "start":
                    isUpdateContinuously = true;
                    startForeground(20714, mNotificationBuilder.build());
                    startLocationRequest();
                    //mBleScan.scanLeDevice(true);
                    break;
                case "stop":
                    isUpdateContinuously = false;
                    stopForeground(true);
                    stopLocationRequest();
                    break;
                case "current":
                    // 현재위치만 알고 싶을 경우, 위치 검색 1회 후
                    // isUpdateContinuously가 false 면 검색 멈춤
                    startLocationRequest();
                default:
                    break;
            }
        }
    }
}
