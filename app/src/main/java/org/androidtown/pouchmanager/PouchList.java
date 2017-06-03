package org.androidtown.pouchmanager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PouchList extends Fragment {

    private SingletonUserInfo mSingletonUserInfo = SingletonUserInfo.getInstance();

    private View mRootView;
    private ListView lvPouchList;
    private ListViewAdapter mAdapter;

    //private SharedPreferences loginPreferences;
    //private String user_id;

    public static PouchList newInstance() {

        Bundle args = new Bundle();

        PouchList fragment = new PouchList();
        fragment.setArguments(args);
        return fragment;
    }


    public PouchList() {
        // Required empty public constructor
        //loginPreferences = this.getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        //user_id = loginPreferences.getString("id", "");
    }

    public void getPouchListFunc() {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("code", "list");
        params.put("id", mSingletonUserInfo.getUserId());
        networkTask.execute(params);

        Log.d("sybaek", "listener");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = (View) inflater.inflate(R.layout.fragment_pouch_list, null);

        lvPouchList = (ListView) mRootView.findViewById(R.id.listview_item);

        return mRootView;
    }

    public void notifyDataSetChagnedFromActivity() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // MainActivity의 리스트 참조를 가져와 어댑터를 생성
        // 그러기 위해 프래그먼트 생명 주기 중 onActivityCreated 선택
        // onCreateView 다음 단계임
        mAdapter = new ListViewAdapter(Constants.FRAGMENTS.POUCH_LIST, ((MainActivity) getActivity()).mPouchListItems);
        lvPouchList.setAdapter(mAdapter);
        lvPouchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                // TODO : use item data.
                Toast.makeText(getContext(), titleStr, Toast.LENGTH_SHORT).show();
            }
        });

        getPouchListFunc();
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
                    // 서버로부터 새로 받아올 때 기존 아이템들이 있으면 삭제
                    if (mAdapter.getCount() > 0) {
                        mAdapter.clearItems();
                        mAdapter.notifyDataSetChanged();
                    }

                    // 서버로부터 아이템을 받아와 추가
                    JSONArray array = new JSONArray(s);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        mAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_bag2), row.getString("pouch_name"), "발신: "+row.getString("sender")+" 수신: "+row.getString("receiver"));
                    }

                    // 어댑터 갱신
                    mAdapter.notifyDataSetChanged();
                    Log.d("Awesometic", "list in adapter count: " + mAdapter.getCount());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/list");

            http.addAllParameters(maps[0]);

            HttpClient post = http.create();
            post.request();

            if (post.getHttpStatusCode() == 200) {
                String body = post.getBody();

                Log.d("sybaek", body);
                return body;
            } else {
                Log.d("statuscode", String.valueOf(post.getHttpStatusCode()));

                return "ERROR";
            }
        }
    }

}
