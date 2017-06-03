package org.androidtown.pouchmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SingletonUserInfo mSingletonUserInfo = SingletonUserInfo.getInstance();

    private String id, pw;
    private Button btnLogin;
    private EditText et_id;
    private EditText et_pw;
    private CheckBox ch_rem;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.editText_id);
        et_pw = (EditText) findViewById(R.id.editText_pw);
        btnLogin = (Button) findViewById(R.id.button_login);
        ch_rem = (CheckBox) findViewById(R.id.checkBox_remember_me);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();


        ch_rem.setChecked(true);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = et_id.getText().toString();
                pw = et_pw.getText().toString();

                if (ch_rem.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("id", id);
                    loginPrefsEditor.putString("pw", pw);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                loginFunc();
            }
        });
    }

    public void loginFunc() {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("id", id);
        params.put("password", pw);
        networkTask.execute(params);

        Log.d("sybaek", "listener");
    }

    public class NetworkTask extends AsyncTask <Map, Integer, String>{
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

                        // Add userId to singleton
                        mSingletonUserInfo.setUserId(id);

                        // Add userId to preference
                        loginPrefsEditor.putString("id", id);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // false
                        Log.d("sybaek", "Login fail");
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/login");

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
