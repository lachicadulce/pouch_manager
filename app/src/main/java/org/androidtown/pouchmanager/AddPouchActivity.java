package org.androidtown.pouchmanager;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPouchActivity extends AppCompatActivity {

    private EditText et_dline, et_memo, et_pouch_name;
    private AutoCompleteTextView et_recei;
    private Button bt_add;

    private String test;

    private String[] receivers = { "14050001", "14050020", "14050032", "11050038"};//받아오도록 수정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_pouch);

        // 서버로 받는 애 시작
        // 서버로부터 받으면 거기서 receivers를 초기화
        // 그리고 어댑터의 notify

        et_recei = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_receiver);
        et_memo = (EditText) findViewById(R.id.editText_memo);
        et_dline = (EditText) findViewById(R.id.editText_deadline);
        et_pouch_name = (EditText) findViewById(R.id.editText_pouch_name);
        bt_add = (Button) findViewById(R.id.button_add);

        et_recei.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, receivers));

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_add.getText().toString() == "취소") {
                    Toast.makeText(getApplicationContext(), "등록이 취소됐습니다", Toast.LENGTH_SHORT).show();
                } else {
                    regesterNewItem();
                }
                finish();
            }
        });
        getEmptyPouchFunc();
    }

    public void regesterNewItem() {
        NetworkTask networkTask = new NetworkTask();
        test = "register";
        Map params = new HashMap();
        params.put("id", "14050032");
        params.put("receiver", et_recei.getText().toString());
        params.put("deadline", et_dline.getText().toString());
        params.put("memo", et_memo.getText().toString());
        params.put("pouch_name", et_pouch_name.getText().toString());
        networkTask.execute(params);

        Log.d("sybaek", et_recei.getText().toString());
    }

    public void getEmptyPouchFunc() {
        NetworkTask networkTask = new NetworkTask();
    test = "empty_pouch";
        Map params = new HashMap();
        params.put("id", "14050032");
        //params.put("id", user_id);
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
                Log.d("see", "error");
                et_pouch_name.setText("잠시후 시도해주세요");
                bt_add.setText("취소");
            } else {
                try {
                    if(test=="empty_pouch"){
                    JSONArray array = new JSONArray(s);

                        Log.d("sybaek", "lenght: "+array.length());
                        if (array.length()!=0){
                            JSONObject row = array.getJSONObject(0);
                            et_pouch_name.setText(row.getString("pouch_name"));
                        }
                        else {
                            bt_add.setText("취소");
                            et_pouch_name.setText("빈 파우치가 없습니다");
                        }
                    }

                    Log.d("sybaek",test);
                    test="";
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            Log.d("sybaek", "끝끝");
        }


        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/"+test);

            http.addAllParameters(maps[0]);

            HttpClient post = http.create();
            post.request();


            if (post.getHttpStatusCode() == 200) {
                String body = post.getBody();
                Log.d("sybaek", body);
                return body;
            } else {

                Log.d("see", Integer.toString(post.getHttpStatusCode()));
                return "ERROR";
            }
        }


    }

}
