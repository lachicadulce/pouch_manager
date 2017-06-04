package org.androidtown.pouchmanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;
    private final int MY_PERMISSIONS_REQUEST = 100;
    private boolean isGpsStarted;
    private int mCurrentPage;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    static private String user_id;

    // 각 프래그먼트별 아이템들
    public ArrayList<ListViewItem> mPouchListItems;
    public ArrayList<ListViewItem> mMyPouchListItems;
    public ArrayList<ListViewItem> mMyPouchChargedItems;
    public ArrayList<ListViewItem> mMyPageItems;

    private PouchList mPouchListFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        mPouchListItems = new ArrayList<>();
        mMyPouchListItems = new ArrayList<>();
        mMyPouchChargedItems = new ArrayList<>();
        mMyPageItems = new ArrayList<>();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        user_id = loginPreferences.getString("id", "");

        // 액티비티가 종료되더라도 SharedPreference 에 저장된 GPS 검색 상태 불러오기
        // 해당 키(isGpsStarted)가 없으면 기본값으로 false 반환 (보통 첫 실행 시)
        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        isGpsStarted = pref.getBoolean("isGpsStarted", false);

        PouchPagerAdapter mPouchPagerAdapter = new PouchPagerAdapter(
                getSupportFragmentManager()
        );

        // 객체가 앱에서 하나뿐인(single instance) PouchList 프래그먼트를 가져옴
        mPouchListFrag = (PouchList) mPouchPagerAdapter.getFragment(Constants.FRAGMENTS.POUCH_LIST);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPouchPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Awesometic", "page\tmCurrent: " + mCurrentPage + "\tcurrent: " + position);

                // 페이지가 변할 때
                if (mCurrentPage != position) {
                    mCurrentPage = position;
                    setGpsService();
                    // 전체 목록으로 돌아올 때
                    if (mCurrentPage == Constants.FRAGMENTS.POUCH_LIST) {
                        // 체크된 아이템들이 있으면 전부 체크 해제
                        for (ListViewItem item : mPouchListItems) {
                            if (item.getChecked())
                                item.setChecked(false);
                        }

                        mPouchListFrag.notifyDataSetChagnedFromActivity();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCurrentPage = 0;

        TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 플로팅 버튼을 누를 때
                if (mCurrentPage == Constants.FRAGMENTS.POUCH_LIST) {
                    // 현재 페이지가 전체 목록이라면 체크된 아이템들을 걸러냄
                    ArrayList<ListViewItem> checkedItems = new ArrayList<ListViewItem>();
                    for (ListViewItem item : mPouchListItems) {
                        if (item.getChecked())
                            checkedItems.add(item);
                    }

                    if (checkedItems.size() > 0) {
                        // 체크된 아이템들이 있을 때
                        Toast.makeText(getApplicationContext(), "Checked item existed, count: " + checkedItems.size(), Toast.LENGTH_SHORT).show();
                        for (int index = 0; index < checkedItems.size(); index++){
                            mSendChargeList(checkedItems.get(index).getTitle().toString());
                        }
                    } else {
                        // 체크된 아이템이 없을 때
                        startActivity(new Intent(MainActivity.this, AddPouchActivity.class));
                    }
                } else {
                    // 현재 페이지가 전체 목록이 아닐 때
                    startActivity(new Intent(MainActivity.this, AddPouchActivity.class));
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 버전이 M 이상일 경우
            int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            int bluetoothPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            int bluetoothAdminPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);

            if (coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED
                    || fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED || bluetoothPermissionCheck == PackageManager.PERMISSION_DENIED || bluetoothAdminPermissionCheck == PackageManager.PERMISSION_DENIED) {
                // 권한을 허락받지 않았을 때

                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH);
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                        MY_PERMISSIONS_REQUEST);
            }
        }


        // 버전이 M 보다 낮거나 이미 권한이 있는 경우
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (!isGpsStarted) {
            Toast.makeText(this, "로그아웃됐습니다", Toast.LENGTH_SHORT).show();
            loginPrefsEditor.putBoolean("saveLogin", false);
            loginPrefsEditor.putString("id", "");
            loginPrefsEditor.putString("pw", "");

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "담당목록이 있습니다", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    private void setGpsService() {
        int listLength = mMyPouchChargedItems.size();

        Log.d("sybaek", "담당목록길이: "+listLength);
        Intent sendIntent = new Intent("org.androidtown.pouchmanager.action.LOCATION_UPDATE_REQUEST");

        if (listLength == 0) {
            //if (isGpsStarted)
                sendIntent.putExtra("request", "stop");
            isGpsStarted = false;
        }else {
            //if (!isGpsStarted)
                sendIntent.putExtra("request", "start");
            isGpsStarted = true;
        }

        sendBroadcast(sendIntent);
    }

    private void mSendChargeList(String mpouch_name) {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("pouch_name", mpouch_name);
        params.put("id", user_id);
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
                Log.d("sybaek","여기 에러래");
            } else {
                try {
                    Toast.makeText(getApplicationContext(), "담당됐습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                }

            }
        }


        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/in_charge");

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

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 처음 생성되거나 재개될 경우 (액티비티 생명주기 참고)

        if (isGpsStarted) {
            // 위치 검색중
            // 새로 위치를 요청하지 않음
        } else {
            // 위치 검색중이 아닐 때
            // 새로 위치를 요청 (브로드캐스트)
            Intent sendIntent = new Intent("org.androidtown.pouchmanager.action.LOCATION_UPDATE_REQUEST");
            sendIntent.putExtra("request", "current");

            sendBroadcast(sendIntent);
            Toast.makeText(getApplicationContext(), "request current location at first time", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 액티비티가 종료될 경우
        // SharedPreference에 isGpsStarted 상태 저장
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isGpsStarted", isGpsStarted);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, MyService.class);
                    startService(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "권한이 없으면 앱을 사용하실 수 없습니다!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    // 새로 찾은 위치에 대한 브로드캐스트 리시버
    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            Location lastLocation = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);

        }
    }

}
